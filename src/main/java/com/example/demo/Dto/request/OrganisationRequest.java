package com.example.demo.Dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de requête pour créer ou modifier une organisation.
 * Les annotations de validation garantissent l'intégrité des données avant persistance.
 */
@Data
public class OrganisationRequest {

    /** Nom de l'organisation. Obligatoire, max 150 caractères. */
    @NotBlank(message = "Le nom de l'organisation est obligatoire")
    @Size(max = 150, message = "Le nom ne peut pas dépasser 150 caractères")
    private String nom;

    /**
     * Type de l'organisation.
     * Valeurs attendues : ASSOCIATION, COOPERATIVE, ENTREPRISE, GROUPE_INFORMEL.
     */
    @NotBlank(message = "Le type de l'organisation est obligatoire")
    @Size(max = 50, message = "Le type ne peut pas dépasser 50 caractères")
    private String type;

    /** Ville où est basée l'organisation. */
    @NotBlank(message = "La ville est obligatoire")
    @Size(max = 100, message = "La ville ne peut pas dépasser 100 caractères")
    private String ville;

    /**
     * Statut de l'organisation.
     * Valeurs attendues : ACTIF, INACTIF, SUSPENDU.
     */
    @NotBlank(message = "Le statut est obligatoire")
    private String statut;
}
