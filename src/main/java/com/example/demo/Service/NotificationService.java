package com.example.demo.Service;

import com.example.demo.Entity.Tontine;
import com.example.demo.Entity.Tour;
import com.example.demo.Entity.Utilisateur;

import java.math.BigDecimal;

public interface NotificationService {

    /**
     * SMS de bienvenue envoyé quand un membre rejoint une tontine.
     * Contenu : "Bienvenue dans la tontine [nom], votre ordre de passage est [n]."
     */
    void envoyerBienvenueTontine(Utilisateur membre, Tontine tontine);

    /**
     * SMS envoyé à tous les membres au lancement d'un nouveau tour.
     * Contenu : "Tour [n] lancé ! Bénéficiaire : [nom]. Payez avant le [date]."
     */
    void notifierNouveauTour(Tontine tontine, Tour tour, Utilisateur beneficiaire);

    /**
     * SMS de rappel de cotisation envoyé avant l'échéance (J-3, J-1).
     * Contenu : "Rappel : votre cotisation de [montant] [devise] est due le [date]."
     */
    void envoyerRappelCotisation(Utilisateur membre, Tour tour, int joursRestants);

    /**
     * SMS de confirmation envoyé après réception d'un paiement réussi.
     * Contenu : "Paiement de [montant] [devise] reçu. Merci !"
     */
    void confirmerPaiement(Utilisateur membre, Tour tour);

    /**
     * SMS notifiant le bénéficiaire que la cagnotte est prête à être retirée.
     * Contenu : "La cagnotte du tour [n] est prête ! Montant : [total] [devise]."
     */
    void notifierCagnottePrete(Utilisateur beneficiaire, Tour tour);

    /**
     * SMS d'alerte de retard envoyé au membre qui n'a pas payé après l'échéance.
     * Contenu : "RETARD : votre cotisation du tour [n] est en retard. Une pénalité sera appliquée."
     */
    void alerterRetardCotisation(Utilisateur membre, Tour tour);

    /**
     * SMS envoyé à l'agent gestionnaire quand un membre déclare un paiement cash.
     */
    void notifierDeclarationCash(Utilisateur agent, String nomMembre, BigDecimal montant, String devise, Tontine tontine);

    /**
     * SMS envoyé au membre après confirmation ou rejet de son paiement cash par l'agent.
     */
    void notifierConfirmationCash(Utilisateur membre, boolean accepte, BigDecimal montant, String devise, Tontine tontine, String commentaire);
}
