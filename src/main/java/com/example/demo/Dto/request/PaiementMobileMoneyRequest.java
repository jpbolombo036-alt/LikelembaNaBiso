package com.example.demo.Dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de requête pour initier un paiement Mobile Money.
 * Le membre envoie cette requête pour payer sa cotisation via Airtel Money,
 * Orange Money ou M-Pesa Vodacom.
 */
@Data
public class PaiementMobileMoneyRequest {

    /** Identifiant de la cotisation à régler. */
    @NotNull(message = "L'identifiant de la cotisation est obligatoire")
    private UUID cotisationId;

    /**
     * Opérateur Mobile Money choisi par le membre.
     * Valeurs acceptées : AIRTEL_MONEY, ORANGE_MONEY, MPESA_VODACOM.
     */
    @NotBlank(message = "L'opérateur Mobile Money est obligatoire")
    private String operateur;

    /**
     * Numéro Mobile Money depuis lequel le paiement sera débité.
     * Doit appartenir à l'opérateur sélectionné.
     * Format : +243XXXXXXXXX
     */
    @NotBlank(message = "Le numéro de téléphone Mobile Money est obligatoire")
    @Size(max = 20, message = "Le numéro ne peut pas dépasser 20 caractères")
    private String numeroTelephone;

    /** Montant à envoyer. Doit correspondre au montant attendu de la cotisation. */
    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "1.0", message = "Le montant doit être supérieur à 0")
    private BigDecimal montant;

    /**
     * Devise du paiement.
     * Valeurs attendues : CDF, USD.
     */
    @NotBlank(message = "La devise est obligatoire")
    private String devise;
}
