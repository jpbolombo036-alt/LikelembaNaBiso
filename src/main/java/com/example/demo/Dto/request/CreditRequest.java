package com.example.demo.Dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de requête pour accorder un micro-crédit à un membre dans le cadre d'une tontine.
 */
@Data
public class CreditRequest {

    /** Identifiant de la tontine dans laquelle le crédit est accordé. */
    @NotNull(message = "L'identifiant de la tontine est obligatoire")
    private UUID tontineId;

    /** Identifiant de l'utilisateur qui emprunte les fonds. */
    @NotNull(message = "L'identifiant de l'emprunteur est obligatoire")
    private UUID emprunteurId;

    /** Montant principal emprunté (hors intérêts). */
    @NotNull(message = "Le montant principal est obligatoire")
    @DecimalMin(value = "1.0", message = "Le montant doit être supérieur à 0")
    private BigDecimal montantPrincipal;

    /**
     * Taux d'intérêt mensuel en pourcentage.
     * Exemple : 5.00 pour 5%/mois.
     */
    @NotNull(message = "Le taux d'intérêt est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le taux doit être positif")
    private BigDecimal tauxInteretMensuel;

    /**
     * Durée du remboursement en mois.
     * Minimum 1 mois.
     */
    @NotNull(message = "La durée est obligatoire")
    @Min(value = 1, message = "La durée minimale est de 1 mois")
    private Integer dureeMois;

    /** Devise du crédit (CDF ou USD). */
    @NotBlank(message = "La devise est obligatoire")
    private String devise;
}
