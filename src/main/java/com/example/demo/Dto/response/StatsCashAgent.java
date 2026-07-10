package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Statistiques cash agrégées pour l'agent gestionnaire, calculées sur l'ensemble
 * des tontines qu'il gère (potentiellement dans plusieurs organisations).
 *
 * Les compteurs et montants sont ventilés par statut de transaction cash :
 * - déclarations en attente (INITIEE)
 * - paiements confirmés (REUSSIE)
 * - paiements rejetés (ECHOUEE)
 */
@Data
@Builder
public class StatsCashAgent {

    /** Nombre de tontines gérées par l'agent (périmètre du dashboard cash). */
    private long nombreTontinesGerees;

    /** Nombre de déclarations cash en attente de confirmation (statut INITIEE). */
    private long nombreDeclarationsEnAttente;

    /** Montant cumulé des déclarations cash en attente. */
    private BigDecimal montantEnAttente;

    /** Nombre de paiements cash confirmés (statut REUSSIE). */
    private long nombreConfirmees;

    /** Montant cumulé des paiements cash confirmés. */
    private BigDecimal montantConfirme;

    /** Nombre de paiements cash rejetés (statut ECHOUEE). */
    private long nombreRejetees;

    /** Montant cumulé des paiements cash rejetés. */
    private BigDecimal montantRejete;
}
