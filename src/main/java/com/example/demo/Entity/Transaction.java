package com.example.demo.Entity;

import com.example.demo.Enum.ModePaiement;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA représentant une transaction Mobile Money liée à une cotisation.
 *
 * Chaque tentative de paiement via Airtel Money, Orange Money ou M-Pesa Vodacom
 * génère une transaction tracée ici. Cela permet d'avoir une preuve de paiement
 * indépendante de l'opérateur, d'éviter les litiges et de réconcilier les paiements.
 *
 * Flux typique :
 *   1. Le membre initie un paiement → transaction créée en statut INITIEE
 *   2. L'API Mobile Money répond → statut mis à jour (REUSSIE / ECHOUEE)
 *   3. La cotisation correspondante est mise à jour automatiquement
 */
@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Transaction {

    /** Identifiant unique de la transaction (clé primaire UUID). */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_transaction", updatable = false, nullable = false)
    private UUID idTransaction;

    /**
     * Cotisation que ce paiement est censé régler (clé étrangère vers COTISATION).
     * Une cotisation peut avoir plusieurs tentatives de transaction (ex : première tentative échouée).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cotisation_id", nullable = false)
    private Cotisation cotisation;

    /**
     * Utilisateur qui a initié le paiement (clé étrangère vers UTILISATEUR).
     * En général le membre lui-même, mais peut être l'agent gestionnaire en son nom.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payeur_id", nullable = false)
    private Utilisateur payeur;

    /**
     * Opérateur Mobile Money utilisé pour ce paiement.
     * Valeurs possibles : AIRTEL_MONEY, ORANGE_MONEY, MPESA_VODACOM, CASH.
     */
    @Column(name = "operateur", nullable = false, length = 20)
    private String operateur;

    /** Montant effectivement envoyé lors de cette transaction. */
    @Column(name = "montant", nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    /**
     * Devise du montant transféré (référence vers la table devise).
     * Exemple : CDF (Franc Congolais), USD.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devise", nullable = false)
    private Devise devise;

    /**
     * Mode de paiement utilisé pour cette transaction.
     * Valeurs possibles : MOBILE_MONEY, CASH.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false, length = 20)
    private ModePaiement modePaiement;

    /**
     * Référence unique retournée par l'opérateur Mobile Money ou l'agrégateur (CinetPay/Flutterwave).
     * Sert à réconcilier et vérifier le paiement côté opérateur.
     * Nullable pour les paiements cash.
     */
    @Column(name = "reference_operateur", unique = true, length = 100)
    private String referenceOperateur;

    /**
     * Numéro de téléphone Mobile Money depuis lequel le paiement a été effectué.
     * Nullable pour les paiements cash.
     * Exemple : +243812345678
     */
    @Column(name = "numero_telephone_payeur", length = 20)
    private String numeroTelephonePayeur;

    /**
     * Statut actuel de la transaction.
     * Valeurs possibles : INITIEE, EN_ATTENTE, REUSSIE, ECHOUEE, ANNULEE, REMBOURSEE.
     */
    @Column(name = "statut", nullable = false, length = 20)
    private String statut;

    /**
     * Message de retour fourni par l'opérateur Mobile Money.
     * Utile pour diagnostiquer les échecs (ex : "Solde insuffisant", "Numéro invalide").
     */
    @Column(name = "message_operateur", length = 255)
    private String messageOperateur;

    /** Date et heure exactes de l'initiation de la transaction. */
    @Column(name = "date_initiation", nullable = false)
    private LocalDateTime dateInitiation;

    /**
     * Date et heure de la confirmation finale par l'opérateur.
     * Null si la transaction est encore en attente ou a échoué.
     */
    @Column(name = "date_confirmation")
    private LocalDateTime dateConfirmation;

    @PrePersist
    protected void onCreate() {
        this.dateInitiation = LocalDateTime.now();
    }
}
