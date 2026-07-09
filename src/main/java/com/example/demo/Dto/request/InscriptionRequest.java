package com.example.demo.Dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de requête pour l'inscription d'un nouvel utilisateur.
 * L'authentification dans Likelamba se fait par numéro de téléphone (pas d'email).
 */
@Data
public class InscriptionRequest {

    /** Nom complet de l'utilisateur. */
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    /**
     * Numéro de téléphone Mobile Money de l'utilisateur.
     * Format attendu : +243XXXXXXXXX (format international RDC).
     * Ce numéro sera aussi utilisé pour recevoir les SMS de rappel.
     */
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(
        regexp = "^\\+?[0-9]{9,15}$",
        message = "Format de téléphone invalide. Exemple : +243812345678"
    )
    private String telephone;

    /**
     * Mot de passe en clair (sera hashé avant stockage en base).
     * Minimum 8 caractères. Si non fourni, un mot de passe par défaut (12345678)
     * est attribué à la création.
     */
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String motDePasse;
}
