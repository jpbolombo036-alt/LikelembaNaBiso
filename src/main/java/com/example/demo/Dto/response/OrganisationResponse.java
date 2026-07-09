package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * DTO de réponse pour l'entité Organisation.
 * Contient uniquement les données sûres à exposer via l'API REST.
 */
@Data
@Builder
public class OrganisationResponse {

    /** Identifiant unique de l'organisation. */
    private UUID idOrganisation;

    /** Nom de l'organisation. */
    private String nom;

    /** Type de l'organisation (ASSOCIATION, COOPERATIVE, etc.). */
    private String type;

    /** Ville de l'organisation. */
    private String ville;

    /** Statut actuel (ACTIF, INACTIF, SUSPENDU). */
    private String statut;
}
