package com.example.demo.controller;

import com.example.demo.Dto.request.InscriptionRequest;
import com.example.demo.Dto.request.LoginRequest;
import com.example.demo.Dto.request.RefreshTokenRequest;
import com.example.demo.Dto.request.ChangerMotDePasseRequest;
import com.example.demo.Dto.request.ReinitialiserMotDePasseRequest;
import com.example.demo.Dto.response.LoginResponse;
import com.example.demo.Dto.response.RefreshTokenResponse;
import com.example.demo.Dto.response.UtilisateurResponse;
import com.example.demo.Service.UtilisateurService;
import com.example.demo.Service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST pour l'authentification.
 * Endpoints publics (pas de JWT requis) : inscription et connexion.
 *
 * Base URL : /api/auth
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UtilisateurService utilisateurService;
    private final RefreshTokenService refreshTokenService;
    private final com.example.demo.Security.JwtService jwtService;

    /**
     * POST /api/auth/inscription
     * Crée un nouveau compte utilisateur.
     *
     * @param request Données d'inscription (nom, téléphone, mot de passe)
     * @return 201 CREATED + profil utilisateur créé
     */
    @PostMapping("/inscription")
    public ResponseEntity<UtilisateurResponse> inscrire(@Valid @RequestBody InscriptionRequest request) {
        UtilisateurResponse response = utilisateurService.inscrire(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/auth/connexion
     * Authentifie un utilisateur et retourne un token JWT.
     *
     * @param request Identifiants (téléphone + mot de passe)
     * @return 200 OK + token JWT + profil utilisateur
     */
    @PostMapping("/connexion")
    public ResponseEntity<LoginResponse> connecter(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = utilisateurService.connecter(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/auth/profil/{telephone}
     * Retourne le profil d'un utilisateur par son numéro de téléphone.
     *
     * @param telephone Numéro de téléphone de l'utilisateur
     * @return 200 OK + profil utilisateur
     */
    @GetMapping("/profil/{telephone}")
    public ResponseEntity<UtilisateurResponse> obtenirProfil(@PathVariable String telephone) {
        UtilisateurResponse response = utilisateurService.obtenirParTelephone(telephone);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = refreshTokenService.rafraichir(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(name = "Authorization") String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        refreshTokenService.revokeToken(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/revoke-token")
    public ResponseEntity<Void> revokeToken(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenService.revokeToken(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/changer-mot-de-passe")
    public ResponseEntity<UtilisateurResponse> changerMotDePasse(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @Valid @RequestBody ChangerMotDePasseRequest request) {
        UUID utilisateurId = extraireUtilisateurId(authorizationHeader);
        return ResponseEntity.ok(utilisateurService.changerMotDePasse(utilisateurId, request));
    }

    @PostMapping("/reinitialiser-un")
    public ResponseEntity<Void> reinitialiserUn(@Valid @RequestBody ReinitialiserMotDePasseRequest request) {
        utilisateurService.reinitialiserMotDePasse(request);
        return ResponseEntity.noContent().build();
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Header Authorization manquant ou invalide");
        }
        return authorizationHeader.substring(7);
    }

    private UUID extraireUtilisateurId(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        return jwtService.extraireIdUtilisateur(token);
    }
}
