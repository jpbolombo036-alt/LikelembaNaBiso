package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entité JPA représentant un abonnement souscrit par une organisation à un plan de service.
 * Un abonnement détermine les fonctionnalités disponibles pour l'organisation
 * ainsi que la période de validité du contrat.
 */
@Entity
@Table(name = "abonnement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Abonnement {

    /** Identifiant unique de l'abonnement (clé primaire UUID). */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_abonnement", updatable = false, nullable = false)
    private UUID idAbonnement;

    /** Organisation titulaire de cet abonnement (clé étrangère vers ORGANISATION). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    /**
     * Plan d'abonnement choisi par l'organisation.
     * Exemples : GRATUIT, STANDARD, PREMIUM.
     */
    @Column(name = "plan", nullable = false, length = 30)
    private String plan;

    /** Date de début de validité de l'abonnement. */
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    /**
     * Date de fin de validité de l'abonnement.
     * Peut être null pour un abonnement sans date d'expiration.
     */
    @Column(name = "date_fin")
    private LocalDate dateFin;

    /**
     * Statut actuel de l'abonnement.
     * Valeurs possibles : ACTIF, EXPIRE, SUSPENDU, ANNULE.
     */
    @Column(name = "statut", nullable = false, length = 20)
    private String statut;
}
