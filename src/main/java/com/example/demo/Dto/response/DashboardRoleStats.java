package com.example.demo.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DashboardRoleStats {

    private String role;
    private String utilisateurNom;
    private String organisationNom;
    private StatsGlobales statsGlobales;
    private StatsTontines statsTontines;
    private StatsCotisations statsCotisations;
    private StatsCredits statsCredits;
    private java.util.List<Alerte> alertes;
    private java.util.List<ProchaineEcheance> prochainesEcheances;
}
