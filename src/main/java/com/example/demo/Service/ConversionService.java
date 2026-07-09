package com.example.demo.Service;

import com.example.demo.Dto.response.TauxChangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service utilitaire de conversion de montants entre devises.
 *
 * Utilise le taux de change courant fourni par {@link TauxChangeService}.
 * Arrondit le résultat à 2 décimales (RoundingMode.HALF_UP).
 */
@Service
@RequiredArgsConstructor
public class ConversionService {

    private final TauxChangeService tauxChangeService;

    /**
     * Convertit un montant d'une devise source vers une devise cible.
     *
     * @param montant montant à convertir (non null)
     * @param source  code de la devise source (ex : CDF)
     * @param cible   code de la devise cible (ex : USD)
     * @return montant converti, arrondi à 2 décimales
     * @throws IllegalArgumentException si une devise est manquante ou si aucun taux n'existe
     */
    public BigDecimal convertir(BigDecimal montant, String source, String cible) {
        if (montant == null) {
            throw new IllegalArgumentException("Le montant à convertir est obligatoire");
        }
        if (source == null || cible == null) {
            throw new IllegalArgumentException("La devise source et la devise cible sont obligatoires");
        }
        if (source.equalsIgnoreCase(cible)) {
            return montant.setScale(2, RoundingMode.HALF_UP);
        }

        try {
            // Tentative directe : source -> cible
            TauxChangeResponse direct = tauxChangeService.obtenirTauxActuel(source, cible);
            return montant.multiply(direct.getTaux()).setScale(2, RoundingMode.HALF_UP);
        } catch (IllegalStateException directManquant) {
            // Repli sur le taux inverse : cible -> source
            TauxChangeResponse inverse = tauxChangeService.obtenirTauxActuel(cible, source);
            if (inverse.getTaux().compareTo(BigDecimal.ZERO) == 0) {
                throw new IllegalStateException("Taux de change nul, conversion impossible");
            }
            return montant.divide(inverse.getTaux(), 2, RoundingMode.HALF_UP);
        }
    }
}
