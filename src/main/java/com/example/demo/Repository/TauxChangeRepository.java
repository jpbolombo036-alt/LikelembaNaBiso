package com.example.demo.Repository;

import com.example.demo.Entity.TauxChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité TauxChange.
 *
 * Le taux "courant" d'une paire est celui dont la date de fin est nulle.
 */
@Repository
public interface TauxChangeRepository extends JpaRepository<TauxChange, UUID> {

    /**
     * Retourne le taux courant (date_fin IS NULL) pour une paire de devises.
     */
    Optional<TauxChange> findByDeviseSource_CodeAndDeviseCible_CodeAndDateFinIsNull(
            String deviseSource, String deviseCible);

    /** Retourne tous les taux encore courants (date_fin IS NULL). */
    List<TauxChange> findByDateFinIsNull();
}
