package com.example.demo.controller;

import com.example.demo.Dto.request.ConfirmationCashRequest;
import com.example.demo.Dto.request.PaiementCashRequest;
import com.example.demo.Dto.request.PaiementMobileMoneyRequest;
import com.example.demo.Dto.response.TransactionResponse;
import com.example.demo.Service.PaiementService;
import com.example.demo.Security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller REST pour les paiements Mobile Money et Cash.
 *
 * Base URL : /api/paiements
 *
 * Endpoints :
 *   POST /api/paiements/initier              → Initier un paiement Mobile Money
 *   POST /api/paiements/callback             → Webhook appelé par l'opérateur (CinetPay/Flutterwave)
 *   GET  /api/paiements/{transactionId}      → Vérifier le statut d'une transaction
 *   POST /api/paiements/cash/declarer        → Membre déclare un paiement cash
 *   POST /api/paiements/cash/confirmer       → Agent gestionnaire confirme/rejette un cash
 *   GET  /api/paiements/cash/en-attente/{tontineId} → Agent voit les déclarations en attente
 *
 * ⚠️ L'endpoint /callback NE doit PAS être protégé par JWT.
 * Il est appelé par l'opérateur Mobile Money, pas par l'utilisateur.
 * Sa sécurité repose sur une signature HMAC vérifiée côté service.
 */
@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
@Slf4j
public class PaiementController {

    private final PaiementService paiementService;
    private final JwtService jwtService;

    /**
     * POST /api/paiements/initier
     * Initie un paiement Mobile Money pour régler une cotisation.
     */
    @PostMapping("/initier")
    public ResponseEntity<TransactionResponse> initierPaiement(
            @Valid @RequestBody PaiementMobileMoneyRequest request) {
        TransactionResponse response = paiementService.initierPaiement(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * POST /api/paiements/callback
     * Webhook appelé par CinetPay ou Flutterwave après traitement du paiement.
     */
    @PostMapping("/callback")
    public ResponseEntity<Void> traiterCallback(@RequestBody Map<String, String> payload) {
        log.info("Callback Mobile Money reçu : {}", payload);

        String referenceOperateur = payload.get("reference_operateur");
        String statut = payload.get("statut");
        String message = payload.getOrDefault("message", "");

        if (referenceOperateur == null || statut == null) {
            log.warn("Callback invalide : champs manquants");
            return ResponseEntity.badRequest().build();
        }

        paiementService.traiterCallbackOperateur(referenceOperateur, statut, message);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/paiements/{transactionId}
     * Vérifie le statut d'une transaction.
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> verifierStatut(@PathVariable UUID transactionId) {
        return ResponseEntity.ok(paiementService.verifierStatut(transactionId));
    }

    /**
     * POST /api/paiements/cash/declarer
     * Membre déclare un paiement cash pour régler une cotisation.
     */
    @PostMapping("/cash/declarer")
    public ResponseEntity<TransactionResponse> declarerPaiementCash(
            @Valid @RequestBody PaiementCashRequest request,
            HttpServletRequest httpRequest) {
        UUID payeurId = extraireUtilisateurId(httpRequest);
        TransactionResponse response = paiementService.declarerPaiementCash(request, payeurId);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/paiements/cash/confirmer
     * Agent gestionnaire confirme ou rejette une déclaration de paiement cash.
     */
    @PostMapping("/cash/confirmer")
    public ResponseEntity<TransactionResponse> confirmerPaiementCash(
            @Valid @RequestBody ConfirmationCashRequest request,
            HttpServletRequest httpRequest) {
        UUID agentId = extraireUtilisateurId(httpRequest);
        TransactionResponse response = paiementService.confirmerPaiementCash(request, agentId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/paiements/cash/en-attente/{tontineId}
     * Retourne les déclarations cash en attente pour une tontine donnée.
     */
    @GetMapping("/cash/en-attente/{tontineId}")
    public ResponseEntity<List<TransactionResponse>> getDeclarationsCashEnAttente(@PathVariable UUID tontineId) {
        List<TransactionResponse> response = paiementService.getDeclarationsCashEnAttente(tontineId);
        return ResponseEntity.ok(response);
    }

    private UUID extraireUtilisateurId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token JWT manquant ou invalide");
        }
        String token = authHeader.substring(7);
        return jwtService.extraireIdUtilisateur(token);
    }
}
