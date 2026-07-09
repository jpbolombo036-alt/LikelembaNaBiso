package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA représentant une notification envoyée à un utilisateur.
 *
 * Les notifications permettent de tenir les membres informés des événements importants :
 * rappels de cotisation avant échéance, confirmation de paiement reçu, annonce du
 * bénéficiaire du prochain tour, alertes de retard, etc.
 *
 * Canal de diffusion : SMS via Africa's Talking (ou agrégateur local).
 * Des notifications in-app peuvent également être stockées ici pour affichage
 * dans l'interface utilisateur.
 */
@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Notification {

    /** Identifiant unique de la notification (clé primaire UUID). */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_notification", updatable = false, nullable = false)
    private UUID idNotification;

    /**
     * Utilisateur destinataire de la notification (clé étrangère vers UTILISATEUR).
     * C'est le membre qui doit recevoir le message.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Utilisateur destinataire;

    /**
     * Tontine concernée par la notification (clé étrangère vers TONTINE).
     * Peut être null pour les notifications générales (ex : bienvenue sur la plateforme).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tontine_id")
    private Tontine tontine;

    /**
     * Type de notification pour catégoriser l'événement déclencheur.
     * Valeurs possibles :
     *   RAPPEL_COTISATION   → Rappel avant date d'échéance
     *   CONFIRMATION_PAIEMENT → Accusé de réception d'une cotisation
     *   ANNONCE_BENEFICIAIRE → Annonce du gagnant du tour
     *   ALERTE_RETARD       → Membre en retard de cotisation
     *   BIENVENUE           → Invitation à rejoindre une tontine
     *   GENERAL             → Message libre de l'organisateur
     */
    @Column(name = "type", nullable = false, length = 30)
    private String type;

    /**
     * Canal d'envoi de la notification.
     * Valeurs possibles : SMS, PUSH, EMAIL, IN_APP.
     */
    @Column(name = "canal", nullable = false, length = 10)
    private String canal;

    /** Contenu du message envoyé au destinataire (texte du SMS ou de la notification). */
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    /**
     * Statut de l'envoi de la notification.
     * Valeurs possibles : EN_ATTENTE, ENVOYE, ECHEC, LU.
     */
    @Column(name = "statut", nullable = false, length = 15)
    private String statut;

    /**
     * Référence retournée par le prestataire SMS (ex : Africa's Talking message ID).
     * Utile pour tracer et confirmer la livraison du SMS côté opérateur.
     */
    @Column(name = "reference_prestataire", length = 100)
    private String referencePrestataire;

    /** Date et heure de création de la notification (générée automatiquement). */
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    /**
     * Date et heure effective d'envoi au destinataire.
     * Null si la notification est encore en file d'attente.
     */
    @Column(name = "date_envoi")
    private LocalDateTime dateEnvoi;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }
}
