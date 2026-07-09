package com.example.demo.controller;

import com.example.demo.Dto.request.CreditRequest;
import com.example.demo.Dto.response.CreditResponse;
import com.example.demo.Service.CreditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Controller REST pour la gestion des micro-crédits rotatifs.
 *
 * Base URL : /api/credits
 *
 * Endpoints :
 *   POST  /api/credits                                    → Accorder un crédit
 *   GET   /api/credits/{id}                              → Détail d'un crédit
 *   GET   /api/credits?tontineId={uuid}                  → Crédits actifs d'une tontine
 *   GET   /api/credits?emprunteurId={uuid}               → Historique d'un emprunteur
 *   POST  /api/credits/{id}/rembourser?montant={valeur}  → Enregistrer un remboursement
 *   PATCH /api/credits/{id}/defaut                       → Marquer en défaut
 */
@RestController
@RequestMapping("/api/credits")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    /**
     * POST /api/credits?approbateurId={uuid}
     * Accorde un crédit à un membre. Requiert l'ID de l'agent approbateur.
     */
    @PostMapping
    public ResponseEntity<CreditResponse> accorder(
            @Valid @RequestBody CreditRequest request,
            @RequestParam UUID approbateurId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(creditService.accorderCredit(request, approbateurId));
    }

    /** GET /api/credits/{id} — Retourne le détail d'un crédit. */
    @GetMapping("/{id}")
    public ResponseEntity<CreditResponse> obtenirParId(@PathVariable UUID id) {
        return ResponseEntity.ok(creditService.obtenirParId(id));
    }

    /**
     * GET /api/credits?tontineId={uuid}    → Crédits actifs de la tontine
     * GET /api/credits?emprunteurId={uuid} → Historique de l'emprunteur
     */
    @GetMapping
    public ResponseEntity<List<CreditResponse>> lister(
            @RequestParam(required = false) UUID tontineId,
            @RequestParam(required = false) UUID emprunteurId) {

        if (tontineId != null) {
            return ResponseEntity.ok(creditService.listerParTontine(tontineId));
        } else if (emprunteurId != null) {
            return ResponseEntity.ok(creditService.listerParEmprunteur(emprunteurId));
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * POST /api/credits/{id}/rembourser?montant=50000
     * Enregistre un remboursement (partiel ou total) sur un crédit.
     */
    @PostMapping("/{id}/rembourser")
    public ResponseEntity<CreditResponse> rembourser(
            @PathVariable UUID id,
            @RequestParam BigDecimal montant) {
        return ResponseEntity.ok(creditService.enregistrerRemboursement(id, montant));
    }

    /**
     * PATCH /api/credits/{id}/defaut
     * Marque un crédit en état de défaut (non remboursé après échéance).
     */
    @PatchMapping("/{id}/defaut")
    public ResponseEntity<Void> marquerEnDefaut(@PathVariable UUID id) {
        creditService.marquerEnDefaut(id);
        return ResponseEntity.ok().build();
    }
}
