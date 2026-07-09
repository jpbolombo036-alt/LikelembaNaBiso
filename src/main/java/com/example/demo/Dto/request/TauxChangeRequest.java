package com.example.demo.Dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO de requête pour enregistrer (saisir/overrider) un taux de change.
 */
@Data
public class TauxChangeRequest {

    /** Code de la devise source. */
    @NotBlank(message = "La devise source est obligatoire")
    private String deviseSourceCode;

    /** Code de la devise cible. */
    @NotBlank(message = "La devise cible est obligatoire")
    private String deviseCibleCode;

    /** Taux de conversion (1 unité source = `taux` unités cible). */
    @NotNull(message = "Le taux est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le taux doit être strictement positif")
    private BigDecimal taux;

    /**
     * Origine du taux (API ou MANUAL).
     * Par défaut MANUAL pour une saisie admin.
     */
    @Size(max = 10, message = "La source ne peut pas dépasser 10 caractères")
    private String source;
}
