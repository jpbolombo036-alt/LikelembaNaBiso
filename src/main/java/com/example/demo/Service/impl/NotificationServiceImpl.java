package com.example.demo.Service.impl;

import com.example.demo.Entity.Notification;
import com.example.demo.Entity.Tontine;
import com.example.demo.Entity.Tour;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Repository.MembreTontineRepository;
import com.example.demo.Repository.NotificationRepository;
import com.example.demo.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implémentation du service de notification SMS.
 *
 * ⚠️ INTÉGRATION EXTERNE REQUISE — Africa's Talking :
 * Chaque envoi SMS appelle l'API Africa's Talking (ou agrégateur local).
 * Les appels réels sont marqués TODO et seront implémentés dans un client dédié
 * (ex: AfricasTalkingClient) une fois les clés API obtenues.
 *
 * Documentation Africa's Talking : https://developers.africastalking.com/docs/sms/sending
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final MembreTontineRepository membreTontineRepository;

    @Override
    @Transactional
    public void envoyerBienvenueTontine(Utilisateur membre, Tontine tontine) {
        String message = String.format(
            "Bienvenue dans la tontine \"%s\" ! Montant de cotisation : %s %s. " +
            "Vous recevrez un SMS à chaque tour. Likelamba.",
            tontine.getNom(),
            tontine.getMontantCotisation(),
            tontine.getDevise().getCode()
        );
        envoyerSms(membre, tontine, "BIENVENUE", message);
    }

    @Override
    @Transactional
    public void notifierNouveauTour(Tontine tontine, Tour tour, Utilisateur beneficiaire) {
        // Notification à TOUS les membres actifs de la tontine
        List<com.example.demo.Entity.MembreTontine> membres =
            membreTontineRepository.findByTontineAndStatut(tontine, "ACTIF");

        String message = String.format(
            "Tour %d lancé dans \"%s\" ! Bénéficiaire : %s. " +
            "Payez votre cotisation de %s %s avant le %s. Likelamba.",
            tour.getNumeroTour(),
            tontine.getNom(),
            beneficiaire.getNom(),
            tontine.getMontantCotisation(),
            tontine.getDevise().getCode(),
            tour.getDatePrevue()
        );

        for (com.example.demo.Entity.MembreTontine membre : membres) {
            envoyerSms(membre.getUtilisateur(), tontine, "ANNONCE_BENEFICIAIRE", message);
        }

        log.info("Notifications tour {} envoyées à {} membres", tour.getNumeroTour(), membres.size());
    }

    @Override
    @Transactional
    public void envoyerRappelCotisation(Utilisateur membre, Tour tour, int joursRestants) {
        String message = String.format(
            "RAPPEL Likelamba : votre cotisation de %s %s pour le tour %d est due dans %d jour(s) (le %s). " +
            "Payez maintenant pour éviter une pénalité.",
            tour.getTontine().getMontantCotisation(),
            tour.getTontine().getDevise().getCode(),
            tour.getNumeroTour(),
            joursRestants,
            tour.getDatePrevue()
        );
        envoyerSms(membre, tour.getTontine(), "RAPPEL_COTISATION", message);
    }

    @Override
    @Transactional
    public void confirmerPaiement(Utilisateur membre, Tour tour) {
        String message = String.format(
            "Likelamba : paiement de %s %s confirmé pour le tour %d de \"%s\". Merci !",
            tour.getTontine().getMontantCotisation(),
            tour.getTontine().getDevise().getCode(),
            tour.getNumeroTour(),
            tour.getTontine().getNom()
        );
        envoyerSms(membre, tour.getTontine(), "CONFIRMATION_PAIEMENT", message);
    }

    @Override
    @Transactional
    public void notifierCagnottePrete(Utilisateur beneficiaire, Tour tour) {
        String message = String.format(
            "Félicitations %s ! La cagnotte du tour %d de \"%s\" est prête. " +
            "Contactez votre gestionnaire pour la récupérer. Likelamba.",
            beneficiaire.getNom(),
            tour.getNumeroTour(),
            tour.getTontine().getNom()
        );
        envoyerSms(beneficiaire, tour.getTontine(), "ANNONCE_BENEFICIAIRE", message);
    }

    @Override
    @Transactional
    public void alerterRetardCotisation(Utilisateur membre, Tour tour) {
        String message = String.format(
            "RETARD Likelamba : votre cotisation du tour %d de \"%s\" est en retard. " +
            "Une pénalité a été appliquée. Régularisez au plus vite.",
            tour.getNumeroTour(),
            tour.getTontine().getNom()
        );
        envoyerSms(membre, tour.getTontine(), "ALERTE_RETARD", message);
    }

    // ---- Méthode centrale d'envoi ----

    /**
     * Crée la notification en base et appelle l'API SMS.
     * En cas d'échec de l'envoi SMS, la notification reste en base avec statut ECHEC
     * pour être réessayée ultérieurement par le Scheduler.
     */
    private void envoyerSms(Utilisateur destinataire, Tontine tontine, String type, String message) {
        // 1. Persister la notification en base (statut EN_ATTENTE)
        Notification notification = Notification.builder()
                .destinataire(destinataire)
                .tontine(tontine)
                .type(type)
                .canal("SMS")
                .message(message)
                .statut("EN_ATTENTE")
                .build();

        Notification saved = notificationRepository.save(notification);

        // 2. TODO : Appel à l'API Africa's Talking
        // try {
        //     AfricasTalkingResponse response = africasTalkingClient.envoyerSms(
        //         destinataire.getTelephone(), message
        //     );
        //     saved.setStatut("ENVOYE");
        //     saved.setReferencePrestataire(response.getMessageId());
        //     saved.setDateEnvoi(LocalDateTime.now());
        // } catch (Exception e) {
        //     log.error("Échec envoi SMS à {} : {}", destinataire.getTelephone(), e.getMessage());
        //     saved.setStatut("ECHEC");
        // }

        // Simulation : marquer comme ENVOYE (à remplacer par l'appel réel)
        saved.setStatut("ENVOYE");
        saved.setDateEnvoi(LocalDateTime.now());
        notificationRepository.save(saved);

        log.info("SMS [{}] envoyé à {} : {}", type, destinataire.getTelephone(), message);
    }

    @Override
    @Transactional
    public void notifierDeclarationCash(Utilisateur agent, String nomMembre, BigDecimal montant, String devise, Tontine tontine) {
        String message = String.format(
            "Likelamba : le membre %s a déclaré un paiement cash de %s %s pour \"%s\". " +
            "Veuillez confirmer ou rejeter cette déclaration.",
            nomMembre,
            montant,
            devise,
            tontine.getNom()
        );
        envoyerSms(agent, tontine, "DECLARATION_CASH", message);
    }

    @Override
    @Transactional
    public void notifierConfirmationCash(Utilisateur membre, boolean accepte, BigDecimal montant, String devise, Tontine tontine, String commentaire) {
        String statut = accepte ? "confirmé" : "rejeté";
        String motif = (commentaire != null && !commentaire.isEmpty()) ? " Motif : " + commentaire : "";
        String message = String.format(
            "Likelamba : votre paiement cash de %s %s pour \"%s\" a été %s.%s",
            montant,
            devise,
            tontine.getNom(),
            statut,
            motif
        );
        envoyerSms(membre, tontine, accepte ? "CONFIRMATION_CASH" : "REJET_CASH", message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> listerPourAgent(Utilisateur agent, String typeFiltre) {
        if (typeFiltre != null && !typeFiltre.isEmpty()) {
            return notificationRepository
                    .findByDestinataireAndTypeAndStatutNotOrderByDateCreationDesc(agent, typeFiltre, "LU");
        }
        return notificationRepository.findByDestinataireAndStatutNotOrderByDateCreationDesc(agent, "LU");
    }

    @Override
    @Transactional
    public void marquerCommeLue(UUID idNotification, UUID agentId) {
        Notification notification = notificationRepository.findById(idNotification)
                .orElseThrow(() -> new IllegalArgumentException("Notification introuvable : " + idNotification));

        if (!notification.getDestinataire().getIdUtilisateur().equals(agentId)) {
            throw new IllegalAccessError(
                    "La notification " + idNotification + " n'appartient pas à l'agent " + agentId);
        }

        notification.setStatut("LU");
        notificationRepository.save(notification);
    }
}
