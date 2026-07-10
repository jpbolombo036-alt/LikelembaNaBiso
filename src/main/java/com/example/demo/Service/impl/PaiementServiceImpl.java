package com.example.demo.Service.impl;

import com.example.demo.Dto.request.ConfirmationCashRequest;
import com.example.demo.Dto.request.PaiementCashRequest;
import com.example.demo.Dto.request.PaiementMobileMoneyRequest;
import com.example.demo.Dto.response.TransactionResponse;
import com.example.demo.Entity.Cotisation;
import com.example.demo.Entity.MembreTontine;
import com.example.demo.Entity.Tontine;
import com.example.demo.Entity.Transaction;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Enum.ModePaiement;
import com.example.demo.Repository.CotisationRepository;
import com.example.demo.Repository.MembreTontineRepository;
import com.example.demo.Repository.TontineRepository;
import com.example.demo.Repository.TransactionRepository;
import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.Service.DeviseService;
import com.example.demo.Service.NotificationService;
import com.example.demo.Service.PaiementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implémentation du service de paiement Mobile Money et Cash.
 *
 * ⚠️ INTÉGRATION EXTERNE REQUISE :
 * Cette implémentation contient des TODO pour l'appel aux APIs externes :
 * - CinetPay API : https://developer.cinetpay.com/
 * - Flutterwave API : https://developer.flutterwave.com/
 *
 * Les appels API réels seront implémentés dans un client dédié
 * (ex: CinetPayClient ou FlutterwaveClient) injecté ici.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaiementServiceImpl implements PaiementService {

    private final CotisationRepository cotisationRepository;
    private final TransactionRepository transactionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;
    private final TontineRepository tontineRepository;
    private final MembreTontineRepository membreTontineRepository;
    private final DeviseService deviseService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TransactionResponse initierPaiement(PaiementMobileMoneyRequest request) {
        log.info("Initiation paiement {} via {} pour cotisation {}",
                request.getMontant(), request.getOperateur(), request.getCotisationId());

        Cotisation cotisation = cotisationRepository.findById(request.getCotisationId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Cotisation introuvable : " + request.getCotisationId()
                ));

        if ("PAYE".equals(cotisation.getStatut())) {
            throw new IllegalStateException("Cette cotisation est déjà réglée");
        }

        if (transactionRepository.existsByCotisationAndStatut(cotisation, "REUSSIE")) {
            throw new IllegalStateException("Un paiement réussi existe déjà pour cette cotisation");
        }

        Transaction transaction = Transaction.builder()
                .cotisation(cotisation)
                .payeur(cotisation.getMembreTontine().getUtilisateur())
                .operateur(request.getOperateur())
                .modePaiement(ModePaiement.MOBILE_MONEY)
                .montant(request.getMontant())
                .devise(deviseService.resoudre(request.getDevise()))
                .numeroTelephonePayeur(request.getNumeroTelephone())
                .statut("INITIEE")
                .build();

        Transaction saved = transactionRepository.save(transaction);

        saved.setStatut("EN_ATTENTE");
        transactionRepository.save(saved);

        log.info("Transaction {} créée en statut EN_ATTENTE", saved.getIdTransaction());
        return toResponse(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void traiterCallbackOperateur(String referenceOperateur, String statut, String messageOperateur) {
        log.info("Callback reçu de l'opérateur : ref={}, statut={}", referenceOperateur, statut);

        Transaction transaction = transactionRepository
                .findByReferenceOperateur(referenceOperateur)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Transaction introuvable pour la référence : " + referenceOperateur
                ));

        transaction.setStatut(statut);
        transaction.setMessageOperateur(messageOperateur);

        if ("REUSSIE".equals(statut)) {
            transaction.setDateConfirmation(LocalDateTime.now());
            transactionRepository.save(transaction);

            Cotisation cotisation = transaction.getCotisation();
            cotisation.setMontantPaye(transaction.getMontant());
            cotisation.setStatut("PAYE");
            cotisationRepository.save(cotisation);

            log.info("Cotisation {} marquée comme PAYEE suite au callback", cotisation.getIdCotisation());

        } else if ("ECHOUEE".equals(statut)) {
            transactionRepository.save(transaction);
            log.warn("Transaction {} échouée : {}", transaction.getIdTransaction(), messageOperateur);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse verifierStatut(UUID idTransaction) {
        Transaction transaction = transactionRepository.findById(idTransaction)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Transaction introuvable : " + idTransaction
                ));

        return toResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse declarerPaiementCash(PaiementCashRequest request, UUID payeurId) {
        log.info("Déclaration paiement cash {} {} pour cotisation {} par membre {}",
                request.getMontant(), request.getDevise(), request.getCotisationId(), payeurId);

        Cotisation cotisation = cotisationRepository.findById(request.getCotisationId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Cotisation introuvable : " + request.getCotisationId()
                ));

        if ("PAYE".equals(cotisation.getStatut())) {
            throw new IllegalStateException("Cette cotisation est déjà réglée");
        }

        if (transactionRepository.existsByCotisationAndStatut(cotisation, "REUSSIE")) {
            throw new IllegalStateException("Un paiement réussi existe déjà pour cette cotisation");
        }

        Utilisateur membre = utilisateurRepository.findById(payeurId)
                .orElseThrow(() -> new IllegalArgumentException("Membre introuvable : " + payeurId));

        if (!cotisation.getMembreTontine().getUtilisateur().getIdUtilisateur().equals(payeurId)) {
            throw new IllegalAccessError("Vous n'êtes pas autorisé à déclarer un paiement pour cette cotisation");
        }

        Tontine tontine = cotisation.getTour().getTontine();

        Transaction transaction = Transaction.builder()
                .cotisation(cotisation)
                .payeur(membre)
                .operateur("CASH")
                .modePaiement(ModePaiement.CASH)
                .montant(request.getMontant())
                .devise(deviseService.resoudre(request.getDevise()))
                .statut("INITIEE")
                .build();

        Transaction saved = transactionRepository.save(transaction);

        notificationService.notifierDeclarationCash(
            tontine.getAgentGestionnaire(),
            membre.getNom(),
            request.getMontant(),
            request.getDevise(),
            tontine
        );

        log.info("Déclaration cash {} créée pour la cotisation {}", saved.getIdTransaction(), cotisation.getIdCotisation());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public TransactionResponse confirmerPaiementCash(ConfirmationCashRequest request, UUID agentId) {
        log.info("Confirmation cash pour transaction {} : accepter={}", request.getTransactionId(), request.getAccepter());

        Transaction transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Transaction introuvable : " + request.getTransactionId()
                ));

        if (!ModePaiement.CASH.equals(transaction.getModePaiement())) {
            throw new IllegalStateException("Cette transaction n'est pas un paiement cash");
        }

        if (!"INITIEE".equals(transaction.getStatut())) {
            throw new IllegalStateException("Cette transaction n'est plus en attente de confirmation (statut : " + transaction.getStatut() + ")");
        }

        Utilisateur agent = utilisateurRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent introuvable : " + agentId));

        Cotisation cotisation = transaction.getCotisation();
        Tontine tontine = cotisation.getTour().getTontine();

        if (!tontine.getAgentGestionnaire().getIdUtilisateur().equals(agentId)) {
            throw new IllegalAccessError("Seul l'agent gestionnaire de la tontine peut confirmer ce paiement cash");
        }

        if (request.getAccepter()) {
            transaction.setStatut("REUSSIE");
            transaction.setDateConfirmation(LocalDateTime.now());
            transaction.setReferenceOperateur("CASH-" + transaction.getIdTransaction());
            transactionRepository.save(transaction);

            cotisation.setMontantPaye(transaction.getMontant());
            if (transaction.getMontant().compareTo(cotisation.getMontantAttendu()) >= 0) {
                cotisation.setStatut("PAYE");
            } else {
                cotisation.setStatut("PARTIEL");
            }
            cotisation.setConfirmePar(agent);
            cotisationRepository.save(cotisation);

            notificationService.notifierConfirmationCash(
                transaction.getPayeur(),
                true,
                transaction.getMontant(),
                transaction.getDevise().getCode(),
                tontine,
                null
            );

            log.info("Paiement cash {} confirmé par l'agent {}", transaction.getIdTransaction(), agentId);

        } else {
            transaction.setStatut("ECHOUEE");
            transaction.setMessageOperateur(request.getCommentaire() != null ? request.getCommentaire() : "Rejeté par l'agent gestionnaire");
            transactionRepository.save(transaction);

            notificationService.notifierConfirmationCash(
                transaction.getPayeur(),
                false,
                transaction.getMontant(),
                transaction.getDevise().getCode(),
                tontine,
                request.getCommentaire()
            );

            log.info("Paiement cash {} rejeté par l'agent {} : {}", transaction.getIdTransaction(), agentId, request.getCommentaire());
        }

        return toResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getDeclarationsCashEnAttente(UUID tontineId) {
        List<Transaction> transactions = transactionRepository
                .findByStatutAndModePaiementAndCotisation_Tour_Tontine_IdTontineOrderByDateInitiationAsc(
                        "INITIEE", ModePaiement.CASH, tontineId);
        return transactions.stream()
                .map(this::toResponse)
                .toList();
    }

    /** Convertit une entité Transaction en DTO de réponse. */
    private TransactionResponse toResponse(Transaction t) {
        return TransactionResponse.builder()
                .idTransaction(t.getIdTransaction())
                .referenceOperateur(t.getReferenceOperateur())
                .operateur(t.getOperateur())
                .modePaiement(t.getModePaiement())
                .montant(t.getMontant())
                .devise(t.getDevise().getCode())
                .statut(t.getStatut())
                .messageOperateur(t.getMessageOperateur())
                .dateInitiation(t.getDateInitiation())
                .dateConfirmation(t.getDateConfirmation())
                .build();
    }
}
