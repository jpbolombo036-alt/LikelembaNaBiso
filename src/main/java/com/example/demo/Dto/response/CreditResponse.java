package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO de réponse pour un micro-crédit.
 * Affiche le détail du crédit avec le montant restant à rembourser.
 */
@Data
@Builder
public class CreditResponse {

    /** Identifiant unique du crédit. */
    private UUID idCredit;

    /** Nom de l'emprunteur. */
    private String nomEmprunteur;

    /** Nom de la tontine concernée. */
    private String nomTontine;

    /** Montant principal emprunté. */
    private BigDecimal montantPrincipal;

    /** Taux d'intérêt mensuel (%). */
    private BigDecimal tauxInteretMensuel;

    /** Durée totale en mois. */
    private Integer dureeMois;

    /** Montant total à rembourser (principal + intérêts). */
    private BigDecimal montantTotalDu;

    /** Montant déjà remboursé. */
    private BigDecimal montantRembourse;

    /** Montant restant à rembourser (calculé : montantTotalDu - montantRembourse). */
    private BigDecimal montantRestant;

    /** Devise. */
    private String devise;

    /** Date d'octroi du crédit. */
    private LocalDate dateOctroi;

    /** Date d'échéance finale. */
    private LocalDate dateEcheanceFinale;

    /** Statut actuel (EN_ATTENTE, ACTIF, REMBOURSE, EN_RETARD, DEFAUT). */
    private String statut;
}
