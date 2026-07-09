package com.example.demo.Repository;

import com.example.demo.Entity.MembreOrganisation;
import com.example.demo.Entity.Organisation;
import com.example.demo.Entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité MembreOrganisation.
 * Gère l'appartenance des utilisateurs aux organisations et leurs rôles.
 */
@Repository
public interface MembreOrganisationRepository extends JpaRepository<MembreOrganisation, UUID> {

    /** Retourne tous les membres d'une organisation donnée. */
    List<MembreOrganisation> findByOrganisation(Organisation organisation);

    /** Retourne toutes les organisations auxquelles un utilisateur appartient. */
    List<MembreOrganisation> findByUtilisateur(Utilisateur utilisateur);

    /** Vérifie si un utilisateur est déjà membre d'une organisation. */
    boolean existsByOrganisationAndUtilisateur(Organisation organisation, Utilisateur utilisateur);

    /** Retourne l'enregistrement d'appartenance d'un utilisateur dans une organisation. */
    Optional<MembreOrganisation> findByOrganisationAndUtilisateur(Organisation organisation, Utilisateur utilisateur);

    /** Retourne tous les membres d'une organisation ayant un rôle précis (ex : ADMIN). */
    List<MembreOrganisation> findByOrganisationAndRole(Organisation organisation, String role);

    /** Retourne tous les membres ayant un rôle précis, toutes organisations confondues. */
    List<MembreOrganisation> findByRole(String role);

    /** Compte le nombre de membres dans une organisation. */
    long countByOrganisation(Organisation organisation);
}
