package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DashboardResponse {

    private String role;
    private String utilisateurNom;
    private String organisationNom;
    private StatsGlobales statsGlobales;
    private StatsTontines statsTontines;
    private StatsCotisations statsCotisations;
    private StatsCredits statsCredits;
    private List<Alerte> alertes;
    private List<ProchaineEcheance> prochainesEcheances;
}
