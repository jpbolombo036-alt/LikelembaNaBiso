package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA représentant un taux de change entre deux devises pour une période donnée.
 *
 * Le taux "courant" pour une paire est identifié par {@code dateFin IS NULL}.
 * Un nouveau taux pour une paire clôture le précédent (dateFin renseignée),
 * ce qui permet de conserver un historique complet des taux.
 */
@Entity
@Table(name = "taux_change")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TauxChange {

    /** Identifiant unique du taux de change. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** Devise source du taux (clé étrangère vers DEVISE). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devise_source", nullable = false)
    private Devise deviseSource;

    /** Devise cible du taux (clé étrangère vers DEVISE). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devise_cible", nullable = false)
    private Devise deviseCible;

    /** Taux de conversion (1 unité de devise_source vaut `taux` unités de devise_cible). */
    @Column(name = "taux", nullable = false, precision = 19, scale = 6)
    private BigDecimal taux;

    /** Date de début de validité du taux. */
    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    /**
     * Date de fin de validité du taux.
     * Null => taux courant (encore valide).
     */
    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    /**
     * Origine du taux.
     * Valeurs possibles : API (import automatique), MANUAL (saisie admin).
     */
    @Column(name = "source", nullable = false, length = 10)
    private String source;

    /** Utilisateur ayant enregistré/saisi le taux (null pour les imports API). */
    @Column(name = "cree_par")
    private UUID creePar;

    /** Date et heure de création de l'enregistrement. */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
