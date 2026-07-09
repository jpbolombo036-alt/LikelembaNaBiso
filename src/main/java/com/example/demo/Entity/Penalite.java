package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA représentant une pénalité appliquée à un membre en retard de cotisation.
 *
 * Les pénalités sont un mécanisme de discipline financière au sein du groupe.
 * Quand un membre ne paie pas sa cotisation avant l'échéance, une amende est générée
 * automatiquement par le scheduler. Cela incite les membres à respecter les délais
 * et protège la cohésion du groupe.
 *
 * Exemple : Retard de 3 jours → pénalité de 500 CDF par jour = 1 500 CDF d'amende.
 */
@Entity
@Table(name = "penalite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Penalite {

    /** Identifiant unique de la pénalité (clé primaire UUID). */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_penalite", updatable = false, nullable = false)
    private UUID idPenalite;

    /**
     * Cotisation en retard qui a déclenché cette pénalité (clé étrangère vers COTISATION).
     * La pénalité est directement liée à la cotisation non honorée à temps.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cotisation_id", nullable = false)
    private Cotisation cotisation;

    /**
     * Membre pénalisé (clé étrangère vers UTILISATEUR).
     * Déduit directement depuis la cotisation, mais stocké ici pour des requêtes rapides.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membre_id", nullable = false)
    private Utilisateur membre;

    /**
     * Motif de la pénalité.
     * Valeurs possibles : RETARD_COTISATION, ABSENCE_REUNION, NON_PAIEMENT_CREDIT, AUTRE.
     */
    @Column(name = "motif", nullable = false, length = 30)
    private String motif;

    /**
     * Nombre de jours de retard constatés au moment de la génération de la pénalité.
     * Calculé automatiquement par le scheduler.
     */
    @Column(name = "jours_retard", nullable = false)
    private Integer joursRetard;

    /** Montant de l'amende appliquée au membre. */
    @Column(name = "montant", nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    /**
     * Devise de la pénalité (référence vers la table devise).
     * Exemple : CDF (Franc Congolais), USD.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devise", nullable = false)
    private Devise devise;

    /**
     * Statut de la pénalité.
     * Valeurs possibles : EN_ATTENTE, PAYEE, ANNULEE, DISPENSEE.
     * Une pénalité peut être "dispensée" par le responsable dans des cas exceptionnels.
     */
    @Column(name = "statut", nullable = false, length = 15)
    private String statut;

    /** Date à laquelle la pénalité doit être réglée par le membre. */
    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    /** Date et heure à laquelle la pénalité a été générée par le système. */
    @Column(name = "date_generation", updatable = false)
    private LocalDateTime dateGeneration;

    /**
     * Date et heure du paiement effectif de la pénalité.
     * Null si la pénalité n'a pas encore été réglée.
     */
    @Column(name = "date_paiement")
    private LocalDateTime datePaiement;

    @PrePersist
    protected void onCreate() {
        this.dateGeneration = LocalDateTime.now();
    }
}
