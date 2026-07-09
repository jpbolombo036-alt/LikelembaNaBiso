package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class StatsCredits {
    private long nombreCreditsActifs;
    private BigDecimal montantTotalPrincipal;
    private BigDecimal montantTotalRembourse;
    private BigDecimal montantTotalDu;
    private long nombreCreditsEnRetard;
}
