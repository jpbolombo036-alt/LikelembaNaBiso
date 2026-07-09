package com.example.demo.controller;

import com.example.demo.Dto.response.PenaliteResponse;
import com.example.demo.Service.PenaliteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Controller REST pour la gestion des pénalités.
 *
 * Base URL : /api/penalites
 *
 * Endpoints :
 *   GET   /api/penalites?membreId={uuid}     → Pénalités d'un membre
 *   POST  /api/penalites/{id}/payer          → Marquer une pénalité comme payée
 *   PATCH /api/penalites/{id}/dispenser      → Dispenser un membre (cas exceptionnel)
 *   GET   /api/penalites/{membreId}/total    → Montant total des pénalités en attente
 */
@RestController
@RequestMapping("/api/penalites")
@RequiredArgsConstructor
public class PenaliteController {

    private final PenaliteService penaliteService;

    /**
     * GET /api/penalites?membreId={uuid}
     * Retourne toutes les pénalités d'un membre (historique complet).
     */
    @GetMapping
    public ResponseEntity<List<PenaliteResponse>> listerParMembre(@RequestParam UUID membreId) {
        return ResponseEntity.ok(penaliteService.listerParMembre(membreId));
    }

    /**
     * POST /api/penalites/{id}/payer
     * Marque une pénalité comme payée (après réception du règlement).
     */
    @PostMapping("/{id}/payer")
    public ResponseEntity<PenaliteResponse> marquerPayee(@PathVariable UUID id) {
        return ResponseEntity.ok(penaliteService.marquerPayee(id));
    }

    /**
     * PATCH /api/penalites/{id}/dispenser
     * Dispense un membre d'une pénalité (décision exceptionnelle du gestionnaire).
     * Ex : force majeure, maladie, décision collective du groupe.
     */
    @PatchMapping("/{id}/dispenser")
    public ResponseEntity<PenaliteResponse> dispenser(@PathVariable UUID id) {
        return ResponseEntity.ok(penaliteService.dispenser(id));
    }

    /**
     * GET /api/penalites/{membreId}/total
     * Retourne le montant total des pénalités impayées d'un membre.
     * Utile pour afficher le solde dû sur le tableau de bord du membre.
     */
    @GetMapping("/{membreId}/total")
    public ResponseEntity<BigDecimal> calculerTotal(@PathVariable UUID membreId) {
        return ResponseEntity.ok(penaliteService.calculerTotalPenalitesEnAttente(membreId));
    }
}
