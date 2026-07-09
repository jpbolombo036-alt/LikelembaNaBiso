package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entité JPA représentant l'inscription d'un utilisateur à une tontine.
 * Chaque membre possède un ordre de passage qui définit quand il sera bénéficiaire
 * de la cagnotte durant le cycle de la tontine.
 */
@Entity
@Table(name = "membre_tontine")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MembreTontine {

    /** Identifiant unique du membre dans la tontine (clé primaire UUID). */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_membre_tontine", updatable = false, nullable = false)
    private UUID idMembreTontine;

    /** Tontine à laquelle ce membre est inscrit (clé étrangère vers TONTINE). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tontine_id", nullable = false)
    private Tontine tontine;

    /** Utilisateur inscrit à la tontine (clé étrangère vers UTILISATEUR). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Utilisateur utilisateur;

    /**
     * Position du membre dans l'ordre de passage pour recevoir la cagnotte.
     * Le membre avec ordre_passage = 1 est le premier à bénéficier de la cagnotte.
     */
    @Column(name = "ordre_passage", nullable = false)
    private Integer ordrePassage;

    /**
     * Statut de l'inscription du membre à la tontine.
     * Valeurs possibles : ACTIF, SUSPENDU, EXCLU, SORTI.
     */
    @Column(name = "statut", nullable = false, length = 20)
    private String statut;
}
