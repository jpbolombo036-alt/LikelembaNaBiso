package com.example.demo.Dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ReinitialisationMasseRequest {

    @NotNull(message = "La liste des utilisateurs ne peut pas être vide")
    @Size(min = 1, max = 100, message = "Entre 1 et 100 utilisateurs par lot")
    private List<UUID> utilisateurIds;

    @NotBlank(message = "Le nouveau mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String nouveauMotDePasse;
}
