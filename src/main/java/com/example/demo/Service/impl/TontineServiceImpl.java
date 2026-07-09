package com.example.demo.Service.impl;

import com.example.demo.Dto.request.TontineRequest;
import com.example.demo.Dto.response.TontineResponse;
import com.example.demo.Dto.response.TourResponse;
import com.example.demo.Entity.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.DeviseService;
import com.example.demo.Service.NotificationService;
import com.example.demo.Service.TontineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des tontines.
 *
 * Logique métier principale :
 * - Lancement automatique du prochain tour selon l'ordre de passage
 * - Création des cotisations pour tous les membres actifs
 * - Envoi des notifications SMS au lancement et à la clôture
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TontineServiceImpl implements TontineService {

    private final TontineRepository tontineRepository;
    private final OrganisationRepository organisationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MembreTontineRepository membreTontineRepository;
    private final TourRepository tourRepository;
    private final CotisationRepository cotisationRepository;
    private final NotificationService notificationService;
    private final DeviseService deviseService;

    @Override
    @Transactional
    public TontineResponse creer(TontineRequest request) {
        log.info("Création d'une tontine : {}", request.getNom());

        Organisation organisation = organisationRepository.findById(request.getOrganisationId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Organisation introuvable : " + request.getOrganisationId()
                ));

        Utilisateur agent = utilisateurRepository.findById(request.getAgentGestionnaireId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Agent gestionnaire introuvable : " + request.getAgentGestionnaireId()
                ));

        Tontine tontine = Tontine.builder()
                .organisation(organisation)
                .agentGestionnaire(agent)
                .nom(request.getNom())
                .montantCotisation(request.getMontantCotisation())
                .devise(deviseService.resoudre(request.getDevise()))
                .statut("EN_COURS")
                .build();

        Tontine saved = tontineRepository.save(tontine);
        log.info("Tontine créée : {}", saved.getIdTontine());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TontineResponse obtenirParId(UUID idTontine) {
        return toResponse(trouverOuEchouer(idTontine));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TontineResponse> listerParOrganisation(UUID idOrganisation) {
        Organisation organisation = organisationRepository.findById(idOrganisation)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Organisation introuvable : " + idOrganisation
                ));
        return tontineRepository.findByOrganisationAndStatut(organisation, "EN_COURS")
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void inscrireMembre(UUID idTontine, UUID idUtilisateur, Integer ordrePassage) {
        Tontine tontine = trouverOuEchouer(idTontine);

        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Utilisateur introuvable : " + idUtilisateur
                ));

        // Vérification que le membre n'est pas déjà inscrit
        if (membreTontineRepository.existsByTontineAndUtilisateur(tontine, utilisateur)) {
            throw new IllegalArgumentException("Cet utilisateur est déjà inscrit à cette tontine");
        }

        MembreTontine membre = MembreTontine.builder()
                .tontine(tontine)
                .utilisateur(utilisateur)
                .ordrePassage(ordrePassage)
                .statut("ACTIF")
                .build();

        membreTontineRepository.save(membre);

        // Notification de bienvenue par SMS
        notificationService.envoyerBienvenueTontine(utilisateur, tontine);
        log.info("Membre {} inscrit à la tontine {} (ordre : {})", idUtilisateur, idTontine, ordrePassage);
    }

    @Override
    @Transactional
    public void retirerMembre(UUID idTontine, UUID idUtilisateur) {
        Tontine tontine = trouverOuEchouer(idTontine);
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Utilisateur introuvable : " + idUtilisateur
                ));

        MembreTontine membre = membreTontineRepository
                .findByTontineAndUtilisateur(tontine, utilisateur)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Cet utilisateur n'est pas membre de cette tontine"
                ));

        // Changement de statut plutôt que suppression physique (historique)
        membre.setStatut("SORTI");
        membreTontineRepository.save(membre);
        log.info("Membre {} sorti de la tontine {}", idUtilisateur, idTontine);
    }

    @Override
    @Transactional
    public TourResponse lancerProchainTour(UUID idTontine) {
        Tontine tontine = trouverOuEchouer(idTontine);

        // Calcul du numéro du prochain tour
        long nombreToursExistants = tourRepository.countByTontine(tontine);
        int numeroProchainTour = (int) nombreToursExistants + 1;

        // Récupération des membres actifs triés par ordre de passage
        List<MembreTontine> membresActifs = membreTontineRepository
                .findMembresActifsOrdonnes(tontine);

        if (membresActifs.isEmpty()) {
            throw new IllegalStateException("Aucun membre actif dans cette tontine");
        }

        // Le bénéficiaire est le membre dont l'ordre correspond au numéro du tour
        int indexBeneficiaire = (numeroProchainTour - 1) % membresActifs.size();
        MembreTontine membreBeneficiaire = membresActifs.get(indexBeneficiaire);
        Utilisateur beneficiaire = membreBeneficiaire.getUtilisateur();

        // Date du tour : 30 jours à partir d'aujourd'hui par défaut
        LocalDate datePrevue = LocalDate.now().plusDays(30);

        // Création du tour
        Tour tour = Tour.builder()
                .tontine(tontine)
                .beneficiaire(beneficiaire)
                .numeroTour(numeroProchainTour)
                .datePrevue(datePrevue)
                .statut("EN_COURS")
                .build();

        Tour savedTour = tourRepository.save(tour);

        // Création des cotisations pour chaque membre actif
        for (MembreTontine membre : membresActifs) {
            Cotisation cotisation = Cotisation.builder()
                    .tour(savedTour)
                    .membreTontine(membre)
                    .montantAttendu(tontine.getMontantCotisation())
                    .dateEcheance(datePrevue)
                    .statut("EN_ATTENTE")
                    .build();
            cotisationRepository.save(cotisation);
        }

        // Notification à tous les membres
        notificationService.notifierNouveauTour(tontine, savedTour, beneficiaire);

        log.info("Tour {} lancé pour la tontine {}. Bénéficiaire : {}",
                numeroProchainTour, idTontine, beneficiaire.getNom());

        return toTourResponse(savedTour, membresActifs.size());
    }

    @Override
    @Transactional
    public void cloturerTour(UUID idTour) {
        Tour tour = tourRepository.findById(idTour)
                .orElseThrow(() -> new IllegalArgumentException("Tour introuvable : " + idTour));

        // Vérification que toutes les cotisations sont payées
        boolean cotisationsManquantes = cotisationRepository
                .existsByTourAndStatutNot(tour, "PAYE");

        if (cotisationsManquantes) {
            throw new IllegalStateException(
                "Impossible de clôturer : des cotisations ne sont pas encore payées"
            );
        }

        tour.setStatut("TERMINE");
        tourRepository.save(tour);

        // Notification au bénéficiaire
        notificationService.notifierCagnottePrete(tour.getBeneficiaire(), tour);
        log.info("Tour {} clôturé. Cagnotte remise à {}", idTour, tour.getBeneficiaire().getNom());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TourResponse> listerTours(UUID idTontine) {
        Tontine tontine = trouverOuEchouer(idTontine);
        long nombreMembres = membreTontineRepository.countByTontineAndStatut(tontine, "ACTIF");
        return tourRepository.findByTontineOrderByNumeroTourAsc(tontine)
                .stream()
                .map(t -> toTourResponse(t, nombreMembres))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TontineResponse changerStatut(UUID idTontine, String statut) {
        Tontine tontine = trouverOuEchouer(idTontine);
        tontine.setStatut(statut);
        return toResponse(tontineRepository.save(tontine));
    }

    // ---- Méthodes utilitaires privées ----

    private Tontine trouverOuEchouer(UUID id) {
        return tontineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tontine introuvable : " + id));
    }

    private TontineResponse toResponse(Tontine t) {
        long membres = membreTontineRepository.countByTontineAndStatut(t, "ACTIF");
        return TontineResponse.builder()
                .idTontine(t.getIdTontine())
                .nom(t.getNom())
                .montantCotisation(t.getMontantCotisation())
                .devise(t.getDevise().getCode())
                .statut(t.getStatut())
                .organisationId(t.getOrganisation().getIdOrganisation())
                .organisationNom(t.getOrganisation().getNom())
                .agentGestionnaireId(t.getAgentGestionnaire().getIdUtilisateur())
                .agentGestionnaireNom(t.getAgentGestionnaire().getNom())
                .nombreMembres(membres)
                .build();
    }

    private TourResponse toTourResponse(Tour t, long nombreMembres) {
        return TourResponse.builder()
                .idTour(t.getIdTour())
                .nomTontine(t.getTontine().getNom())
                .numeroTour(t.getNumeroTour())
                .nomBeneficiaire(t.getBeneficiaire().getNom())
                .telephoneBeneficiaire(t.getBeneficiaire().getTelephone())
                .datePrevue(t.getDatePrevue())
                .statut(t.getStatut())
                .nombreTotalMembres(nombreMembres)
                .build();
    }
}
