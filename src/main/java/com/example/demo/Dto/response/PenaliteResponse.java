package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse pour une pénalité.
 * Affiche le détail de l'amende et son statut de paiement.
 */
@Data
@Builder
public class PenaliteResponse {

    /** Identifiant unique de la pénalité. */
    private UUID idPenalite;

    /** Nom du membre pénalisé. */
    private String nomMembre;

    /** Motif de la pénalité (RETARD_COTISATION, etc.). */
    private String motif;

    /** Nombre de jours de retard constatés. */
    private Integer joursRetard;

    /** Montant de l'amende. */
    private BigDecimal montant;

    /** Devise. */
    private String devise;

    /** Statut (EN_ATTENTE, PAYEE, ANNULEE, DISPENSEE). */
    private String statut;

    /** Date de génération automatique par le Scheduler. */
    private LocalDateTime dateGeneration;
}
