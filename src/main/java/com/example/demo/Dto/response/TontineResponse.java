package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de réponse pour l'entité Tontine.
 * Inclut des informations résumées sur l'organisation et l'agent gestionnaire.
 */
@Data
@Builder
public class TontineResponse {

    /** Identifiant unique de la tontine. */
    private UUID idTontine;

    /** Nom de la tontine. */
    private String nom;

    /** Montant de cotisation par tour. */
    private BigDecimal montantCotisation;

    /** Devise utilisée (CDF, USD). */
    private String devise;

    /** Statut actuel de la tontine (EN_COURS, TERMINEE, etc.). */
    private String statut;

    /** Résumé de l'organisation hébergeant la tontine. */
    private UUID organisationId;
    private String organisationNom;

    /** Résumé de l'agent gestionnaire. */
    private UUID agentGestionnaireId;
    private String agentGestionnaireNom;

    /** Nombre de membres actifs dans la tontine (calculé à la volée). */
    private long nombreMembres;
}
