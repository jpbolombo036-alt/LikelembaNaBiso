package com.example.demo.controller;

import com.example.demo.Dto.request.DeviseRequest;
import com.example.demo.Dto.response.DeviseResponse;
import com.example.demo.Security.JwtService;
import com.example.demo.Service.DeviseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.List;

/**
 * Controller REST d'administration des devises.
 *
 * Base URL : /api/admin/devises
 * Accès réservé aux rôles ADMIN et TRESORIER.
 */
@RestController
@RequestMapping("/api/admin/devises")
@RequiredArgsConstructor
@Slf4j
public class DeviseController {

    private final DeviseService deviseService;
    private final JwtService jwtService;

    /** GET /api/admin/devises — Liste toutes les devises. */
    @GetMapping
    public ResponseEntity<List<DeviseResponse>> lister(
            @RequestHeader(name = "Authorization") String authorizationHeader) {
        verifierAccesAdmin(authorizationHeader);
        return ResponseEntity.ok(deviseService.lister());
    }

    /** POST /api/admin/devises — Crée une devise. */
    @PostMapping
    public ResponseEntity<DeviseResponse> creer(
            @Valid @RequestBody DeviseRequest request,
            @RequestHeader(name = "Authorization") String authorizationHeader) {
        verifierAccesAdmin(authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(deviseService.creer(request));
    }

    /** PATCH /api/admin/devises/{code}/actif?actif=true — Active/désactive une devise. */
    @PatchMapping("/{code}/actif")
    public ResponseEntity<DeviseResponse> changerActif(
            @PathVariable String code,
            @RequestParam boolean actif,
            @RequestHeader(name = "Authorization") String authorizationHeader) {
        verifierAccesAdmin(authorizationHeader);
        return ResponseEntity.ok(deviseService.changerActif(code, actif));
    }

    // ---- Sécurité (rôles ADMIN / TRESORIER) ----

    private void verifierAccesAdmin(String authorizationHeader) {
        String role = extraireRole(authorizationHeader);
        if (!"ADMIN".equals(role) && !"TRESORIER".equals(role)) {
            throw new IllegalAccessError("Accès réservé aux rôles ADMIN ou TRESORIER");
        }
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
