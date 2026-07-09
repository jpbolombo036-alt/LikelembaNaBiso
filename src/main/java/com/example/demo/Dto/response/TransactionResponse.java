package com.example.demo.Dto.response;

import com.example.demo.Enum.ModePaiement;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TransactionResponse {

    private UUID idTransaction;

    private String referenceOperateur;

    private String operateur;

    private ModePaiement modePaiement;

    private BigDecimal montant;

    private String devise;

    private String statut;

    private String messageOperateur;

    private LocalDateTime dateInitiation;

    private LocalDateTime dateConfirmation;
}
