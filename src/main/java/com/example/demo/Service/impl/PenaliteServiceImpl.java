package com.example.demo.Service.impl;

import com.example.demo.Dto.response.PenaliteResponse;
import com.example.demo.Entity.Cotisation;
import com.example.demo.Entity.Penalite;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Repository.PenaliteRepository;
import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.Service.NotificationService;
import com.example.demo.Service.PenaliteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des pénalités.
 *
 * Les pénalités sont générées automatiquement par le Scheduler
 * (voir TontineScheduler) lorsqu'une cotisation dépasse son échéance.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PenaliteServiceImpl implements PenaliteService {

    private final PenaliteRepository penaliteRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PenaliteResponse genererPenalite(Cotisation cotisation, int joursRetard, BigDecimal montantParJour) {
        Utilisateur membre = cotisation.getMembreTontine().getUtilisateur();

        // Protection anti-doublon : ne pas générer deux fois la même pénalité
        if (penaliteRepository.existsByCotisationAndMotif(cotisation, "RETARD_COTISATION")) {
            throw new IllegalStateException(
                "Une pénalité RETARD_COTISATION existe déjà pour cette cotisation"
            );
        }

        // Calcul du montant total de la pénalité
        BigDecimal montantTotal = montantParJour.multiply(BigDecimal.valueOf(joursRetard));

        Penalite penalite = Penalite.builder()
                .cotisation(cotisation)
                .membre(membre)
                .motif("RETARD_COTISATION")
                .joursRetard(joursRetard)
                .montant(montantTotal)
                .devise(cotisation.getMembreTontine().getTontine().getDevise())
                .statut("EN_ATTENTE")
                .build();

        Penalite saved = penaliteRepository.save(penalite);

        // Alerte SMS au membre
        notificationService.alerterRetardCotisation(membre, cotisation.getTour());

        log.warn("Pénalité générée : {} jours de retard → {} {} pour {}",
                joursRetard, montantTotal,
                cotisation.getMembreTontine().getTontine().getDevise().getCode(),
                membre.getNom());

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PenaliteResponse> listerParMembre(UUID idUtilisateur) {
        Utilisateur membre = trouverUtilisateur(idUtilisateur);
        return penaliteRepository.findByMembre(membre)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PenaliteResponse marquerPayee(UUID idPenalite) {
        Penalite penalite = trouverOuEchouer(idPenalite);

        if ("PAYEE".equals(penalite.getStatut())) {
            throw new IllegalStateException("Cette pénalité est déjà marquée comme payée");
        }

        penalite.setStatut("PAYEE");
        penalite.setDatePaiement(LocalDateTime.now());
        Penalite saved = penaliteRepository.save(penalite);

        log.info("Pénalité {} marquée comme PAYEE", idPenalite);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public PenaliteResponse dispenser(UUID idPenalite) {
        Penalite penalite = trouverOuEchouer(idPenalite);

        penalite.setStatut("DISPENSEE");
        Penalite saved = penaliteRepository.save(penalite);

        log.info("Pénalité {} dispensée par le gestionnaire", idPenalite);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean aPenalitesEnAttente(UUID idUtilisateur) {
        Utilisateur membre = trouverUtilisateur(idUtilisateur);
        return penaliteRepository.countByMembreAndStatut(membre, "EN_ATTENTE") > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculerTotalPenalitesEnAttente(UUID idUtilisateur) {
        Utilisateur membre = trouverUtilisateur(idUtilisateur);
        return penaliteRepository.sumPenalitesEnAttente(membre);
    }

    // ---- Méthodes utilitaires ----

    private Penalite trouverOuEchouer(UUID id) {
        return penaliteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pénalité introuvable : " + id));
    }

    private Utilisateur trouverUtilisateur(UUID id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + id));
    }

    private PenaliteResponse toResponse(Penalite p) {
        return PenaliteResponse.builder()
                .idPenalite(p.getIdPenalite())
                .nomMembre(p.getMembre().getNom())
                .motif(p.getMotif())
                .joursRetard(p.getJoursRetard())
                .montant(p.getMontant())
                .devise(p.getDevise().getCode())
                .statut(p.getStatut())
                .dateGeneration(p.getDateGeneration())
                .build();
    }
}
