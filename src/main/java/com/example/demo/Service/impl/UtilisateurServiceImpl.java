package com.example.demo.Service.impl;

import com.example.demo.Dto.request.InscriptionRequest;
import com.example.demo.Dto.request.LoginRequest;
import com.example.demo.Dto.request.ChangerMotDePasseRequest;
import com.example.demo.Dto.request.ReinitialiserMotDePasseRequest;
import com.example.demo.Dto.request.ReinitialisationMasseRequest;
import com.example.demo.Dto.response.LoginResponse;
import com.example.demo.Dto.response.UtilisateurResponse;
import com.example.demo.Entity.MembreOrganisation;
import com.example.demo.Entity.RefreshToken;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.Repository.RefreshTokenRepository;
import com.example.demo.Repository.MembreOrganisationRepository;
import com.example.demo.Service.UtilisateurService;
import com.example.demo.Security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implémentation du service de gestion des utilisateurs.
 *
 * Dépendances :
 * - UtilisateurRepository : accès base de données
 * - PasswordEncoder : hashage BCrypt du mot de passe
 * - JwtService : génération du token JWT
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MembreOrganisationRepository membreOrganisationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * {@inheritDoc}
     *
     * Étapes :
     * 1. Vérifier que le numéro de téléphone n'est pas déjà utilisé
     * 2. Hasher le mot de passe avec BCrypt
     * 3. Sauvegarder l'utilisateur en base
     * 4. Retourner le profil créé
     */
    @Override
    @Transactional
    public UtilisateurResponse inscrire(InscriptionRequest request) {
        log.info("Tentative d'inscription pour le numéro : {}", request.getTelephone());

        // Vérification unicité du numéro de téléphone
        if (utilisateurRepository.existsByTelephone(request.getTelephone())) {
            throw new IllegalArgumentException(
                "Ce numéro de téléphone est déjà utilisé : " + request.getTelephone()
            );
        }

        // Mot de passe par défaut si non fourni
        String motDePasse = (request.getMotDePasse() == null || request.getMotDePasse().isBlank())
                ? "12345678" : request.getMotDePasse();
        boolean doitChanger = (request.getMotDePasse() == null || request.getMotDePasse().isBlank());

        // Création de l'entité utilisateur avec mot de passe hashé
        Utilisateur utilisateur = Utilisateur.builder()
                .nom(request.getNom())
                .telephone(request.getTelephone())
                .motDePasseHash(passwordEncoder.encode(motDePasse))
                .doitChangerMotDePasse(doitChanger)
                .build();

        Utilisateur saved = utilisateurRepository.save(utilisateur);
        log.info("Utilisateur créé avec succès : {}", saved.getIdUtilisateur());

        return toResponse(saved);
    }

    /**
     * {@inheritDoc}
     *
     * Étapes :
     * 1. Rechercher l'utilisateur par numéro de téléphone
     * 2. Vérifier le mot de passe avec BCrypt
     * 3. Générer et retourner un token JWT
     *
     * TODO : intégrer JwtService pour la génération du token.
     */
    @Override
    @Transactional(readOnly = true)
    public LoginResponse connecter(LoginRequest request) {
        log.info("Tentative de connexion pour le numéro : {}", request.getTelephone());

        Utilisateur utilisateur = utilisateurRepository
                .findByTelephone(request.getTelephone())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Numéro de téléphone ou mot de passe incorrect"
                ));

        // Vérification du mot de passe
        if (!passwordEncoder.matches(request.getMotDePasse(), utilisateur.getMotDePasseHash())) {
            throw new IllegalArgumentException("Numéro de téléphone ou mot de passe incorrect");
        }

        log.info("Connexion réussie pour : {}", utilisateur.getIdUtilisateur());

        String jwt = jwtService.genererToken(utilisateur);
        String refreshToken = jwtService.genererRefreshToken(utilisateur);
        RefreshToken tokenEntity = RefreshToken.builder()
                .utilisateur(utilisateur)
                .token(refreshToken)
                .dateExpiration(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
        refreshTokenRepository.save(tokenEntity);

        return LoginResponse.builder()
                .token(jwt)
                .type("Bearer")
                .expiresIn(jwtService.getJwtExpiration() / 1000)
                .refreshToken(refreshToken)
                .utilisateur(toResponse(utilisateur))
                .doitChangerMotDePasse(utilisateur.isDoitChangerMotDePasse())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponse obtenirParId(UUID idUtilisateur) {
        Utilisateur utilisateur = utilisateurRepository
                .findById(idUtilisateur)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Utilisateur introuvable : " + idUtilisateur
                ));
        return toResponse(utilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponse obtenirParTelephone(String telephone) {
        Utilisateur utilisateur = utilisateurRepository
                .findByTelephone(telephone)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Utilisateur introuvable pour le numéro : " + telephone
                ));
        return toResponse(utilisateur);
    }

    private UtilisateurResponse toResponse(Utilisateur utilisateur) {
        return UtilisateurResponse.builder()
                .idUtilisateur(utilisateur.getIdUtilisateur())
                .nom(utilisateur.getNom())
                .telephone(utilisateur.getTelephone())
                .actif(utilisateur.isActif())
                .build();
    }

    @Override
    @Transactional
    public ChangerMotDePasseRequest changerMotDePasse(UUID idUtilisateur, ChangerMotDePasseRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + idUtilisateur));

        if (!passwordEncoder.matches(request.getAncienMotDePasse(), utilisateur.getMotDePasseHash())) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }

        utilisateur.setMotDePasseHash(passwordEncoder.encode(request.getNouveauMotDePasse()));
        utilisateur.setDoitChangerMotDePasse(false);
        utilisateurRepository.save(utilisateur);
        log.info("Mot de passe changé pour : {}", idUtilisateur);
        return request;
    }

    @Override
    @Transactional
    public void reinitialiserMotDePasse(ReinitialiserMotDePasseRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(request.getTelephone())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable pour le numéro : " + request.getTelephone()));

        utilisateur.setMotDePasseHash(passwordEncoder.encode(request.getNouveauMotDePasse()));
        utilisateurRepository.save(utilisateur);
        log.info("Mot de passe réinitialisé pour : {}", utilisateur.getIdUtilisateur());
    }

    @Override
    @Transactional
    public void reinitialiserEnMasse(ReinitialisationMasseRequest request) {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAllById(request.getUtilisateurIds());
        if (utilisateurs.isEmpty()) {
            throw new IllegalArgumentException("Aucun utilisateur trouvé pour les identifiants fournis");
        }

        String hash = passwordEncoder.encode(request.getNouveauMotDePasse());
        for (Utilisateur utilisateur : utilisateurs) {
            utilisateur.setMotDePasseHash(hash);
            utilisateurRepository.save(utilisateur);
        }
        log.info("Réinitialisation en masse de {} utilisateurs", utilisateurs.size());
    }

    @Override
    @Transactional
    public UtilisateurResponse assignerRole(UUID utilisateurId, String role) {
        // Pour rester fidèle au modèle existant, on recherche le premier MembreOrganisation de l'utilisateur
        // ou on exige un organisationId en plus. Ici on assigne le rôle sur tous les MembreOrganisation de l'utilisateur.
        List<com.example.demo.Entity.MembreOrganisation> membres = membreOrganisationRepository.findByUtilisateur(
                utilisateurRepository.findById(utilisateurId)
                        .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + utilisateurId))
        );

        if (membres.isEmpty()) {
            throw new IllegalArgumentException("L'utilisateur n'appartient à aucune organisation");
        }

        for (com.example.demo.Entity.MembreOrganisation membre : membres) {
            membre.setRole(role);
            membreOrganisationRepository.save(membre);
        }
        log.info("Rôle {} assigné à l'utilisateur {} sur {} organisations", role, utilisateurId, membres.size());
        return obtenirParId(utilisateurId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurResponse> listerParRole(String role) {
        List<MembreOrganisation> membres = membreOrganisationRepository.findByRole(role);
        return membres.stream()
                .map(membre -> toResponse(membre.getUtilisateur()))
                .toList();
    }

    @Override
    @Transactional
    public UtilisateurResponse toggleStatut(UUID idUtilisateur) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + idUtilisateur));

        utilisateur.setActif(!utilisateur.isActif());
        utilisateurRepository.save(utilisateur);
        log.info("Statut de l'utilisateur {} basculé vers {}", idUtilisateur, utilisateur.isActif());
        return toResponse(utilisateur);
    }
}
