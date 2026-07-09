package com.example.demo.Service.impl;

import com.example.demo.Dto.response.*;
import com.example.demo.Entity.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UtilisateurRepository utilisateurRepository;
    private final MembreOrganisationRepository membreOrganisationRepository;
    private final TontineRepository tontineRepository;
    private final MembreTontineRepository membreTontineRepository;
    private final CotisationRepository cotisationRepository;
    private final TransactionRepository transactionRepository;
    private final CreditRepository creditRepository;
    private final PenaliteRepository penaliteRepository;
    private final NotificationRepository notificationRepository;
    private final TourRepository tourRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardRoleStats obtenirDashboard(UUID utilisateurId, String role) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + utilisateurId));

        List<MembreOrganisation> membres = membreOrganisationRepository.findByUtilisateur(utilisateur);
        String organisationNom = membres.isEmpty() ? null : membres.get(0).getOrganisation().getNom();

        long nombreTontinesActives = tontineRepository.findByStatut("EN_COURS").size();
        long nombreTontinesTerminees = tontineRepository.findByStatut("TERMINEE").size();
        long nombreToursPlanifies = tourRepository.findByStatut("PLANIFIE").size();
        long nombreToursEnCours = tourRepository.findByStatut("EN_COURS").size();

        BigDecimal montantTotalCotisations = transactionRepository.findByStatut("REUSSIE").stream()
                .map(Transaction::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montantTotalRetards = penaliteRepository.findByStatut("EN_ATTENTE").stream()
                .map(Penalite::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        StatsGlobales globales = StatsGlobales.builder()
                .nombreMembres(membres.isEmpty() ? 0 : membreOrganisationRepository.countByOrganisation(membres.get(0).getOrganisation()))
                .nombreTontinesActives(nombreTontinesActives)
                .nombreTontinesTerminees(nombreTontinesTerminees)
                .montantTotalCotisations(montantTotalCotisations)
                .montantTotalRetards(montantTotalRetards)
                .nombreNotificationsNonLues(notificationRepository.countByDestinataireAndStatut(utilisateur, "EN_ATTENTE"))
                .build();

        long nombreTontines = nombreTontinesActives + nombreTontinesTerminees;
        BigDecimal montantTotalCredit = creditRepository.findAll().stream()
                .map(Credit::getMontantPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        StatsTontines tontines = StatsTontines.builder()
                .nombreTontines(nombreTontines)
                .montantTotalCotisations(montantTotalCotisations)
                .montantTotalCredit(montantTotalCredit)
                .nombreToursPlanifies(nombreToursPlanifies)
                .nombreToursEnCours(nombreToursEnCours)
                .build();

        List<Cotisation> cotisations = cotisationRepository.findAll();
        BigDecimal totalAttendu = cotisations.stream()
                .map(Cotisation::getMontantAttendu)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPaye = cotisations.stream()
                .map(c -> c.getMontantPaye() != null ? c.getMontantPaye() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        StatsCotisations cotisationsStats = StatsCotisations.builder()
                .montantAttendu(totalAttendu)
                .montantPaye(totalPaye)
                .montantRetard(totalAttendu.subtract(totalPaye).max(BigDecimal.ZERO))
                .nombreEnAttente(cotisations.stream().filter(c -> "EN_ATTENTE".equals(c.getStatut())).count())
                .nombrePayees(cotisations.stream().filter(c -> "PAYE".equals(c.getStatut())).count())
                .nombrePartielles(cotisations.stream().filter(c -> "PARTIEL".equals(c.getStatut())).count())
                .nombreRetards(cotisations.stream().filter(c -> "RETARD".equals(c.getStatut())).count())
                .build();

        List<Credit> credits = creditRepository.findAll();
        BigDecimal totalPrincipal = credits.stream()
                .map(Credit::getMontantPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalRembourse = credits.stream()
                .map(c -> c.getMontantRembourse() != null ? c.getMontantRembourse() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDu = credits.stream()
                .map(Credit::getMontantTotalDu)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        StatsCredits statsCredits = StatsCredits.builder()
                .nombreCreditsActifs(credits.stream().filter(c -> "ACTIF".equals(c.getStatut())).count())
                .montantTotalPrincipal(totalPrincipal)
                .montantTotalRembourse(totalRembourse)
                .montantTotalDu(totalDu)
                .nombreCreditsEnRetard(credits.stream().filter(c -> "EN_RETARD".equals(c.getStatut())).count())
                .build();

        List<Alerte> alertes = new ArrayList<>();

        long retardsNonPayes = penaliteRepository.findByStatut("EN_ATTENTE").size();
        if (retardsNonPayes > 0) {
            alertes.add(Alerte.builder()
                    .type("RETARD")
                    .message(retardsNonPayes + " penalite(s) en attente de paiement")
                    .severite("WARN")
                    .date(LocalDate.now())
                    .montant(montantTotalRetards)
                    .build());
        }

        long creditsEnRetard = credits.stream().filter(c -> "EN_RETARD".equals(c.getStatut())).count();
        if (creditsEnRetard > 0) {
            alertes.add(Alerte.builder()
                    .type("CREDIT")
                    .message(creditsEnRetard + " credit(s) en retard")
                    .severite("ERROR")
                    .date(LocalDate.now())
                    .build());
        }

        List<ProchaineEcheance> prochainesEcheances = new ArrayList<>();
        List<Tour> toursPlanifies = tourRepository.findByStatut("PLANIFIE");
        for (Tour tour : toursPlanifies) {
            prochainesEcheances.add(ProchaineEcheance.builder()
                    .id(tour.getIdTour())
                    .type("TOUR")
                    .libelle("Tour " + tour.getNumeroTour() + " - " + tour.getBeneficiaire().getNom())
                    .montant(tour.getTontine().getMontantCotisation())
                    .dateEcheance(tour.getDatePrevue())
                    .statut(tour.getStatut())
                    .build());
        }

        return DashboardRoleStats.builder()
                .role(role)
                .utilisateurNom(utilisateur.getNom())
                .organisationNom(organisationNom)
                .statsGlobales(globales)
                .statsTontines(tontines)
                .statsCotisations(cotisationsStats)
                .statsCredits(statsCredits)
                .alertes(alertes)
                .prochainesEcheances(prochainesEcheances)
                .build();
    }
}
