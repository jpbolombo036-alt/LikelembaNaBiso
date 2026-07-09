package com.example.demo.Service;

import com.example.demo.Dto.request.TauxChangeRequest;
import com.example.demo.Dto.response.TauxChangeResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service de gestion des taux de change.
 *
 * Alimentation hybride :
 * - saisie/override manuel par un admin ({@link #enregistrerTaux})
 * - import automatique depuis une API externe ({@link #importerDepuisApi})
 */
public interface TauxChangeService {

    /** Retourne le taux courant (date_fin IS NULL) pour une paire de devises. */
    TauxChangeResponse obtenirTauxActuel(String source, String cible);

    /** Enregistre (ou remplace) un taux de change manuellement. */
    TauxChangeResponse enregistrerTaux(TauxChangeRequest request, UUID utilisateurId);

    /** Importe les taux depuis l'API externe configurée (résilient). */
    void importerDepuisApi();

    /** Liste tous les taux de change courants. */
    List<TauxChangeResponse> lister();
}
