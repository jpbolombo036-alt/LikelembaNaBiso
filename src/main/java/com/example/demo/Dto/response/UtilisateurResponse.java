package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * DTO de réponse pour l'entité Utilisateur.
 * N'expose JAMAIS le mot de passe hashé, même sous forme hashée.
 */
@Data
@Builder
public class UtilisateurResponse {

    /** Identifiant unique de l'utilisateur. */
    private UUID idUtilisateur;

    /** Nom complet de l'utilisateur. */
    private String nom;

    /** Numéro de téléphone (masqué partiellement pour la sécurité si nécessaire). */
    private String telephone;

    /** Indique si le compte utilisateur est actif. */
    private boolean actif;
}
