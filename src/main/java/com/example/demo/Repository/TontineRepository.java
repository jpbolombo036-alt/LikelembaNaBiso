package com.example.demo.Repository;

import com.example.demo.Entity.Organisation;
import com.example.demo.Entity.Tontine;
import com.example.demo.Entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository JPA pour l'entité Tontine.
 * Gère les cycles d'épargne collective au sein des organisations.
 */
@Repository
public interface TontineRepository extends JpaRepository<Tontine, UUID> {

    /** Retourne toutes les tontines d'une organisation. */
    List<Tontine> findByOrganisation(Organisation organisation);

    /** Retourne les tontines actives (EN_COURS) d'une organisation. */
    List<Tontine> findByOrganisationAndStatut(Organisation organisation, String statut);

    /** Retourne toutes les tontines gérées par un agent gestionnaire donné. */
    List<Tontine> findByAgentGestionnaire(Utilisateur agentGestionnaire);

    /** Retourne les tontines par statut global (ex: toutes les tontines EN_COURS). */
    List<Tontine> findByStatut(String statut);

    /** Recherche une tontine par son nom dans une organisation. */
    List<Tontine> findByOrganisationAndNomContainingIgnoreCase(Organisation organisation, String nom);

    /** Compte les tontines actives d'une organisation. */
    long countByOrganisationAndStatut(Organisation organisation, String statut);
}
