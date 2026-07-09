package com.example.demo.Dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de requête pour créer une nouvelle tontine dans une organisation.
 */
@Data
public class TontineRequest {

    /** Identifiant de l'organisation qui héberge la tontine. */
    @NotNull(message = "L'identifiant de l'organisation est obligatoire")
    private UUID organisationId;

    /** Identifiant de l'agent qui gérera la tontine. */
    @NotNull(message = "L'identifiant de l'agent gestionnaire est obligatoire")
    private UUID agentGestionnaireId;

    /** Nom de la tontine (ex : "Tontine des Femmes du Marché"). */
    @NotBlank(message = "Le nom de la tontine est obligatoire")
    @Size(max = 150, message = "Le nom ne peut pas dépasser 150 caractères")
    private String nom;

    /** Montant de cotisation par tour. Doit être positif. */
    @NotNull(message = "Le montant de cotisation est obligatoire")
    @DecimalMin(value = "1.0", message = "Le montant doit être supérieur à 0")
    private BigDecimal montantCotisation;

    /**
     * Devise utilisée pour les cotisations.
     * Valeurs attendues : CDF, USD.
     */
    @NotBlank(message = "La devise est obligatoire")
    @Size(max = 10, message = "Le code devise ne peut pas dépasser 10 caractères")
    private String devise;
}
