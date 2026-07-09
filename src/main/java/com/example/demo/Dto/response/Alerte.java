package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class Alerte {
    private String type;
    private String message;
    private String severite;
    private LocalDate date;
    private BigDecimal montant;
}
