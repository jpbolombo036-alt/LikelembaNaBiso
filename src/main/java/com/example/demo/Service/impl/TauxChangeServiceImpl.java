package com.example.demo.Service.impl;

import com.example.demo.Dto.request.TauxChangeRequest;
import com.example.demo.Dto.response.TauxChangeResponse;
import com.example.demo.Entity.Devise;
import com.example.demo.Entity.TauxChange;
import com.example.demo.Repository.TauxChangeRepository;
import com.example.demo.Service.DeviseService;
import com.example.demo.Service.TauxChangeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des taux de change.
 *
 * Historique : chaque nouveau taux pour une paire clôture le précédent
 * (date_fin renseignée). Le taux "courant" est celui dont date_fin IS NULL.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TauxChangeServiceImpl implements TauxChangeService {

    private final TauxChangeRepository tauxChangeRepository;
    private final DeviseService deviseService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** URL de l'API externe de taux (optionnelle). Si absente, l'import est désactivé. */
    @Value("${app.taux.api-url:}")
    private String apiUrl;

    @Override
    @Transactional(readOnly = true)
    public TauxChangeResponse obtenirTauxActuel(String source, String cible) {
        return tauxChangeRepository
                .findByDeviseSource_CodeAndDeviseCible_CodeAndDateFinIsNull(
                        source.toUpperCase(), cible.toUpperCase())
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalStateException(
                    "Aucun taux de change courant pour " + source + " -> " + cible));
    }

    @Override
    @Transactional
    public TauxChangeResponse enregistrerTaux(TauxChangeRequest request, UUID utilisateurId) {
        Devise source = deviseService.resoudre(request.getDeviseSourceCode());
        Devise cible = deviseService.resoudre(request.getDeviseCibleCode());

        String sourceType = request.getSource() == null ? "MANUAL" : request.getSource().toUpperCase();
        TauxChange sauvegarde = sauvegarderNouveauTaux(
                source, cible, request.getTaux(), sourceType, utilisateurId);

        log.info("Taux enregistré ({}): {} {} -> {} = {}",
                sourceType, source.getCode(), cible.getCode(), request.getTaux());
        return toResponse(sauvegarde);
    }

    @Override
    @Transactional
    public void importerDepuisApi() {
        if (apiUrl == null || apiUrl.isBlank()) {
            log.info("Importation API des taux désactivée (app.taux.api-url non configuré) " +
                    "— anciens taux conservés.");
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                throw new IllegalStateException("Réponse API taux HTTP " + resp.statusCode());
            }

            Map<String, Object> json = objectMapper.readValue(resp.body(), Map.class);
            Map<String, Object> rates = (Map<String, Object>) json.get("rates");
            if (rates == null) {
                throw new IllegalStateException("Champ 'rates' absent de la réponse API");
            }

            // Base de référence = USD
            BigDecimal usdRef = new BigDecimal(rates.get("USD").toString());

            for (Map.Entry<String, Object> entry : rates.entrySet()) {
                String code = entry.getKey();
                if ("USD".equals(code)) {
                    continue;
                }
                Devise source = deviseServiceResolue(code);
                if (source == null) {
                    continue;
                }
                BigDecimal parUsd = new BigDecimal(entry.getValue().toString());
                // Le JSON fournit (units of code) par 1 USD ;
                // on calcule le taux code -> USD = usdRef / parUsd
                BigDecimal taux = usdRef.divide(parUsd, 6, RoundingMode.HALF_UP);
                sauvegarderNouveauTaux(source, deviseService.resoudre("USD"), taux, "API", null);
            }

            log.info("Taux de change importés depuis l'API avec succès ({} devises).", rates.size());
        } catch (Exception e) {
            // Résilience : on garde l'ancien taux en cas d'échec
            log.error("Échec de l'importation des taux depuis l'API. Anciens taux conservés : {}",
                    e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TauxChangeResponse> lister() {
        return tauxChangeRepository.findByDateFinIsNull().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ---- Méthodes utilitaires ----

    /**
     * Sauvegarde un nouveau taux pour une paire, en clôturant au préalable
     * le taux courant existant (date_fin = now).
     */
    private TauxChange sauvegarderNouveauTaux(Devise source, Devise cible, BigDecimal taux,
                                              String sourceType, UUID creePar) {
        tauxChangeRepository
                .findByDeviseSource_CodeAndDeviseCible_CodeAndDateFinIsNull(
                        source.getCode(), cible.getCode())
                .ifPresent(ancien -> {
                    ancien.setDateFin(LocalDateTime.now());
                    tauxChangeRepository.save(ancien);
                });

        TauxChange tauxChange = TauxChange.builder()
                .deviseSource(source)
                .deviseCible(cible)
                .taux(taux)
                .dateDebut(LocalDateTime.now())
                .source(sourceType)
                .creePar(creePar)
                .build();

        return tauxChangeRepository.save(tauxChange);
    }

    /** Résout une devise sans la créer (retourne null si absente). */
    private Devise deviseServiceResolue(String code) {
        try {
            return deviseService.resoudre(code);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private TauxChangeResponse toResponse(TauxChange t) {
        return TauxChangeResponse.builder()
                .id(t.getId())
                .deviseSourceCode(t.getDeviseSource().getCode())
                .deviseCibleCode(t.getDeviseCible().getCode())
                .taux(t.getTaux())
                .dateDebut(t.getDateDebut())
                .dateFin(t.getDateFin())
                .source(t.getSource())
                .creePar(t.getCreePar())
                .build();
    }
}
