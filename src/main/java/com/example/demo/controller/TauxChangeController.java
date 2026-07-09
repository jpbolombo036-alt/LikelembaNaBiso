package com.example.demo.controller;

import com.example.demo.Dto.request.TauxChangeRequest;
import com.example.demo.Dto.response.TauxChangeResponse;
import com.example.demo.Security.JwtService;
import com.example.demo.Service.TauxChangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.List;
import java.util.UUID;

/**
 * Controller REST d'administration des taux de change.
 *
 * Base URL : /api/admin/taux-change
 * Accès réservé aux rôles ADMIN et TRESORIER.
 */
@RestController
@RequestMapping("/api/admin/taux-change")
@RequiredArgsConstructor
@Slf4j
public class TauxChangeController {

    private final TauxChangeService tauxChangeService;
    private final JwtService jwtService;

    /** GET /api/admin/taux-change — Liste les taux de change courants. */
    @GetMapping
    public ResponseEntity<List<TauxChangeResponse>> lister(
            @RequestHeader(name = "Authorization") String authorizationHeader) {
        verifierAccesAdmin(authorizationHeader);
        return ResponseEntity.ok(tauxChangeService.lister());
    }

    /** POST /api/admin/taux-change — Enregistre (saisit/override) un taux de change. */
    @PostMapping
    public ResponseEntity<TauxChangeResponse> enregistrer(
            @Valid @RequestBody TauxChangeRequest request,
            @RequestHeader(name = "Authorization") String authorizationHeader) {
        verifierAccesAdmin(authorizationHeader);
        UUID utilisateurId = extraireUtilisateurId(authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tauxChangeService.enregistrerTaux(request, utilisateurId));
    }

    /** POST /api/admin/taux-change/importer — Déclenche l'import depuis l'API externe. */
    @PostMapping("/importer")
    public ResponseEntity<Void> importer(
            @RequestHeader(name = "Authorization") String authorizationHeader) {
        verifierAccesAdmin(authorizationHeader);
        tauxChangeService.importerDepuisApi();
        return ResponseEntity.accepted().build();
    }

    // ---- Sécurité (rôles ADMIN / TRESORIER) ----

    private void verifierAccesAdmin(String authorizationHeader) {
        String role = extraireRole(authorizationHeader);
        if (!"ADMIN".equals(role) && !"TRESORIER".equals(role)) {
            throw new IllegalAccessError("Accès réservé aux rôles ADMIN ou TRESORIER");
        }
    }

    private UUID extraireUtilisateurId(String authorizationHeader) {
        return jwtService.extraireIdUtilisateur(extraireToken(authorizationHeader));
    }

    private String extraireToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Header Authorization manquant ou invalide");
        }
        return authorizationHeader.substring(7);
    }

    private String extraireRole(String authorizationHeader) {
        String token = extraireToken(authorizationHeader);
        Key signingKey = jwtService.extraireCleSignature();
        return io.jsonwebtoken.Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }
}
