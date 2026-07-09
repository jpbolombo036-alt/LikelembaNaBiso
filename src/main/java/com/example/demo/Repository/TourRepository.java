package com.example.demo.Repository;

import com.example.demo.Entity.Tontine;
import com.example.demo.Entity.Tour;
import com.example.demo.Entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité Tour.
 * Gère les cycles de collecte et l'attribution de la cagnotte à chaque bénéficiaire.
 */
@Repository
public interface TourRepository extends JpaRepository<Tour, UUID> {

    /** Retourne tous les tours d'une tontine, triés par numéro de tour croissant. */
    List<Tour> findByTontineOrderByNumeroTourAsc(Tontine tontine);

    /** Retourne les tours d'une tontine par statut (ex: PLANIFIE, EN_COURS). */
    List<Tour> findByTontineAndStatut(Tontine tontine, String statut);

    /** Retourne le tour actuel en cours d'une tontine (statut EN_COURS). */
    Optional<Tour> findByTontineAndStatut_First(Tontine tontine, String statut);

    /** Retourne un tour par son numéro dans une tontine. */
    Optional<Tour> findByTontineAndNumeroTour(Tontine tontine, Integer numeroTour);

    /** Retourne tous les tours dont un utilisateur est le bénéficiaire. */
    List<Tour> findByBeneficiaire(Utilisateur beneficiaire);

    /**
     * Retourne les tours dont la date prévue est passée mais qui ne sont pas encore terminés.
     * Utilisé par le Scheduler pour détecter les tours en retard.
     */
    List<Tour> findByDatePrevueBeforeAndStatutNot(LocalDate date, String statut);

    /**
     * Retourne les tours à venir dans les N prochains jours.
     * Utilisé par le Scheduler pour envoyer des rappels SMS aux membres.
     */
    List<Tour> findByDatePrevueBetweenAndStatut(LocalDate debut, LocalDate fin, String statut);

    /** Compte le nombre total de tours dans une tontine. */
    long countByTontine(Tontine tontine);
}
