package com.example.demo.Dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ConfirmationCashRequest {

    @NotNull(message = "L'identifiant de la transaction est obligatoire")
    private UUID transactionId;

    @NotNull(message = "Le champ accepter est obligatoire")
    private Boolean accepter;

    private String commentaire;
}
