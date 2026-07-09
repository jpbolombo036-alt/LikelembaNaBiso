package com.example.demo.Repository;

import com.example.demo.Entity.Notification;
import com.example.demo.Entity.Tontine;
import com.example.demo.Entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository JPA pour l'entité Notification.
 * Historique de tous les SMS et messages envoyés aux membres.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /** Retourne toutes les notifications d'un destinataire, triées de la plus récente. */
    List<Notification> findByDestinataireOrderByDateCreationDesc(Utilisateur destinataire);

    /** Retourne les notifications non lues d'un destinataire. */
    List<Notification> findByDestinataireAndStatut(Utilisateur destinataire, String statut);

    /** Retourne les notifications liées à une tontine spécifique. */
    List<Notification> findByTontine(Tontine tontine);

    /** Retourne les notifications en attente d'envoi (pour le worker d'envoi SMS). */
    List<Notification> findByStatutOrderByDateCreationAsc(String statut);

    /** Retourne les notifications par type (ex: RAPPEL_COTISATION). */
    List<Notification> findByType(String type);

    /** Retourne les notifications par canal (ex: SMS, PUSH). */
    List<Notification> findByCanal(String canal);

    /** Compte les notifications non lues d'un utilisateur (badge UI). */
    long countByDestinataireAndStatut(Utilisateur destinataire, String statut);
}
