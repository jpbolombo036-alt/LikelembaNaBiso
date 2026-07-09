package com.example.demo.Repository;

import com.example.demo.Entity.Cotisation;
import com.example.demo.Entity.Transaction;
import com.example.demo.Entity.Utilisateur;
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
}
