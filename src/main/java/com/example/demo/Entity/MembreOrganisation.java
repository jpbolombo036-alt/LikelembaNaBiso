package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entité JPA représentant l'appartenance d'un utilisateur à une organisation.
 * Cette table de liaison définit le rôle de l'utilisateur au sein de l'organisation
 * et la date à laquelle il a rejoint.
 */
@Entity
@Table(name = "membre_organisation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MembreOrganisation {

    /** Identifiant unique du membre dans l'organisation (clé primaire UUID). */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_membre_organisation", updatable = false, nullable = false)
    private UUID idMembreOrganisation;

    /** Organisation à laquelle appartient ce membre (clé étrangère vers ORGANISATION). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    /** Utilisateur membre de l'organisation (clé étrangère vers UTILISATEUR). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Utilisateur utilisateur;

    /**
     * Rôle de l'utilisateur au sein de l'organisation.
     * Exemples : ADMIN, MEMBRE, TRESORIER, SECRETAIRE.
     */
    @Column(name = "role", nullable = false, length = 30)
    private String role;

    /** Date à laquelle l'utilisateur a rejoint l'organisation. */
    @Column(name = "date_ajout", nullable = false)
    private LocalDate dateAjout;
}
