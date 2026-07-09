package com.example.demo.Service;

import com.example.demo.Dto.request.CreditRequest;
import com.example.demo.Dto.response.CreditResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Interface du service de gestion des micro-crédits rotatifs.
 * Permet aux groupes avancés de faire fonctionner leur tontine en mode crédit avec intérêts.
 */
public interface CreditService {

    /**
     * Accorde un crédit à un membre dans le cadre d'une tontine.
     * Vérifie que le membre n'a pas déjà un crédit actif.
     * Calcule automatiquement le montant total dû (principal + intérêts).
     */
    CreditResponse accorderCredit(CreditRequest request, UUID idApprobateur);

    /** Retourne le détail d'un crédit. */
    CreditResponse obtenirParId(UUID idCredit);

    /** Retourne tous les crédits actifs d'une tontine. */
    List<CreditResponse> listerParTontine(UUID idTontine);

    /** Retourne l'historique des crédits d'un emprunteur. */
    List<CreditResponse> listerParEmprunteur(UUID idUtilisateur);

    /**
     * Enregistre un remboursement partiel ou total d'un crédit.
     * Met à jour le montant remboursé et change le statut à REMBOURSE si soldé.
     *
     * @param idCredit Identifiant du crédit
     * @param montant  Montant remboursé
     */
    CreditResponse enregistrerRemboursement(UUID idCredit, BigDecimal montant);

    /** Marque un crédit en DEFAUT (non remboursé après échéance). */
    void marquerEnDefaut(UUID idCredit);
}
