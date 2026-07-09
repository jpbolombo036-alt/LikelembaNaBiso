package com.example.demo.Service;

import com.example.demo.Dto.response.DashboardResponse;
import com.example.demo.Dto.response.DashboardRoleStats;

import java.util.UUID;

public interface DashboardService {

    DashboardRoleStats obtenirDashboard(UUID utilisateurId, String role);
}
