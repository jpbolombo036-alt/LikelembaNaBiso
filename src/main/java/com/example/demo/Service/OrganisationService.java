package com.example.demo.Service;

import com.example.demo.Dto.request.OrganisationRequest;
import com.example.demo.Dto.response.OrganisationResponse;

import java.util.List;
import java.util.UUID;

/**
 * Interface du service de gestion des organisations.
 * Une organisation est la structure racine (groupe, association, etc.).
 */
public interface OrganisationService {

    /** Crée une nouvelle organisation. */
    OrganisationResponse creer(OrganisationRequest request);

    /** Met à jour une organisation existante. */
    OrganisationResponse modifier(UUID idOrganisation, OrganisationRequest request);

    /** Retourne le détail d'une organisation par son identifiant. */
    OrganisationResponse obtenirParId(UUID idOrganisation);

    /** Retourne toutes les organisations actives. */
    List<OrganisationResponse> listerActives();

    /** Retourne toutes les organisations d'une ville donnée. */
    List<OrganisationResponse> listerParVille(String ville);

    /**
     * Ajoute un membre à une organisation avec un rôle donné.
     *
     * @param idOrganisation Identifiant de l'organisation
     * @param idUtilisateur  Identifiant de l'utilisateur à ajouter
     * @param role           Rôle dans l'organisation (ADMIN, MEMBRE, TRESORIER...)
     */
    void ajouterMembre(UUID idOrganisation, UUID idUtilisateur, String role);

    /** Supprime un membre d'une organisation. */
    void retirerMembre(UUID idOrganisation, UUID idUtilisateur);

    /** Change le statut d'une organisation (ACTIF / SUSPENDU / INACTIF). */
    OrganisationResponse changerStatut(UUID idOrganisation, String statut);
}
