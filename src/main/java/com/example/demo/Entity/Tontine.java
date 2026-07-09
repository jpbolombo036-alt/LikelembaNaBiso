package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entité JPA représentant une tontine organisée au sein d'une organisation.
 * Une tontine est un système d'épargne collectif où les membres cotisent régulièrement
 * et chacun reçoit à tour de rôle la totalité de la cagnotte.
 */
@Entity
@Table(name = "tontine")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Tontine {

    /** Identifiant unique de la tontine (clé primaire UUID). */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_tontine", updatable = false, nullable = false)
    private UUID idTontine;

    /** Organisation qui héberge cette tontine (clé étrangère vers ORGANISATION). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    /**
     * Utilisateur responsable de la gestion de cette tontine (clé étrangère vers UTILISATEUR).
     * L'agent gestionnaire supervise les cotisations et les tours.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_gestionnaire_id", nullable = false)
    private Utilisateur agentGestionnaire;

    /** Nom de la tontine (ex : "Tontine des Femmes du Marché"). */
    @Column(name = "nom", nullable = false, length = 150)
    private String nom;

    /** Montant de la cotisation que chaque membre doit verser à chaque tour. */
    @Column(name = "montant_cotisation", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantCotisation;

    /**
     * Devise utilisée pour les cotisations (référence vers la table devise).
     * Exemples : CDF (Franc Congolais), USD.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devise", nullable = false)
    private Devise devise;

    /**
     * Statut actuel de la tontine.
     * Valeurs possibles : EN_COURS, TERMINEE, SUSPENDUE, ANNULEE.
     */
    @Column(name = "statut", nullable = false, length = 20)
    private String statut;
}
