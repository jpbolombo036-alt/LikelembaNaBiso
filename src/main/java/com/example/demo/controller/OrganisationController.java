package com.example.demo.controller;

import com.example.demo.Dto.request.OrganisationRequest;
import com.example.demo.Dto.response.OrganisationResponse;
import com.example.demo.Service.OrganisationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST pour la gestion des organisations.
 *
 * Base URL : /api/organisations
 *
 * Endpoints :
 *   POST   /api/organisations                          → Créer une organisation
 *   GET    /api/organisations/{id}                     → Détail d'une organisation
 *   PUT    /api/organisations/{id}                     → Modifier une organisation
 *   GET    /api/organisations?ville=Kinshasa           → Lister par ville
 *   POST   /api/organisations/{id}/membres             → Ajouter un membre
 *   DELETE /api/organisations/{id}/membres/{userId}    → Retirer un membre
 *   PATCH  /api/organisations/{id}/statut              → Changer le statut
 */
@RestController
@RequestMapping("/api/organisations")
@RequiredArgsConstructor
public class OrganisationController {

    private final OrganisationService organisationService;

    /** POST /api/organisations — Crée une nouvelle organisation. */
    @PostMapping
    public ResponseEntity<OrganisationResponse> creer(@Valid @RequestBody OrganisationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(organisationService.creer(request));
    }

    /** GET /api/organisations/{id} — Retourne le détail d'une organisation. */
    @GetMapping("/{id}")
    public ResponseEntity<OrganisationResponse> obtenirParId(@PathVariable UUID id) {
        return ResponseEntity.ok(organisationService.obtenirParId(id));
    }

    /** PUT /api/organisations/{id} — Met à jour une organisation. */
    @PutMapping("/{id}")
    public ResponseEntity<OrganisationResponse> modifier(
            @PathVariable UUID id,
            @Valid @RequestBody OrganisationRequest request) {
        return ResponseEntity.ok(organisationService.modifier(id, request));
    }

    /**
     * GET /api/organisations?ville=Kinshasa — Liste les organisations actives.
     * Si le paramètre ville est fourni, filtre par ville.
     */
    @GetMapping
    public ResponseEntity<List<OrganisationResponse>> lister(
            @RequestParam(required = false) String ville) {
        List<OrganisationResponse> liste = (ville != null && !ville.isBlank())
                ? organisationService.listerParVille(ville)
                : organisationService.listerActives();
        return ResponseEntity.ok(liste);
    }

    /**
     * POST /api/organisations/{id}/membres
     * Body : { "utilisateurId": "uuid", "role": "MEMBRE" }
     * Ajoute un utilisateur comme membre de l'organisation.
     */
    @PostMapping("/{id}/membres")
    public ResponseEntity<Void> ajouterMembre(
            @PathVariable UUID id,
            @RequestParam UUID utilisateurId,
            @RequestParam String role) {
        organisationService.ajouterMembre(id, utilisateurId, role);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** DELETE /api/organisations/{id}/membres/{userId} — Retire un membre. */
    @DeleteMapping("/{id}/membres/{userId}")
    public ResponseEntity<Void> retirerMembre(
            @PathVariable UUID id,
            @PathVariable UUID userId) {
        organisationService.retirerMembre(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/organisations/{id}/statut?valeur=SUSPENDU
     * Change le statut d'une organisation (ACTIF / INACTIF / SUSPENDU).
     */
    @PatchMapping("/{id}/statut")
    public ResponseEntity<OrganisationResponse> changerStatut(
            @PathVariable UUID id,
            @RequestParam String valeur) {
        return ResponseEntity.ok(organisationService.changerStatut(id, valeur));
    }
}
