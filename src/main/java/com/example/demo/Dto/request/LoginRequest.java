package com.example.demo.Dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO de requête pour la connexion d'un utilisateur.
 * L'authentification se fait uniquement par téléphone + mot de passe.
 * En cas de succès, un token JWT est retourné.
 */
@Data
public class LoginRequest {

    /** Numéro de téléphone servant d'identifiant unique. */
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(
        regexp = "^\\+?[0-9]{9,15}$",
        message = "Format de téléphone invalide"
    )
    private String telephone;

    /** Mot de passe en clair (sera comparé au hash en base). */
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;
}
