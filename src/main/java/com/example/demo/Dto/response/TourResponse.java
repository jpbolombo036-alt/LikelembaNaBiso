package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO de réponse pour un tour de tontine.
 * Vue complète du tour avec bénéficiaire, avancement des cotisations et cagnotte.
 */
@Data
@Builder
public class TourResponse {

    /** Identifiant unique du tour. */
    private UUID idTour;

    /** Nom de la tontine concernée. */
    private String nomTontine;

    /** Numéro séquentiel du tour. */
    private Integer numeroTour;

    /** Nom du bénéficiaire qui recevra la cagnotte. */
    private String nomBeneficiaire;

    /** Téléphone du bénéficiaire. */
    private String telephoneBeneficiaire;

    /** Date prévue pour la remise de la cagnotte. */
    private LocalDate datePrevue;

    /** Statut du tour (PLANIFIE, EN_COURS, TERMINE, REPORTE, ANNULE). */
    private String statut;

    /** Montant total collecté pour ce tour. */
    private BigDecimal montantCollecte;

    /** Nombre de membres ayant payé. */
    private long nombrePaiementsEffectues;

    /** Nombre total de membres devant cotiser. */
    private long nombreTotalMembres;
}
