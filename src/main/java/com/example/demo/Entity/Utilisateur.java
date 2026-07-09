package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entité JPA représentant un utilisateur de l'application.
 * Un utilisateur peut appartenir à plusieurs organisations et participer à plusieurs tontines.
 */
@Entity
@Table(name = "utilisateur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "motDePasseHash")
public class Utilisateur {

    /** Identifiant unique de l'utilisateur (clé primaire, généré automatiquement en UUID). */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_utilisateur", updatable = false, nullable = false)
    private UUID idUtilisateur;

    /** Numéro de téléphone de l'utilisateur. Doit être unique dans le système. */
    @Column(name = "telephone", nullable = false, unique = true, length = 20)
    private String telephone;

    /**
     * Hash du mot de passe de l'utilisateur.
     * Ne jamais stocker le mot de passe en clair. Utiliser BCrypt ou Argon2.
     * Ce champ est exclu du toString() pour des raisons de sécurité.
     */
    @Column(name = "mot_de_passe_hash", nullable = false)
    private String motDePasseHash;

    /** Nom complet de l'utilisateur. */
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    /** Indique si le compte utilisateur est actif. */
    @Builder.Default
    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    /**
     * Drapeau imposant le changement du mot de passe au prochain login.
     * Positionné à true quand un mot de passe par défaut est attribué à la création.
     */
    @Builder.Default
    @Column(name = "doit_changer_mot_de_passe", nullable = false)
    private boolean doitChangerMotDePasse = false;
}
