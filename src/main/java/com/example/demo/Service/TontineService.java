package com.example.demo.Service;

import com.example.demo.Dto.request.TontineRequest;
import com.example.demo.Dto.response.TontineResponse;
import com.example.demo.Dto.response.TourResponse;

import java.util.List;
import java.util.UUID;

/**
 * Interface du service de gestion des tontines.
 * Cœur métier de Likelamba : création, gestion des membres, tours et clôture.
 */
public interface TontineService {

    /** Crée une nouvelle tontine dans une organisation. */
    TontineResponse creer(TontineRequest request);

    /** Retourne le détail d'une tontine. */
    TontineResponse obtenirParId(UUID idTontine);

    /** Retourne toutes les tontines actives d'une organisation. */
    List<TontineResponse> listerParOrganisation(UUID idOrganisation);

    /**
     * Inscrit un utilisateur à une tontine avec un ordre de passage.
     *
     * @param idTontine      Tontine cible
     * @param idUtilisateur  Utilisateur à inscrire
     * @param ordrePassage   Position dans l'ordre de bénéfice (1 = premier)
     */
    void inscrireMembre(UUID idTontine, UUID idUtilisateur, Integer ordrePassage);

    /** Retire un membre d'une tontine (statut SORTI). */
    void retirerMembre(UUID idTontine, UUID idUtilisateur);

    /**
     * Lance le tour suivant de la tontine.
     * Calcule automatiquement le bénéficiaire selon l'ordre de passage,
     * crée toutes les cotisations pour les membres actifs,
     * et envoie des notifications SMS.
     *
     * @param idTontine Tontine concernée
     * @return Le tour créé avec les détails du bénéficiaire
     */
    TourResponse lancerProchainTour(UUID idTontine);

    /**
     * Clôture un tour après vérification que toutes les cotisations sont payées.
     * Marque le tour comme TERMINE et notifie le bénéficiaire.
     *
     * @param idTour Tour à clôturer
     */
    void cloturerTour(UUID idTour);

    /** Retourne tous les tours d'une tontine. */
    List<TourResponse> listerTours(UUID idTontine);

    /** Change le statut d'une tontine (SUSPENDUE, ANNULEE, EN_COURS). */
    TontineResponse changerStatut(UUID idTontine, String statut);
}
