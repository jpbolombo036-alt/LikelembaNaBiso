package com.example.demo.Repository;

import com.example.demo.Entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité Organisation.
 * Fournit les opérations CRUD de base et les requêtes métier spécifiques.
 */
@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, UUID> {

    /** Recherche une organisation par son nom exact (sensible à la casse). */
    Optional<Organisation> findByNom(String nom);

    /** Recherche toutes les organisations d'un statut donné (ex: ACTIF). */
    List<Organisation> findByStatut(String statut);

    /** Recherche toutes les organisations d'une ville donnée. */
    List<Organisation> findByVille(String ville);

    /** Recherche toutes les organisations d'un type donné (ex: ASSOCIATION). */
    List<Organisation> findByType(String type);

    /** Recherche par ville et statut combinés. */
    List<Organisation> findByVilleAndStatut(String ville, String statut);

    /** Vérifie si une organisation avec ce nom existe déjà. */
    boolean existsByNom(String nom);
}
