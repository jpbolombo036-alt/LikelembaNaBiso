package com.example.demo.controller;

import com.example.demo.Dto.response.DashboardRoleStats;
import com.example.demo.Dto.response.DashboardResponse;
import com.example.demo.Service.DashboardService;
import com.example.demo.Security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardRoleStats> obtenirDashboard(
            @RequestHeader(name = "Authorization") String authorizationHeader) {
        UUID utilisateurId = jwtService.extraireIdUtilisateur(extractToken(authorizationHeader));
        String role = extraireRole(authorizationHeader);
        return ResponseEntity.ok(dashboardService.obtenirDashboard(utilisateurId, role));
    }

    @GetMapping("/public")
    public ResponseEntity<com.example.demo.Dto.response.DashboardResponse> obtenirDashboardPublic() {
        com.example.demo.Dto.response.DashboardResponse response = new com.example.demo.Dto.response.DashboardResponse();
        response.setRole("PUBLIC");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> listerRolesDisponibles() {
        return ResponseEntity.ok(List.of("ADMIN", "TRESORIER", "MEMBRE", "SECRETAIRE", "AGENT_GESTIONNAIRE"));
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Header Authorization manquant ou invalide");
        }
        return authorizationHeader.substring(7);
    }

    private String extraireRole(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        Key signingKey = jwtService.extraireCleSignature();
        return io.jsonwebtoken.Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }
}
