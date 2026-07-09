package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class StatsGlobales {
    private long nombreMembres;
    private long nombreTontinesActives;
    private long nombreTontinesTerminees;
    private BigDecimal montantTotalCotisations;
    private BigDecimal montantTotalRetards;
    private long nombreNotificationsNonLues;
}
