package com.example.demo.Service;

import com.example.demo.Dto.response.CashDashboardResponse;
import com.example.demo.Dto.response.DashboardResponse;
import com.example.demo.Dto.response.DashboardRoleStats;

import java.util.UUID;

public interface DashboardService {

    DashboardRoleStats obtenirDashboard(UUID utilisateurId, String role);

    /**
     * Construit le dashboard cash de l'agent gestionnaire, agrégé sur toutes
     * les tontines qu'il gère. Lève une erreur si l'utilisateur ne gère aucune tontine.
     */
    CashDashboardResponse obtenirDashboardCashAgent(UUID agentId);
}
