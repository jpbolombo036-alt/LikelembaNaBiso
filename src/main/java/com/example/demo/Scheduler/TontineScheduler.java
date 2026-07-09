package com.example.demo.Scheduler;

import com.example.demo.Entity.Cotisation;
import com.example.demo.Repository.CotisationRepository;
import com.example.demo.Service.NotificationService;
import com.example.demo.Service.PenaliteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

/**
 * Planificateur automatique (Scheduler) pour la gestion du cycle de vie des cotisations.
 * Effectue des tâches récurrentes de fond :
 * - Détection des retards et génération automatique des pénalités (tous les jours à minuit)
 * - Envoi automatique des SMS de rappels (tous les jours à 8h00)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TontineScheduler {

    private final CotisationRepository cotisationRepository;
    private final PenaliteService penaliteService;
    private final NotificationService notificationService;

    // Pénalité journalière par défaut (500 CDF ou 1 USD par jour de retard)
    private static final BigDecimal MONTANT_PENALITE_PAR_JOUR_CDF = BigDecimal.valueOf(500);
    private static final BigDecimal MONTANT_PENALITE_PAR_JOUR_USD = BigDecimal.valueOf(1);

    /**
     * Tâche 1 : Détection quotidienne des cotisations en retard.
     * Exécutée tous les jours à minuit (cron : "0 0 0 * * *").
     *
     * Pour chaque cotisation dont la date d'échéance est dépassée :
     * 1. Change son statut en "RETARD"
     * 2. Génère une amende/pénalité via PenaliteService
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void verifierCotisationsEnRetard() {
        log.info("Lancement de la vérification quotidienne des retards de cotisation...");
        LocalDate aujourdhui = LocalDate.now();

        // Récupérer les cotisations EN_ATTENTE et PARTIEL dont la date d'échéance est passée
        List<Cotisation> cotisationsEnRetard = cotisationRepository
                .findByDateEcheanceBeforeAndStatutIn(aujourdhui, Arrays.asList("EN_ATTENTE", "PARTIEL"));

        log.info("{} cotisation(s) en retard identifiée(s).", cotisationsEnRetard.size());

        for (Cotisation cotisation : cotisationsEnRetard) {
            try {
                // Mettre à jour le statut de la cotisation
                cotisation.setStatut("RETARD");
                cotisationRepository.save(cotisation);

                // Calculer le nombre de jours de retard
                long joursRetard = ChronoUnit.DAYS.between(cotisation.getDateEcheance(), aujourdhui);
                if (joursRetard <= 0) joursRetard = 1; // Sécurité de calcul minimum

                // Choisir le montant de la pénalité par jour selon la devise
                String devise = cotisation.getMembreTontine().getTontine().getDevise().getCode();
                BigDecimal penaliteParJour = "USD".equalsIgnoreCase(devise) 
                        ? MONTANT_PENALITE_PAR_JOUR_USD 
                        : MONTANT_PENALITE_PAR_JOUR_CDF;

                // Générer la pénalité en base de données
                penaliteService.genererPenalite(cotisation, (int) joursRetard, penaliteParJour);

            } catch (Exception e) {
                log.error("Erreur lors de la pénalisation de la cotisation {} : {}", 
                        cotisation.getIdCotisation(), e.getMessage());
            }
        }
        log.info("Fin de la vérification quotidienne des retards.");
    }

    /**
     * Tâche 2 : Envoi quotidien des rappels SMS de cotisation avant échéance.
     * Exécutée tous les jours à 8h00 du matin (cron : "0 0 8 * * *").
     *
     * Identifie les cotisations en cours d'échéance :
     * - Rappel à J-3 (échéance dans exactement 3 jours)
     * - Rappel à J-1 (échéance dans exactement 1 jour)
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void envoyerRappelsSMS() {
        log.info("Lancement de l'envoi des rappels SMS...");
        LocalDate aujourdhui = LocalDate.now();

        // Rappel J-3
        LocalDate echeanceJ3 = aujourdhui.plusDays(3);
        List<Cotisation> cotisationsJ3 = cotisationRepository
                .findByDateEcheanceBetweenAndStatut(echeanceJ3, echeanceJ3, "EN_ATTENTE");
        log.info("{} rappel(s) à envoyer pour échéance J-3.", cotisationsJ3.size());
        for (Cotisation cotisation : cotisationsJ3) {
            notificationService.envoyerRappelCotisation(
                    cotisation.getMembreTontine().getUtilisateur(), 
                    cotisation.getTour(), 
                    3
            );
        }

        // Rappel J-1
        LocalDate echeanceJ1 = aujourdhui.plusDays(1);
        List<Cotisation> cotisationsJ1 = cotisationRepository
                .findByDateEcheanceBetweenAndStatut(echeanceJ1, echeanceJ1, "EN_ATTENTE");
        log.info("{} rappel(s) à envoyer pour échéance J-1.", cotisationsJ1.size());
        for (Cotisation cotisation : cotisationsJ1) {
            notificationService.envoyerRappelCotisation(
                    cotisation.getMembreTontine().getUtilisateur(), 
                    cotisation.getTour(), 
                    1
            );
        }

        log.info("Fin de l'envoi des rappels SMS.");
    }
}
