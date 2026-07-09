package com.example.demo.Repository;

import com.example.demo.Entity.Credit;
import com.example.demo.Entity.Tontine;
import com.example.demo.Entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository JPA pour l'entité Credit.
 * Gère les micro-crédits rotatifs accordés aux membres dans le cadre d'une tontine.
 */
@Repository
public interface CreditRepository extends JpaRepository<Credit, UUID> {

    /** Retourne tous les crédits d'une tontine. */
    List<Credit> findByTontine(Tontine tontine);

    /** Retourne tous les crédits d'un emprunteur. */
    List<Credit> findByEmprunteur(Utilisateur emprunteur);

    /** Retourne les crédits d'un emprunteur dans une tontine. */
    List<Credit> findByTontineAndEmprunteur(Tontine tontine, Utilisateur emprunteur);

    /** Retourne les crédits par statut (ex: ACTIF, EN_RETARD). */
    List<Credit> findByStatut(String statut);

    /** Retourne les crédits actifs d'une tontine. */
    List<Credit> findByTontineAndStatut(Tontine tontine, String statut);

    /**
     * Retourne les crédits dont l'échéance finale est dépassée et qui sont encore actifs.
     * Utilisé par le Scheduler pour détecter les crédits en défaut.
     */
    List<Credit> findByDateEcheanceFinaleBeforeAndStatut(LocalDate date, String statut);

    /** Vérifie si un emprunteur a déjà un crédit actif dans une tontine (un seul à la fois). */
    boolean existsByTontineAndEmprunteurAndStatutIn(Tontine tontine, Utilisateur emprunteur, List<String> statuts);

    /**
     * Calcule le montant total restant dû pour tous les crédits actifs d'une tontine.
     */
    @Query("SELECT COALESCE(SUM(c.montantTotalDu - c.montantRembourse), 0) FROM Credit c WHERE c.tontine = :tontine AND c.statut = 'ACTIF'")
    java.math.BigDecimal sumMontantRestantDuByTontine(@Param("tontine") Tontine tontine);
}
