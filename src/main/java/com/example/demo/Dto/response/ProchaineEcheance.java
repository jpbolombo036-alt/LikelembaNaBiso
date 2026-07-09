package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ProchaineEcheance {
    private UUID id;
    private String type;
    private String libelle;
    private BigDecimal montant;
    private LocalDate dateEcheance;
    private String statut;
}
