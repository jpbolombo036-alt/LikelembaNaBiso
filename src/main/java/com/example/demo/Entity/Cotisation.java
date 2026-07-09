package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entité JPA représentant une cotisation effectuée par un membre dans le cadre d'un tour de tontine.
 * Une cotisation lie un membre à un tour spécifique et trace le montant attendu, le montant payé
 * ainsi que le statut du paiement.
 */
@Entity
@Table(name = "cotisation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Cotisation {

    /** Identifiant unique de la cotisation (clé primaire, généré automatiquement en UUID). */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_cotisation", updatable = false, nullable = false)
    private UUID idCotisation;

    /** Tour auquel cette cotisation est rattachée (clé étrangère vers TOUR). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    /** Membre de la tontine qui doit effectuer cette cotisation (clé étrangère vers MEMBRE_TONTINE). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membre_tontine_id", nullable = false)
    private MembreTontine membreTontine;

    /**
     * Utilisateur ayant confirmé la réception du paiement (clé étrangère vers UTILISATEUR).
     * Peut être null si la cotisation n'a pas encore été confirmée.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirme_par_id")
    private Utilisateur confirmePar;

    /** Montant que le membre doit payer pour ce tour (en devise de la tontine). */
    @Column(name = "montant_attendu", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantAttendu;

    /**
     * Montant effectivement payé par le membre.
     * Peut être null si le paiement n'a pas encore été effectué.
     */
    @Column(name = "montant_paye", precision = 15, scale = 2)
    private BigDecimal montantPaye;

    /** Date limite à laquelle la cotisation doit être réglée. */
    @Column(name = "date_echeance", nullable = false)
    private LocalDate dateEcheance;

    /**
     * Statut actuel de la cotisation.
     * Valeurs possibles : EN_ATTENTE, PAYE, PARTIEL, RETARD.
     */
    @Column(name = "statut", nullable = false, length = 20)
    private String statut;
}
