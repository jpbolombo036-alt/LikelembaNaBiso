package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO de réponse représentant une devise de référence.
 */
@Data
@Builder
public class DeviseResponse {

    /** Code ISO de la devise. */
    private String code;

    /** Nom complet de la devise. */
    private String nom;

    /** Symbole monétaire. */
    private String symbole;

    /** Indique si la devise est active. */
    private Boolean actif;
}
