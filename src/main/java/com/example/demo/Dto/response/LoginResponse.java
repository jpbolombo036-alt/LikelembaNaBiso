package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO de réponse après une connexion réussie.
 * Contient le token JWT à utiliser dans les requêtes suivantes
 * via le header : Authorization: Bearer {token}
 */
@Data
@Builder
public class LoginResponse {

    /** Token JWT à inclure dans chaque requête authentifiée. */
    private String token;

    /** Type du token (toujours "Bearer"). */
    @Builder.Default
    private String type = "Bearer";

    /** Informations de base de l'utilisateur connecté. */
    private UtilisateurResponse utilisateur;

    /** Durée de validité du token en secondes (ex: 86400 = 24h). */
    private long expiresIn;

    /** Refresh token permettant d'obtenir un nouveau token JWT sans se reconnecter. */
    private String refreshToken;

    /**
     * Indique si l'utilisateur doit changer son mot de passe (ex : mot de passe par défaut
     * attribué à la création). Le client doit rediriger vers le changement de mot de passe.
     */
    private boolean doitChangerMotDePasse;
}
