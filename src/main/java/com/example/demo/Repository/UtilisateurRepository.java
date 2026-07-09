package com.example.demo.Repository;

import com.example.demo.Entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité Utilisateur.
 * L'authentification se fait par numéro de téléphone (pas d'email).
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, UUID> {

    /** Recherche un utilisateur par son numéro de téléphone (identifiant unique de connexion). */
    Optional<Utilisateur> findByTelephone(String telephone);

    /** Vérifie si un numéro de téléphone est déjà enregistré dans le système. */
    boolean existsByTelephone(String telephone);

    /** Recherche un utilisateur par son nom (pour la recherche dans l'interface). */
    Optional<Utilisateur> findByNomIgnoreCase(String nom);
}
