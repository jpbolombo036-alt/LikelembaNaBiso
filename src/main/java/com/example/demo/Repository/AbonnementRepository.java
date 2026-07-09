package com.example.demo.Repository;

import com.example.demo.Entity.Abonnement;
import com.example.demo.Entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité Abonnement.
 * Permet de vérifier les droits d'accès d'une organisation selon son plan actif.
 */
@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, UUID> {

    /** Retourne l'abonnement actif d'une organisation (statut ACTIF). */
    Optional<Abonnement> findByOrganisationAndStatut(Organisation organisation, String statut);

    /** Retourne tous les abonnements d'une organisation (actifs, expirés, etc.). */
    List<Abonnement> findByOrganisation(Organisation organisation);

    /** Retourne tous les abonnements d'un plan donné (ex : PREMIUM). */
    List<Abonnement> findByPlan(String plan);

    /**
     * Retourne les abonnements qui expirent avant une date donnée.
     * Utile pour le Scheduler qui envoie des alertes d'expiration.
     */
    List<Abonnement> findByDateFinBeforeAndStatut(LocalDate date, String statut);

    /** Vérifie si une organisation possède un abonnement actif. */
    boolean existsByOrganisationAndStatut(Organisation organisation, String statut);
}
