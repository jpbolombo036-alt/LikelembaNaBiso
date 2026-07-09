package com.example.demo.Repository;

import com.example.demo.Entity.MembreTontine;
import com.example.demo.Entity.Tontine;
import com.example.demo.Entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité MembreTontine.
 * Gère l'inscription et l'ordre de passage des membres dans une tontine.
 */
@Repository
public interface MembreTontineRepository extends JpaRepository<MembreTontine, UUID> {

    /** Retourne tous les membres inscrits à une tontine, triés par ordre de passage. */
    List<MembreTontine> findByTontineOrderByOrdrePassageAsc(Tontine tontine);

    /** Retourne toutes les tontines auxquelles un utilisateur est inscrit. */
    List<MembreTontine> findByUtilisateur(Utilisateur utilisateur);

    /** Retourne l'inscription d'un utilisateur dans une tontine spécifique. */
    Optional<MembreTontine> findByTontineAndUtilisateur(Tontine tontine, Utilisateur utilisateur);

    /** Vérifie si un utilisateur est déjà inscrit à une tontine. */
    boolean existsByTontineAndUtilisateur(Tontine tontine, Utilisateur utilisateur);

    /** Retourne les membres actifs d'une tontine (statut ACTIF). */
    List<MembreTontine> findByTontineAndStatut(Tontine tontine, String statut);

    /** Compte le nombre de membres actifs dans une tontine. */
    long countByTontineAndStatut(Tontine tontine, String statut);

    /**
     * Retourne le prochain membre à bénéficier de la cagnotte.
     * Recherche le membre avec l'ordre_passage le plus petit qui n'a pas encore reçu son tour.
     */
    @Query("SELECT m FROM MembreTontine m WHERE m.tontine = :tontine AND m.statut = 'ACTIF' ORDER BY m.ordrePassage ASC")
    List<MembreTontine> findMembresActifsOrdonnes(@Param("tontine") Tontine tontine);
}
