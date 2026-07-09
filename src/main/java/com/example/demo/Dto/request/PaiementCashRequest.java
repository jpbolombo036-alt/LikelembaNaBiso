package com.example.demo.Dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaiementCashRequest {

    @NotNull(message = "L'identifiant de la cotisation est obligatoire")
    private UUID cotisationId;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "1.0", message = "Le montant doit être supérieur à 0")
    private BigDecimal montant;

    @NotNull(message = "La devise est obligatoire")
    private String devise;

    @Size(max = 255, message = "L'URL de preuve ne peut pas dépasser 255 caractères")
    private String preuveUrl;
}
