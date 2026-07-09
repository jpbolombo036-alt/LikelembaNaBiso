package com.example.demo.controller;

import com.example.demo.Dto.request.TontineRequest;
import com.example.demo.Dto.response.TontineResponse;
import com.example.demo.Dto.response.TourResponse;
import com.example.demo.Service.TontineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST pour la gestion des tontines.
 * C'est le cœur fonctionnel de Likelamba.
 *
 * Base URL : /api/tontines
 *
 * Endpoints :
 *   POST   /api/tontines                            → Créer une tontine
 *   GET    /api/tontines/{id}                       → Détail d'une tontine
 *   GET    /api/tontines?organisationId={uuid}      → Lister les tontines d'une organisation
 *   POST   /api/tontines/{id}/membres               → Inscrire un membre
 *   DELETE /api/tontines/{id}/membres/{userId}      → Retirer un membre
 *   POST   /api/tontines/{id}/tours/lancer          → Lancer le prochain tour
 *   POST   /api/tontines/tours/{tourId}/cloturer    → Clôturer un tour
 *   GET    /api/tontines/{id}/tours                 → Liste des tours
 *   PATCH  /api/tontines/{id}/statut                → Changer le statut
 */
@RestController
@RequestMapping("/api/tontines")
@RequiredArgsConstructor
public class TontineController {

    private final TontineService tontineService;

    /** POST /api/tontines — Crée une nouvelle tontine. */
    @PostMapping
    public ResponseEntity<TontineResponse> creer(@Valid @RequestBody TontineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tontineService.creer(request));
    }

    /** GET /api/tontines/{id} — Retourne le détail d'une tontine. */
    @GetMapping("/{id}")
    public ResponseEntity<TontineResponse> obtenirParId(@PathVariable UUID id) {
        return ResponseEntity.ok(tontineService.obtenirParId(id));
    }

    /**
     * GET /api/tontines?organisationId={uuid}
     * Liste les tontines actives d'une organisation.
     */
    @GetMapping
    public ResponseEntity<List<TontineResponse>> lister(@RequestParam UUID organisationId) {
        return ResponseEntity.ok(tontineService.listerParOrganisation(organisationId));
    }

    /**
     * POST /api/tontines/{id}/membres?utilisateurId={uuid}&ordrePassage={n}
     * Inscrit un utilisateur à la tontine avec son ordre de passage.
     */
    @PostMapping("/{id}/membres")
    public ResponseEntity<Void> inscrireMembre(
            @PathVariable UUID id,
            @RequestParam UUID utilisateurId,
            @RequestParam Integer ordrePassage) {
        tontineService.inscrireMembre(id, utilisateurId, ordrePassage);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** DELETE /api/tontines/{id}/membres/{userId} — Retire un membre (statut SORTI). */
    @DeleteMapping("/{id}/membres/{userId}")
    public ResponseEntity<Void> retirerMembre(
            @PathVariable UUID id,
            @PathVariable UUID userId) {
        tontineService.retirerMembre(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/tontines/{id}/tours/lancer
     * Lance le prochain tour de la tontine.
     * Calcule automatiquement le bénéficiaire et crée les cotisations.
     * Envoie des SMS à tous les membres.
     */
    @PostMapping("/{id}/tours/lancer")
    public ResponseEntity<TourResponse> lancerProchainTour(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tontineService.lancerProchainTour(id));
    }

    /**
     * POST /api/tontines/tours/{tourId}/cloturer
     * Clôture un tour après vérification que toutes les cotisations sont payées.
     */
    @PostMapping("/tours/{tourId}/cloturer")
    public ResponseEntity<Void> cloturerTour(@PathVariable UUID tourId) {
        tontineService.cloturerTour(tourId);
        return ResponseEntity.ok().build();
    }

    /** GET /api/tontines/{id}/tours — Liste tous les tours d'une tontine. */
    @GetMapping("/{id}/tours")
    public ResponseEntity<List<TourResponse>> listerTours(@PathVariable UUID id) {
        return ResponseEntity.ok(tontineService.listerTours(id));
    }

    /**
     * PATCH /api/tontines/{id}/statut?valeur=SUSPENDUE
     * Change le statut d'une tontine.
     */
    @PatchMapping("/{id}/statut")
    public ResponseEntity<TontineResponse> changerStatut(
            @PathVariable UUID id,
            @RequestParam String valeur) {
        return ResponseEntity.ok(tontineService.changerStatut(id, valeur));
    }
}
