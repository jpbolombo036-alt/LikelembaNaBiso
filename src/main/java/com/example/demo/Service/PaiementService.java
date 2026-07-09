package com.example.demo.Service;

import com.example.demo.Dto.request.ConfirmationCashRequest;
import com.example.demo.Dto.request.PaiementCashRequest;
import com.example.demo.Dto.request.PaiementMobileMoneyRequest;
import com.example.demo.Dto.response.TransactionResponse;

import java.util.List;
import java.util.UUID;

public interface PaiementService {

    TransactionResponse initierPaiement(PaiementMobileMoneyRequest request);

    void traiterCallbackOperateur(String referenceOperateur, String statut, String messageOperateur);

    TransactionResponse verifierStatut(UUID idTransaction);

    /**
     * Membre déclare un paiement cash pour régler une cotisation.
     */
    TransactionResponse declarerPaiementCash(PaiementCashRequest request, UUID payeurId);

    /**
     * Agent gestionnaire confirme ou rejette un paiement cash.
     */
    TransactionResponse confirmerPaiementCash(ConfirmationCashRequest request, UUID agentId);

    /**
     * Retourne les déclarations cash en attente pour une tontine donnée.
     */
    List<TransactionResponse> getDeclarationsCashEnAttente(UUID tontineId);
}
