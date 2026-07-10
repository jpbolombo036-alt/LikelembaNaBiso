package com.example.demo.Repository;

import com.example.demo.Entity.Cotisation;
import com.example.demo.Entity.Transaction;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Enum.ModePaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité Transaction.
 * Trace tous les paiements Mobile Money (Airtel, Orange, M-Pesa).
 * Sert à la réconciliation financière et à la preuve de paiement.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    /** Retourne toutes les transactions liées à une cotisation. */
    List<Transaction> findByCotisation(Cotisation cotisation);

    /** Retourne toutes les transactions initiées par un payeur. */
    List<Transaction> findByPayeur(Utilisateur payeur);

    /**
     * Recherche une transaction par sa référence opérateur Mobile Money.
     * La référence est unique : elle sert à réconcilier avec les données de l'opérateur.
     */
    Optional<Transaction> findByReferenceOperateur(String referenceOperateur);

    /** Retourne les transactions par statut (ex: EN_ATTENTE, REUSSIE, ECHOUEE). */
    List<Transaction> findByStatut(String statut);

    /** Retourne les transactions par opérateur (ex: AIRTEL_MONEY, ORANGE_MONEY). */
    List<Transaction> findByOperateur(String operateur);

    /** Retourne les transactions réussies d'une cotisation (pour éviter les doublons). */
    List<Transaction> findByCotisationAndStatut(Cotisation cotisation, String statut);

    /** Vérifie si une cotisation a déjà une transaction réussie (paiement confirmé). */
    boolean existsByCotisationAndStatut(Cotisation cotisation, String statut);

    /** Retourne les transactions en attente de confirmation (pour les vérifier auprès de l'opérateur). */
    List<Transaction> findByStatutOrderByDateInitiationAsc(String statut);

    /**
     * Retourne toutes les transactions d'un mode de paiement donné (ex: CASH)
     * portant sur les tontines gérées par un agent gestionnaire donné.
     * Traverse la relation transaction -> cotisation -> tour -> tontine -> agentGestionnaire.
     * Sert au dashboard cash de l'agent, en évitant de charger toutes les transactions en mémoire.
     */
    List<Transaction> findByModePaiementAndCotisation_Tour_Tontine_AgentGestionnaire(
            ModePaiement modePaiement, Utilisateur agentGestionnaire);

    /**
     * Retourne les déclarations cash d'un statut donné (ex: INITIEE) pour les tontines
     * gérées par un agent gestionnaire, triées par date d'initiation croissante.
     */
    List<Transaction> findByStatutAndModePaiementAndCotisation_Tour_Tontine_AgentGestionnaireOrderByDateInitiationAsc(
            String statut, ModePaiement modePaiement, Utilisateur agentGestionnaire);

    /**
     * Retourne les déclarations cash d'un statut donné (ex: INITIEE) pour une tontine précise,
     * triées par date d'initiation croissante. Remplace le filtrage en mémoire.
     */
    List<Transaction> findByStatutAndModePaiementAndCotisation_Tour_Tontine_IdTontineOrderByDateInitiationAsc(
            String statut, ModePaiement modePaiement, UUID tontineId);
}
