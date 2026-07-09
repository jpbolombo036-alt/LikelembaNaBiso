package com.example.demo.Repository;

import com.example.demo.Entity.Cotisation;
import com.example.demo.Entity.Penalite;
import com.example.demo.Entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository JPA pour l'entité Penalite.
 * Gère les amendes générées automatiquement pour les retards de cotisation.
 */
@Repository
public interface PenaliteRepository extends JpaRepository<Penalite, UUID> {

    /** Retourne toutes les pénalités d'un membre. */
    List<Penalite> findByMembre(Utilisateur membre);

    /** Retourne les pénalités d'un membre par statut (ex: EN_ATTENTE). */
    List<Penalite> findByMembreAndStatut(Utilisateur membre, String statut);

    /** Retourne les pénalités liées à une cotisation spécifique. */
    List<Penalite> findByCotisation(Cotisation cotisation);

    /** Retourne toutes les pénalités en attente de paiement. */
    List<Penalite> findByStatut(String statut);

    /** Vérifie si une pénalité existe déjà pour une cotisation (évite les doublons). */
    boolean existsByCotisationAndMotif(Cotisation cotisation, String motif);

    /** Compte les pénalités impayées d'un membre. */
    long countByMembreAndStatut(Utilisateur membre, String statut);

    /**
     * Calcule le montant total des pénalités impayées d'un membre.
     * Utile pour bloquer l'accès au tour suivant si des pénalités sont en attente.
     */
    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM Penalite p WHERE p.membre = :membre AND p.statut = 'EN_ATTENTE'")
    java.math.BigDecimal sumPenalitesEnAttente(@Param("membre") Utilisateur membre);
}
