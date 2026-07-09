package com.example.demo.Scheduler;

import com.example.demo.Service.TauxChangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Planificateur d'importation automatique des taux de change.
 *
 * Exécuté quotidiennement (01h00) : appelle {@link TauxChangeService#importerDepuisApi()}.
 * En cas d'échec de l'API externe, l'ancien taux est conservé (résilience).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TauxChangeScheduler {

    private final TauxChangeService tauxChangeService;

    @Scheduled(cron = "0 0 1 * * *")
    public void importerTauxQuotidiennement() {
        log.info("Importation quotidienne des taux de change (planifiée)...");
        try {
            tauxChangeService.importerDepuisApi();
        } catch (Exception e) {
            log.error("Erreur lors de l'importation planifiée des taux de change : {}", e.getMessage());
        }
    }
}
