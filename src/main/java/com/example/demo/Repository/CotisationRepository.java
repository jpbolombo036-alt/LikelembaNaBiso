package com.example.demo.Repository;

import com.example.demo.Entity.Cotisation;
import com.example.demo.Entity.MembreTontine;
import com.example.demo.Entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité Cotisation.
 * Cœur du système : gère les paiements attendus et effectués par chaque membre.
 */
@Repository
public interface CotisationRepository extends JpaRepository<Cotisation, UUID> {

    /** Retourne toutes les cotisations d'un tour donné. */
    List<Cotisation> findByTour(Tour tour);

    /** Retourne toutes les cotisations d'un membre de tontine. */
    List<Cotisation> findByMembreTontine(MembreTontine membreTontine);

    /** Retourne la cotisation d'un membre pour un tour précis. */
    Optional<Cotisation> findByTourAndMembreTontine(Tour tour, MembreTontine membreTontine);

    /** Retourne les cotisations par statut (ex: EN_ATTENTE, RETARD). */
    List<Cotisation> findByStatut(String statut);

    /**
     * Retourne les cotisations en retard : date d'échéance dépassée et non payées.
     * Utilisé par le Scheduler pour générer les pénalités automatiquement.
     */
    List<Cotisation> findByDateEcheanceBeforeAndStatutIn(LocalDate date, List<String> statuts);

    /**
     * Retourne les cotisations dont l'échéance approche (pour les rappels SMS).
     * Exemple : cotisations dues dans les 3 prochains jours.
     */
    List<Cotisation> findByDateEcheanceBetweenAndStatut(LocalDate debut, LocalDate fin, String statut);

    /** Vérifie si toutes les cotisations d'un tour sont payées (pour clôturer le tour). */
    boolean existsByTourAndStatutNot(Tour tour, String statut);

    /**
     * Calcule le montant total collecté pour un tour donné.
     * Retourne la somme des montants payés pour toutes les cotisations du tour.
     */
    @Query("SELECT COALESCE(SUM(c.montantPaye), 0) FROM Cotisation c WHERE c.tour = :tour AND c.statut = 'PAYE'")
    java.math.BigDecimal sumMontantPayeByTour(@Param("tour") Tour tour);

    /** Compte les cotisations non payées d'un tour (pour la relance). */
    long countByTourAndStatutIn(Tour tour, List<String> statuts);
}
