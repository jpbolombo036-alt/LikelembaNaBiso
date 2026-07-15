package com.example.demo.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
