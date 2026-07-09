package com.example.demo.Dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de requête pour créer ou mettre à jour une devise de référence.
 */
@Data
public class DeviseRequest {

    /** Code ISO de la devise (ex : CDF, USD). */
    @NotBlank(message = "Le code de la devise est obligatoire")
    @Size(max = 10, message = "Le code ne peut pas dépasser 10 caractères")
    private String code;

    /** Nom complet de la devise. */
    @NotBlank(message = "Le nom de la devise est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    /** Symbole monétaire (optionnel). */
    @Size(max = 10, message = "Le symbole ne peut pas dépasser 10 caractères")
    private String symbole;

    /** Indique si la devise est active. Défaut à true si non fourni. */
    private Boolean actif;
}
