package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entité JPA représentant un tour de cotisation au sein d'une tontine.
 * Chaque tour désigne un cycle de collecte où un bénéficiaire est désigné pour
 * recevoir la cagnotte constituée des cotisations de tous les membres.
 */
@Entity
@Table(name = "tour")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Tour {

    /** Identifiant unique du tour (clé primaire UUID). */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_tour", updatable = false, nullable = false)
    private UUID idTour;

    /** Tontine à laquelle appartient ce tour (clé étrangère vers TONTINE). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tontine_id", nullable = false)
    private Tontine tontine;

    /**
     * Utilisateur qui bénéficie de la cagnotte à ce tour (clé étrangère vers UTILISATEUR).
     * C'est le membre désigné pour recevoir l'ensemble des cotisations collectées.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiaire_id", nullable = false)
    private Utilisateur beneficiaire;

    /** Numéro séquentiel du tour au sein de la tontine (commence à 1). */
    @Column(name = "numero_tour", nullable = false)
    private Integer numeroTour;

    /** Date prévue pour la collecte et la remise de la cagnotte au bénéficiaire. */
    @Column(name = "date_prevue", nullable = false)
    private LocalDate datePrevue;

    /**
     * Statut actuel du tour.
     * Valeurs possibles : PLANIFIE, EN_COURS, TERMINE, REPORTE, ANNULE.
     */
    @Column(name = "statut", nullable = false, length = 20)
    private String statut;
}
