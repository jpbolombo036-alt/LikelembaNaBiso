package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class StatsTontines {
    private long nombreTontines;
    private BigDecimal montantTotalCotisations;
    private BigDecimal montantTotalCredit;
    private long nombreToursPlanifies;
    private long nombreToursEnCours;
}
