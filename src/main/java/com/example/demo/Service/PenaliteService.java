package com.example.demo.Service;

import com.example.demo.Dto.response.PenaliteResponse;
import com.example.demo.Entity.Cotisation;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Interface du service de gestion des pénalités.
 * Génère et gère les amendes pour retard de cotisation.
 * Principalement appelé par le Scheduler automatique.
 */
public interface PenaliteService {

    /**
     * Génère une pénalité pour une cotisation en retard.
     * Appelé automatiquement par le Scheduler lorsqu'une échéance est dépassée.
     *
     * @param cotisation   Cotisation en retard
     * @param joursRetard  Nombre de jours de retard constatés
     * @param montantParJour Montant de l'amende par jour de retard
     */
    PenaliteResponse genererPenalite(Cotisation cotisation, int joursRetard, BigDecimal montantParJour);

    /** Retourne toutes les pénalités impayées d'un membre. */
    List<PenaliteResponse> listerParMembre(UUID idUtilisateur);

    /** Marque une pénalité comme payée. */
    PenaliteResponse marquerPayee(UUID idPenalite);

    /**
     * Dispense un membre d'une pénalité (décision exceptionnelle du gestionnaire).
     * Par exemple : en cas de force majeure.
     */
    PenaliteResponse dispenser(UUID idPenalite);

    /**
     * Vérifie si un membre a des pénalités impayées dans une tontine.
     * Peut bloquer l'accès au tour suivant si des pénalités sont en attente.
     */
    boolean aPenalitesEnAttente(UUID idUtilisateur);

    /** Calcule le montant total des pénalités impayées d'un membre. */
    BigDecimal calculerTotalPenalitesEnAttente(UUID idUtilisateur);
}
