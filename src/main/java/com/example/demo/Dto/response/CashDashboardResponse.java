package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Réponse du dashboard cash dédié à l'agent gestionnaire.
 *
 * Agrège, sur toutes les tontines gérées par l'agent :
 * - les statistiques cash ({@link StatsCashAgent})
 * - la liste des déclarations cash en attente de confirmation
 * - les alertes cash (déclarations anciennes, montant en attente élevé, ...)
 */
@Data
@Builder
public class CashDashboardResponse {

    /** Nom de l'agent gestionnaire. */
    private String utilisateurNom;

    /** Nom d'une organisation de rattachement de l'agent (peut être null). */
    private String organisationNom;

    /** Statistiques cash agrégées. */
    private StatsCashAgent statsCash;

    /** Déclarations cash en attente de confirmation (statut INITIEE). */
    private List<TransactionResponse> declarationsEnAttente;

    /** Alertes cash calculées pour l'agent. */
    private List<Alerte> alertesCash;
}
