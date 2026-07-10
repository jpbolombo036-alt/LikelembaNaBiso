package com.example.demo.Service;

import com.example.demo.Dto.request.InscriptionRequest;
import com.example.demo.Dto.request.LoginRequest;
import com.example.demo.Dto.request.ChangerMotDePasseRequest;
import com.example.demo.Dto.request.ReinitialiserMotDePasseRequest;
import com.example.demo.Dto.request.ReinitialisationMasseRequest;
import com.example.demo.Dto.response.LoginResponse;
import com.example.demo.Dto.response.UtilisateurResponse;

import java.util.List;
import java.util.UUID;

/**
 * Interface du service de gestion des utilisateurs.
 * Gère l'inscription, la connexion et la consultation des profils.
 */
public interface UtilisateurService {

    /**
     * Inscrit un nouvel utilisateur dans le système.
     * Le mot de passe est hashé avant stockage (BCrypt).
     *
     * @param request Données d'inscription (nom, téléphone, mot de passe)
     * @return Le profil de l'utilisateur créé
     */
    UtilisateurResponse inscrire(InscriptionRequest request);

    /**
     * Authentifie un utilisateur par téléphone + mot de passe.
     * Retourne un token JWT valide 24h en cas de succès.
     *
     * @param request Identifiants de connexion
     * @return Token JWT + profil utilisateur
     */
    LoginResponse connecter(LoginRequest request);

    /**
     * Retourne le profil d'un utilisateur par son identifiant.
     *
     * @param idUtilisateur UUID de l'utilisateur
     * @return Profil utilisateur
     */
    UtilisateurResponse obtenirParId(UUID idUtilisateur);

    /**
     * Retourne le profil d'un utilisateur par son numéro de téléphone.
     *
     * @param telephone Numéro de téléphone
     * @return Profil utilisateur
     */
    UtilisateurResponse obtenirParTelephone(String telephone);

    UtilisateurResponse changerMotDePasse(UUID idUtilisateur, ChangerMotDePasseRequest request);

    void reinitialiserMotDePasse(ReinitialiserMotDePasseRequest request);

    void reinitialiserEnMasse(ReinitialisationMasseRequest request);

    UtilisateurResponse assignerRole(UUID utilisateurId, String role);

    List<UtilisateurResponse> listerParRole(String role);

    UtilisateurResponse toggleStatut(UUID idUtilisateur);
}
