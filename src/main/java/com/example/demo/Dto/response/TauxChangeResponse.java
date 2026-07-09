package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse représentant un taux de change (courant ou historique).
 */
@Data
@Builder
public class TauxChangeResponse {

    /** Identifiant du taux. */
    private UUID id;

    /** Code de la devise source. */
    private String deviseSourceCode;

    /** Code de la devise cible. */
    private String deviseCibleCode;

    /** Taux de conversion. */
    private BigDecimal taux;

    /** Date de début de validité. */
    private LocalDateTime dateDebut;

    /** Date de fin de validité (null = taux courant). */
    private LocalDateTime dateFin;

    /** Origine du taux (API, MANUAL). */
    private String source;

    /** Utilisateur ayant saisi le taux. */
    private UUID creePar;
}
