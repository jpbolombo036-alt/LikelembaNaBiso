package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO de réponse pour une cotisation.
 * Donne une vue complète du statut de paiement d'un membre pour un tour.
 */
@Data
@Builder
public class CotisationResponse {

    /** Identifiant unique de la cotisation. */
    private UUID idCotisation;

    /** Numéro du tour concerné. */
    private Integer numeroTour;

    /** Nom du membre concerné. */
    private String nomMembre;

    /** Téléphone du membre (pour rappel SMS). */
    private String telephoneMembre;

    /** Montant attendu. */
    private BigDecimal montantAttendu;

    /** Montant effectivement payé. */
    private BigDecimal montantPaye;

    /** Différence restante à payer (montantAttendu - montantPaye). */
    private BigDecimal montantRestant;

    /** Date limite de paiement. */
    private LocalDate dateEcheance;

    /** Statut actuel (EN_ATTENTE, PAYE, PARTIEL, RETARD). */
    private String statut;

    /** Nom de la personne ayant confirmé le paiement (si confirmé). */
    private String confirmeParNom;
}
