package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entité JPA représentant une organisation (association, entreprise, groupe informel, etc.).
 * Une organisation est la structure racine qui contient les membres, les abonnements et les tontines.
 */
@Entity
@Table(name = "organisation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Organisation {

    /** Identifiant unique de l'organisation (clé primaire, généré automatiquement en UUID). */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_organisation", updatable = false, nullable = false)
    private UUID idOrganisation;

    /** Nom complet de l'organisation. */
    @Column(name = "nom", nullable = false, length = 150)
    private String nom;

    /**
     * Type de l'organisation.
     * Exemples : ASSOCIATION, COOPERATIVE, ENTREPRISE, GROUPE_INFORMEL.
     */
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    /** Ville dans laquelle l'organisation est basée. */
    @Column(name = "ville", nullable = false, length = 100)
    private String ville;

    /**
     * Statut actuel de l'organisation.
     * Valeurs possibles : ACTIF, INACTIF, SUSPENDU.
     */
    @Column(name = "statut", nullable = false, length = 20)
    private String statut;
}
