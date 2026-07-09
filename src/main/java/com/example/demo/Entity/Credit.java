package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA représentant un micro-crédit accordé à un membre dans le cadre d'une tontine.
 *
 * Dans la version "crédit" de Likelamba, certains groupes fonctionnent en micro-crédit rotatif :
 * au lieu de simplement redistribuer la cagnotte, des intérêts sont appliqués sur les sommes
 * empruntées, générant un revenu collectif pour le groupe.
 *
 * Exemple : Un membre emprunte 100 USD avec 5% d'intérêts mensuels sur 3 mois.
 * Il remboursera 105 USD/mois × 3 = 315 USD total.
 */
@Entity
@Table(name = "credit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Credit {

    /** Identifiant unique du crédit (clé primaire UUID). */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_credit", updatable = false, nullable = false)
    private UUID idCredit;

    /**
     * Tontine dans le cadre de laquelle ce crédit est accordé (clé étrangère vers TONTINE).
     * Le crédit est financé par la cagnotte collective du groupe.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tontine_id", nullable = false)
    private Tontine tontine;

    /**
     * Membre bénéficiaire du crédit (clé étrangère vers UTILISATEUR).
     * C'est l'utilisateur qui emprunte les fonds du groupe.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprunteur_id", nullable = false)
    private Utilisateur emprunteur;

    /**
     * Agent ayant approuvé et accordé ce crédit (clé étrangère vers UTILISATEUR).
     * En général le responsable ou le trésorier de la tontine.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approuve_par_id", nullable = false)
    private Utilisateur approvePar;

    /** Montant principal emprunté (hors intérêts). */
    @Column(name = "montant_principal", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantPrincipal;

    /**
     * Taux d'intérêt mensuel appliqué sur le crédit (en pourcentage).
     * Exemple : 5.00 pour 5% par mois.
     */
    @Column(name = "taux_interet_mensuel", nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxInteretMensuel;

    /** Durée totale du remboursement en nombre de mois. */
    @Column(name = "duree_mois", nullable = false)
    private Integer dureeMois;

    /**
     * Montant total à rembourser (principal + intérêts calculés).
     * Calculé automatiquement à la création du crédit.
     */
    @Column(name = "montant_total_du", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantTotalDu;

    /** Montant déjà remboursé par l'emprunteur à ce jour. */
    @Column(name = "montant_rembourse", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal montantRembourse = BigDecimal.ZERO;

    /**
     * Devise du crédit (référence vers la table devise).
     * Exemple : CDF (Franc Congolais), USD.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devise", nullable = false)
    private Devise devise;

    /** Date à laquelle le crédit a été accordé et les fonds débloqués. */
    @Column(name = "date_octroi", nullable = false)
    private LocalDate dateOctroi;

    /** Date prévue pour le remboursement total du crédit. */
    @Column(name = "date_echeance_finale", nullable = false)
    private LocalDate dateEcheanceFinale;

    /**
     * Statut actuel du crédit.
     * Valeurs possibles : EN_ATTENTE, ACTIF, REMBOURSE, EN_RETARD, DEFAUT.
     */
    @Column(name = "statut", nullable = false, length = 20)
    private String statut;

    /** Date et heure de création de l'enregistrement du crédit. */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.montantRembourse == null) {
            this.montantRembourse = BigDecimal.ZERO;
        }
    }
}
