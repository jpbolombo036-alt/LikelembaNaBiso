package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité JPA représentant une devise de référence (code ISO comme clé primaire).
 * Les tontines, transactions, crédits et pénalités référencent une devise via son code.
 */
@Entity
@Table(name = "devise")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Devise {

    /** Code ISO de la devise (ex : CDF, USD). Sert de clé primaire. */
    @Id
    @Column(name = "code", nullable = false, length = 10)
    private String code;

    /** Nom complet de la devise (ex : Franc Congolais). */
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    /** Symbole monétaire (ex : FC, $). */
    @Column(name = "symbole", length = 10)
    private String symbole;

    /** Indique si la devise est actuellement utilisable. */
    @Column(name = "actif", nullable = false)
    private Boolean actif;
}
