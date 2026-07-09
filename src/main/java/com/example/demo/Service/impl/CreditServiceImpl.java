package com.example.demo.Service.impl;

import com.example.demo.Dto.request.CreditRequest;
import com.example.demo.Dto.response.CreditResponse;
import com.example.demo.Entity.Credit;
import com.example.demo.Entity.Tontine;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Repository.CreditRepository;
import com.example.demo.Repository.TontineRepository;
import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.Service.CreditService;
import com.example.demo.Service.DeviseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des micro-crédits rotatifs.
 *
 * Formule de calcul du montant total dû :
 * montantTotalDu = principal × (1 + tauxMensuel/100) ^ dureeMois
 * (intérêts composés mensuellement)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreditServiceImpl implements CreditService {

    private final CreditRepository creditRepository;
    private final TontineRepository tontineRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final DeviseService deviseService;

    @Override
    @Transactional
    public CreditResponse accorderCredit(CreditRequest request, UUID idApprobateur) {
        log.info("Accord de crédit pour {} dans la tontine {}", request.getEmprunteurId(), request.getTontineId());

        Tontine tontine = tontineRepository.findById(request.getTontineId())
                .orElseThrow(() -> new IllegalArgumentException("Tontine introuvable : " + request.getTontineId()));

        Utilisateur emprunteur = utilisateurRepository.findById(request.getEmprunteurId())
                .orElseThrow(() -> new IllegalArgumentException("Emprunteur introuvable : " + request.getEmprunteurId()));

        Utilisateur approbateur = utilisateurRepository.findById(idApprobateur)
                .orElseThrow(() -> new IllegalArgumentException("Approbateur introuvable : " + idApprobateur));

        // Vérification : pas de crédit actif ou en attente pour cet emprunteur dans cette tontine
        if (creditRepository.existsByTontineAndEmprunteurAndStatutIn(
                tontine, emprunteur, Arrays.asList("ACTIF", "EN_ATTENTE"))) {
            throw new IllegalStateException(
                "Cet emprunteur a déjà un crédit actif dans cette tontine"
            );
        }

        // Calcul du montant total dû avec intérêts composés
        // Formule : P × (1 + r)^n  où r = taux mensuel / 100, n = durée en mois
        BigDecimal tauxDecimal = request.getTauxInteretMensuel()
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal facteur = BigDecimal.ONE.add(tauxDecimal)
                .pow(request.getDureeMois());
        BigDecimal montantTotalDu = request.getMontantPrincipal()
                .multiply(facteur)
                .setScale(2, RoundingMode.HALF_UP);

        Credit credit = Credit.builder()
                .tontine(tontine)
                .emprunteur(emprunteur)
                .approvePar(approbateur)
                .montantPrincipal(request.getMontantPrincipal())
                .tauxInteretMensuel(request.getTauxInteretMensuel())
                .dureeMois(request.getDureeMois())
                .montantTotalDu(montantTotalDu)
                .montantRembourse(BigDecimal.ZERO)
                .devise(deviseService.resoudre(request.getDevise()))
                .dateOctroi(LocalDate.now())
                .dateEcheanceFinale(LocalDate.now().plusMonths(request.getDureeMois()))
                .statut("ACTIF")
                .build();

        Credit saved = creditRepository.save(credit);
        log.info("Crédit accordé : {} {} à {} (total dû : {})",
                request.getMontantPrincipal(), request.getDevise(),
                emprunteur.getNom(), montantTotalDu);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CreditResponse obtenirParId(UUID idCredit) {
        return toResponse(trouverOuEchouer(idCredit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditResponse> listerParTontine(UUID idTontine) {
        Tontine tontine = tontineRepository.findById(idTontine)
                .orElseThrow(() -> new IllegalArgumentException("Tontine introuvable : " + idTontine));
        return creditRepository.findByTontineAndStatut(tontine, "ACTIF")
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditResponse> listerParEmprunteur(UUID idUtilisateur) {
        Utilisateur emprunteur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + idUtilisateur));
        return creditRepository.findByEmprunteur(emprunteur)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CreditResponse enregistrerRemboursement(UUID idCredit, BigDecimal montant) {
        Credit credit = trouverOuEchouer(idCredit);

        BigDecimal nouveauMontantRembourse = credit.getMontantRembourse().add(montant);

        // Vérification que le remboursement ne dépasse pas le total dû
        if (nouveauMontantRembourse.compareTo(credit.getMontantTotalDu()) > 0) {
            throw new IllegalArgumentException(
                "Le montant remboursé dépasse le total dû. Restant : " +
                credit.getMontantTotalDu().subtract(credit.getMontantRembourse())
            );
        }

        credit.setMontantRembourse(nouveauMontantRembourse);

        // Si le crédit est entièrement remboursé, le clôturer
        if (nouveauMontantRembourse.compareTo(credit.getMontantTotalDu()) == 0) {
            credit.setStatut("REMBOURSE");
            log.info("Crédit {} entièrement remboursé !", idCredit);
        }

        return toResponse(creditRepository.save(credit));
    }

    @Override
    @Transactional
    public void marquerEnDefaut(UUID idCredit) {
        Credit credit = trouverOuEchouer(idCredit);
        credit.setStatut("DEFAUT");
        creditRepository.save(credit);
        log.warn("Crédit {} marqué en DEFAUT", idCredit);
    }

    // ---- Méthodes utilitaires ----

    private Credit trouverOuEchouer(UUID id) {
        return creditRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Crédit introuvable : " + id));
    }

    private CreditResponse toResponse(Credit c) {
        BigDecimal restant = c.getMontantTotalDu().subtract(c.getMontantRembourse());
        return CreditResponse.builder()
                .idCredit(c.getIdCredit())
                .nomEmprunteur(c.getEmprunteur().getNom())
                .nomTontine(c.getTontine().getNom())
                .montantPrincipal(c.getMontantPrincipal())
                .tauxInteretMensuel(c.getTauxInteretMensuel())
                .dureeMois(c.getDureeMois())
                .montantTotalDu(c.getMontantTotalDu())
                .montantRembourse(c.getMontantRembourse())
                .montantRestant(restant)
                .devise(c.getDevise().getCode())
                .dateOctroi(c.getDateOctroi())
                .dateEcheanceFinale(c.getDateEcheanceFinale())
                .statut(c.getStatut())
                .build();
    }
}
