package com.example.demo.Service.impl;

import com.example.demo.Dto.request.OrganisationRequest;
import com.example.demo.Dto.response.OrganisationResponse;
import com.example.demo.Entity.MembreOrganisation;
import com.example.demo.Entity.Organisation;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Repository.MembreOrganisationRepository;
import com.example.demo.Repository.OrganisationRepository;
import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.Service.OrganisationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des organisations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrganisationServiceImpl implements OrganisationService {

    private final OrganisationRepository organisationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MembreOrganisationRepository membreOrganisationRepository;

    @Override
    @Transactional
    public OrganisationResponse creer(OrganisationRequest request) {
        log.info("Création d'une organisation : {}", request.getNom());

        // Vérification unicité du nom
        if (organisationRepository.existsByNom(request.getNom())) {
            throw new IllegalArgumentException(
                "Une organisation avec ce nom existe déjà : " + request.getNom()
            );
        }

        Organisation organisation = Organisation.builder()
                .nom(request.getNom())
                .type(request.getType())
                .ville(request.getVille())
                .statut(request.getStatut())
                .build();

        Organisation saved = organisationRepository.save(organisation);
        log.info("Organisation créée : {}", saved.getIdOrganisation());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public OrganisationResponse modifier(UUID idOrganisation, OrganisationRequest request) {
        Organisation organisation = trouverOuEchouer(idOrganisation);

        organisation.setNom(request.getNom());
        organisation.setType(request.getType());
        organisation.setVille(request.getVille());
        organisation.setStatut(request.getStatut());

        return toResponse(organisationRepository.save(organisation));
    }

    @Override
    @Transactional(readOnly = true)
    public OrganisationResponse obtenirParId(UUID idOrganisation) {
        return toResponse(trouverOuEchouer(idOrganisation));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationResponse> listerActives() {
        return organisationRepository.findByStatut("ACTIF")
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganisationResponse> listerParVille(String ville) {
        return organisationRepository.findByVilleAndStatut(ville, "ACTIF")
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void ajouterMembre(UUID idOrganisation, UUID idUtilisateur, String role) {
        Organisation organisation = trouverOuEchouer(idOrganisation);

        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Utilisateur introuvable : " + idUtilisateur
                ));

        // Vérification que le membre n'est pas déjà dans l'organisation
        if (membreOrganisationRepository.existsByOrganisationAndUtilisateur(organisation, utilisateur)) {
            throw new IllegalArgumentException(
                "Cet utilisateur est déjà membre de l'organisation"
            );
        }

        MembreOrganisation membre = MembreOrganisation.builder()
                .organisation(organisation)
                .utilisateur(utilisateur)
                .role(role)
                .dateAjout(LocalDate.now())
                .build();

        membreOrganisationRepository.save(membre);
        log.info("Membre {} ajouté à l'organisation {} avec le rôle {}", idUtilisateur, idOrganisation, role);
    }

    @Override
    @Transactional
    public void retirerMembre(UUID idOrganisation, UUID idUtilisateur) {
        Organisation organisation = trouverOuEchouer(idOrganisation);

        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Utilisateur introuvable : " + idUtilisateur
                ));

        MembreOrganisation membre = membreOrganisationRepository
                .findByOrganisationAndUtilisateur(organisation, utilisateur)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Cet utilisateur n'est pas membre de l'organisation"
                ));

        membreOrganisationRepository.delete(membre);
        log.info("Membre {} retiré de l'organisation {}", idUtilisateur, idOrganisation);
    }

    @Override
    @Transactional
    public OrganisationResponse changerStatut(UUID idOrganisation, String statut) {
        Organisation organisation = trouverOuEchouer(idOrganisation);
        organisation.setStatut(statut);
        return toResponse(organisationRepository.save(organisation));
    }

    // ---- Méthodes utilitaires privées ----

    /** Retrouve une organisation ou lève une exception claire. */
    private Organisation trouverOuEchouer(UUID id) {
        return organisationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Organisation introuvable : " + id
                ));
    }

    /** Convertit une entité Organisation en DTO de réponse. */
    private OrganisationResponse toResponse(Organisation org) {
        return OrganisationResponse.builder()
                .idOrganisation(org.getIdOrganisation())
                .nom(org.getNom())
                .type(org.getType())
                .ville(org.getVille())
                .statut(org.getStatut())
                .build();
    }
}
