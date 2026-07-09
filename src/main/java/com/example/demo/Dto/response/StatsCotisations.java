package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class StatsCotisations {
    private BigDecimal montantAttendu;
    private BigDecimal montantPaye;
    private BigDecimal montantRetard;
    private long nombreEnAttente;
    private long nombrePayees;
    private long nombrePartielles;
    private long nombreRetards;
}
