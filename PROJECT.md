# 📋 LIKELAMBA — Documentation Complète du Projet

> **À lire en premier par tout développeur ou IA qui rejoint ce projet.**
> Ce document donne la vue globale du projet, son contexte métier, son architecture technique et l'état d'avancement.

---

## 🌍 Contexte & Vision

**Likelamba** est une plateforme de gestion numérique de tontines (*likelemba*) et de micro-crédits rotatifs, conçue spécifiquement pour le contexte de la **République Démocratique du Congo (RDC)**.

### Le problème résolu
En RDC, les tontines sont omniprésentes : entre collègues, membres d'église, marchés, quartiers. Mais **tout se gère encore sur cahier ou WhatsApp**, ce qui crée :
- Des conflits (qui a payé ? qui doit recevoir ?)
- Des erreurs de calcul
- De la fraude et des mésententes qui cassent des groupes entiers

### La solution
Une application qui **digitalise, automatise et sécurise** la gestion des tontines :
- Un responsable crée son groupe et invite les membres par numéro de téléphone
- Chaque membre cotise via **Mobile Money** (Airtel Money, Orange Money, M-Pesa Vodacom)
- L'app calcule automatiquement qui reçoit le tour
- Des **rappels SMS** sont envoyés avant chaque échéance
- Un **historique transparent** est visible par tous (évite les disputes)
- Version **micro-crédit rotatif** avec gestion des intérêts

---

## 🏗️ Stack Technique

| Composant | Technologie |
|-----------|-------------|
| Backend | **Spring Boot 4.1.0** |
| Base de données | **PostgreSQL** (via Spring Data JPA) |
| ORM | **Hibernate / JPA** (Jakarta Persistence) |
| Simplification code | **Lombok** |
| Authentification | **Spring Security + JWT** *(à implémenter)* |
| Paiements | **Mobile Money** via CinetPay ou Flutterwave *(à implémenter)* |
| SMS | **Africa's Talking** *(à implémenter)* |
| Rappels automatiques | **Spring Scheduler** *(à implémenter)* |
| Validation | **spring-boot-starter-validation** (Bean Validation) |
| Java | **Java 17** |

---

## 📁 Structure du Projet

```
src/main/java/com/example/demo/
├── DemoApplication.java          → Point d'entrée Spring Boot
│
├── Entity/                       → ✅ FAIT — 12 entités JPA
│   ├── Organisation.java
│   ├── Utilisateur.java
│   ├── MembreOrganisation.java
│   ├── Abonnement.java
│   ├── Tontine.java
│   ├── MembreTontine.java
│   ├── Tour.java
│   ├── Cotisation.java
│   ├── Transaction.java
│   ├── Notification.java
│   ├── Credit.java
│   └── Penalite.java
│
├── Validation/                   → ✅ FAIT — Annotations custom Bean Validation
│   ├── ValidEmail.java + EmailValidator.java
│   ├── ValidPhone.java + PhoneValidator.java
│   └── ValidStatut.java + StatutValidator.java
│
├── Permission/                   → ✅ FAIT — Système de permissions
│   ├── Permission.java           → Entité JPA
│   ├── PermissionType.java       → Enum des permissions
│   └── RequiresPermission.java   → Annotation @RequiresPermission
│
├── Repository/                   → ✅ FAIT — 12 interfaces JpaRepository
│   ├── OrganisationRepository.java
│   ├── UtilisateurRepository.java
│   ├── MembreOrganisationRepository.java
│   ├── AbonnementRepository.java
│   ├── TontineRepository.java
│   ├── MembreTontineRepository.java
│   ├── TourRepository.java
│   ├── CotisationRepository.java
│   ├── TransactionRepository.java
│   ├── NotificationRepository.java
│   ├── CreditRepository.java
│   └── PenaliteRepository.java
├── Dto/                          → ✅ FAIT — DTOs Request & Response
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── InscriptionRequest.java
│   │   ├── OrganisationRequest.java
│   │   ├── TontineRequest.java
│   │   ├── PaiementMobileMoneyRequest.java
│   │   └── CreditRequest.java
│   └── response/
│       ├── LoginResponse.java
│       ├── UtilisateurResponse.java
│       ├── OrganisationResponse.java
│       ├── TontineResponse.java
│       ├── TourResponse.java
│       ├── CotisationResponse.java
│       ├── TransactionResponse.java
│       ├── CreditResponse.java
│       └── PenaliteResponse.java
├── Service/                      → ✅ FAIT — 5 services (interface + impl)
│   ├── UtilisateurService.java       → Inscription, connexion JWT
│   ├── OrganisationService.java      → CRUD + gestion membres
│   ├── TontineService.java           → Tours automatiques, cotisations
│   ├── PaiementService.java          → Mobile Money (webhook, callback)
│   ├── NotificationService.java      → SMS Africa's Talking
│   ├── CreditService.java            → Micro-crédit + intérêts composés
│   ├── PenaliteService.java          → Amendes retard automatiques
│   └── impl/
│       ├── UtilisateurServiceImpl.java
│       ├── OrganisationServiceImpl.java
│       ├── TontineServiceImpl.java
│       ├── PaiementServiceImpl.java
│       ├── NotificationServiceImpl.java
│       ├── CreditServiceImpl.java
│       └── PenaliteServiceImpl.java
├── controller/                   → ✅ FAIT — 5 controllers REST
│   ├── AuthController.java           → POST /api/auth/inscription, /api/auth/connexion
│   ├── OrganisationController.java   → CRUD + membres (/api/organisations)
│   ├── TontineController.java        → Tontines + tours (/api/tontines)
│   ├── PaiementController.java       → Mobile Money + webhook (/api/paiements)
│   ├── CreditController.java         → Micro-crédits (/api/credits)
│   ├── PenaliteController.java       → Pénalités (/api/penalites)
│   └── GlobalExceptionHandler.java   → Gestion globale des exceptions REST
│
├── Security/                     → ✅ FAIT — Sécurité Spring Security + JWT
│   ├── JwtService.java               → Génération/validation JWT
│   ├── JwtAuthenticationFilter.java  → Filtre d'interception JWT
│   ├── CustomUserDetailsService.java → Chargement utilisateur par téléphone
│   └── SecurityConfig.java           → Configuration HttpSecurity et BCrypt
│
├── Scheduler/                    → ✅ FAIT — Tâches planifiées (cron)
│   └── TontineScheduler.java        → Rappels SMS et génération de pénalités
│
└── Exercice/                     → (dossier existant, non utilisé)
```

---

## 🗃️ Modèle de Données — 12 Entités

### Règles générales
- Toutes les **clés primaires** sont en `UUID` (sauf `Permission` qui utilise `Long`)
- Toutes les **relations** utilisent `@ManyToOne(fetch = FetchType.LAZY)` avec `@JoinColumn`
- Les **montants** sont en `BigDecimal` (precision=15, scale=2)
- Les **dates simples** (sans heure) sont en `LocalDate`
- Les **timestamps** (avec heure) sont en `LocalDateTime`
- Toutes les entités utilisent les annotations **Lombok** : `@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString`

---

### 1. `Organisation`
Structure racine du système. Un groupe/association qui utilise la plateforme.

| Champ | Type Java | Colonne BDD | Contraintes |
|-------|-----------|-------------|-------------|
| idOrganisation | UUID | id_organisation | PK, UUID auto |
| nom | String | nom | NOT NULL |
| type | String | type | NOT NULL (ex: ASSOCIATION, COOPERATIVE) |
| ville | String | ville | NOT NULL |
| statut | String | statut | NOT NULL (ACTIF / INACTIF / SUSPENDU) |

---

### 2. `Utilisateur`
Un utilisateur de l'application. S'authentifie par numéro de téléphone.

| Champ | Type Java | Colonne BDD | Contraintes |
|-------|-----------|-------------|-------------|
| idUtilisateur | UUID | id_utilisateur | PK, UUID auto |
| telephone | String | telephone | NOT NULL, UNIQUE |
| motDePasseHash | String | mot_de_passe_hash | NOT NULL, exclu du toString |
| nom | String | nom | NOT NULL |

---

### 3. `MembreOrganisation`
Table de liaison : un utilisateur appartient à une organisation avec un rôle.

| Champ | Type Java | Colonne BDD | Contraintes |
|-------|-----------|-------------|-------------|
| idMembreOrganisation | UUID | id_membre_organisation | PK |
| organisation | Organisation | organisation_id | FK NOT NULL |
| utilisateur | Utilisateur | user_id | FK NOT NULL |
| role | String | role | NOT NULL (ex: ADMIN, TRESORIER, MEMBRE) |
| dateAjout | LocalDate | date_ajout | NOT NULL |

---

### 4. `Abonnement`
Plan souscrit par une organisation (Gratuit / Premium / etc.).

| Champ | Type Java | Colonne BDD | Contraintes |
|-------|-----------|-------------|-------------|
| idAbonnement | UUID | id_abonnement | PK |
| organisation | Organisation | organisation_id | FK NOT NULL |
| plan | String | plan | NOT NULL (GRATUIT / STANDARD / PREMIUM) |
| dateDebut | LocalDate | date_debut | NOT NULL |
| dateFin | LocalDate | date_fin | Nullable |
| statut | String | statut | NOT NULL (ACTIF / EXPIRE / SUSPENDU / ANNULE) |

---

### 5. `Tontine`
Un cycle d'épargne collective géré par un agent au sein d'une organisation.

| Champ | Type Java | Colonne BDD | Contraintes |
|-------|-----------|-------------|-------------|
| idTontine | UUID | id_tontine | PK |
| organisation | Organisation | organisation_id | FK NOT NULL |
| agentGestionnaire | Utilisateur | agent_gestionnaire_id | FK NOT NULL |
| nom | String | nom | NOT NULL |
| montantCotisation | BigDecimal | montant_cotisation | NOT NULL |
| devise | String | devise | NOT NULL (CDF / USD) |
| statut | String | statut | NOT NULL (EN_COURS / TERMINEE / SUSPENDUE / ANNULEE) |

---

### 6. `MembreTontine`
Inscription d'un utilisateur à une tontine avec son ordre de passage.

| Champ | Type Java | Colonne BDD | Contraintes |
|-------|-----------|-------------|-------------|
| idMembreTontine | UUID | id_membre_tontine | PK |
| tontine | Tontine | tontine_id | FK NOT NULL |
| utilisateur | Utilisateur | user_id | FK NOT NULL |
| ordrePassage | Integer | ordre_passage | NOT NULL (1 = premier bénéficiaire) |
| statut | String | statut | NOT NULL (ACTIF / SUSPENDU / EXCLU / SORTI) |

---

### 7. `Tour`
Un cycle de collecte dans une tontine. Désigne un bénéficiaire par tour.

| Champ | Type Java | Colonne BDD | Contraintes |
|-------|-----------|-------------|-------------|
| idTour | UUID | id_tour | PK |
| tontine | Tontine | tontine_id | FK NOT NULL |
| beneficiaire | Utilisateur | beneficiaire_id | FK NOT NULL |
| numeroTour | Integer | numero_tour | NOT NULL |
| datePrevue | LocalDate | date_prevue | NOT NULL |
| statut | String | statut | NOT NULL (PLANIFIE / EN_COURS / TERMINE / REPORTE / ANNULE) |

---

### 8. `Cotisation`
Le paiement individuel d'un membre pour un tour donné.

| Champ | Type Java | Colonne BDD | Contraintes |
|-------|-----------|-------------|-------------|
| idCotisation | UUID | id_cotisation | PK |
| tour | Tour | tour_id | FK NOT NULL |
| membreTontine | MembreTontine | membre_tontine_id | FK NOT NULL |
| confirmePar | Utilisateur | confirme_par_id | FK Nullable |
| montantAttendu | BigDecimal | montant_attendu | NOT NULL |
| montantPaye | BigDecimal | montant_paye | Nullable |
| dateEcheance | LocalDate | date_echeance | NOT NULL |
| statut | String | statut | NOT NULL (EN_ATTENTE / PAYE / PARTIEL / RETARD) |

---

### 9. `Transaction`
Trace chaque tentative de paiement Mobile Money.

| Champ | Type Java | Colonne BDD | Contraintes |
|-------|-----------|-------------|-------------|
| idTransaction | UUID | id_transaction | PK |
| cotisation | Cotisation | cotisation_id | FK NOT NULL |
| payeur | Utilisateur | payeur_id | FK NOT NULL |
| operateur | String | operateur | NOT NULL (AIRTEL_MONEY / ORANGE_MONEY / MPESA_VODACOM) |
| montant | BigDecimal | montant | NOT NULL |
| devise | String | devise | NOT NULL |
| referenceOperateur | String | reference_operateur | UNIQUE (ref Mobile Money) |
| numeroTelephonePayeur | String | numero_telephone_payeur | NOT NULL |
| statut | String | statut | NOT NULL (INITIEE / EN_ATTENTE / REUSSIE / ECHOUEE / ANNULEE / REMBOURSEE) |
| messageOperateur | String | message_operateur | Nullable |
| dateInitiation | LocalDateTime | date_initiation | Auto @PrePersist |
| dateConfirmation | LocalDateTime | date_confirmation | Nullable |

---

### 10. `Notification`
Historique des SMS et notifications envoyés aux membres.

| Champ | Type Java | Colonne BDD | Contraintes |
|-------|-----------|-------------|-------------|
| idNotification | UUID | id_notification | PK |
| destinataire | Utilisateur | destinataire_id | FK NOT NULL |
| tontine | Tontine | tontine_id | FK Nullable |
| type | String | type | NOT NULL (RAPPEL_COTISATION / CONFIRMATION_PAIEMENT / ANNONCE_BENEFICIAIRE / ALERTE_RETARD / BIENVENUE / GENERAL) |
| canal | String | canal | NOT NULL (SMS / PUSH / EMAIL / IN_APP) |
| message | String | message | NOT NULL (TEXT) |
| statut | String | statut | NOT NULL (EN_ATTENTE / ENVOYE / ECHEC / LU) |
| referencePrestataire | String | reference_prestataire | Nullable (Africa's Talking ID) |
| dateCreation | LocalDateTime | date_creation | Auto @PrePersist |
| dateEnvoi | LocalDateTime | date_envoi | Nullable |

---

### 11. `Credit`
Micro-crédit rotatif avec intérêts pour les groupes avancés.

| Champ | Type Java | Colonne BDD | Contraintes |
|-------|-----------|-------------|-------------|
| idCredit | UUID | id_credit | PK |
| tontine | Tontine | tontine_id | FK NOT NULL |
| emprunteur | Utilisateur | emprunteur_id | FK NOT NULL |
| approvePar | Utilisateur | approuve_par_id | FK NOT NULL |
| montantPrincipal | BigDecimal | montant_principal | NOT NULL |
| tauxInteretMensuel | BigDecimal | taux_interet_mensuel | NOT NULL (ex: 5.00 = 5%/mois) |
| dureeMois | Integer | duree_mois | NOT NULL |
| montantTotalDu | BigDecimal | montant_total_du | NOT NULL (calculé) |
| montantRembourse | BigDecimal | montant_rembourse | Default 0 |
| devise | String | devise | NOT NULL |
| dateOctroi | LocalDate | date_octroi | NOT NULL |
| dateEcheanceFinale | LocalDate | date_echeance_finale | NOT NULL |
| statut | String | statut | NOT NULL (EN_ATTENTE / ACTIF / REMBOURSE / EN_RETARD / DEFAUT) |
| createdAt | LocalDateTime | created_at | Auto @PrePersist |

---

### 12. `Penalite`
Amende automatique générée pour les membres en retard de cotisation.

| Champ | Type Java | Colonne BDD | Contraintes |
|-------|-----------|-------------|-------------|
| idPenalite | UUID | id_penalite | PK |
| cotisation | Cotisation | cotisation_id | FK NOT NULL |
| membre | Utilisateur | membre_id | FK NOT NULL |
| motif | String | motif | NOT NULL (RETARD_COTISATION / ABSENCE_REUNION / NON_PAIEMENT_CREDIT / AUTRE) |
| joursRetard | Integer | jours_retard | NOT NULL |
| montant | BigDecimal | montant | NOT NULL |
| devise | String | devise | NOT NULL |
| statut | String | statut | NOT NULL (EN_ATTENTE / PAYEE / ANNULEE / DISPENSEE) |
| dateEcheance | LocalDate | date_echeance | Nullable |
| dateGeneration | LocalDateTime | date_generation | Auto @PrePersist |
| datePaiement | LocalDateTime | date_paiement | Nullable |

---

## 🔗 Diagramme des Relations

```
Organisation ──────────────────────────────────────────────────┐
    │                                                           │
    ├──< MembreOrganisation >── Utilisateur                    │
    ├──< Abonnement                                             │
    └──< Tontine ────────────────── Utilisateur (agent)        │
              │                                                 │
              ├──< MembreTontine >── Utilisateur               │
              │         │                                       │
              │         └──< Cotisation ──< Transaction        │
              │                   │                            │
              │                   └──< Penalite               │
              │                                                 │
              └──< Tour ──────────── Utilisateur (bénéf.)      │
                    │                                           │
                    └── (lié aux Cotisations)                  │
                                                               │
Credit ──── Tontine + Utilisateur (emprunteur + approbateur)  │
Notification ── Utilisateur + Tontine (optionnel)             │
```

---

## ✅ État d'Avancement

| Couche | Statut | Détail |
|--------|--------|--------|
| **Entity** | ✅ Terminé | 12 entités JPA avec commentaires Javadoc complets |
| **Validation** | ✅ Terminé | @ValidEmail, @ValidPhone, @ValidStatut |
| **Permission** | ✅ Terminé | Enum PermissionType, entité Permission, annotation @RequiresPermission |
| **Repository** | ✅ Terminé | 12 interfaces JpaRepository avec requêtes métier |
| **DTO** | ✅ Terminé | DTOs Request/Response avec Bean Validation |
| **Service** | ✅ Terminé | 5 interfaces + 5 implémentations avec logique métier complète |
| **Controller** | ✅ Terminé | 5 controllers REST avec tous les endpoints |
| **Security** | ✅ Terminé | Spring Security + JWT (authentification par téléphone, session stateless) |
| **Mobile Money** | ⏳ À faire | Intégration CinetPay ou Flutterwave |
| **SMS** | ⏳ À faire | Intégration Africa's Talking |
| **Scheduler** | ✅ Terminé | TontineScheduler pour les rappels SMS à J-3/J-1 et génération automatique des pénalités |

---

## 📌 Conventions de Code

- **Package** : `com.example.demo` (à renommer en `com.likelamba` à terme)
- **Nommage entités** : PascalCase (`MembreTontine`)
- **Nommage colonnes BDD** : snake_case (`membre_tontine_id`)
- **Nommage champs Java** : camelCase (`membreTontine`)
- **Toutes les FKs** : chargement `LAZY` pour éviter les N+1
- **Statuts** : toujours en `String` (pour flexibilité), valeurs en MAJUSCULES
- **Sécurité** : mot de passe toujours hashé (BCrypt), jamais en clair ni dans les logs

---

## 🚀 Prochaine priorité recommandée

1. **Repositories** → Interfaces JPA pour accès aux données
2. **DTOs** → Objets de transfert Request/Response
3. **Services** → Logique métier (TontineService, CotisationService, etc.)
4. **Spring Security + JWT** → Auth par numéro de téléphone
5. **Intégration Mobile Money** → CinetPay/Flutterwave
6. **SMS** → Africa's Talking
7. **Scheduler** → Rappels automatiques + génération pénalités

---

## ⚠️ RÈGLE IMPORTANTE — Mise à jour obligatoire

> **Tout développeur ou IA travaillant sur ce projet DOIT mettre à jour ce fichier après chaque action significative.**
>
> Ce document est la **source de vérité** du projet. Il doit toujours refléter l'état réel du code.
>
> **Que faut-il mettre à jour ?**
> - Le tableau "État d'Avancement" (changer ⏳ en ✅ quand c'est terminé)
> - La structure du projet (ajouter les nouveaux fichiers/dossiers créés)
> - Les tableaux de champs si une entité est modifiée
> - La section "Prochaine priorité" après chaque étape terminée
> - L'historique des modifications ci-dessous

---

## 📝 Historique des Modifications

| Date | Action | Détail |
|------|--------|--------|
| 2026-07-08 | ✅ Création entité `Organisation` | Champs : idOrganisation, nom, type, ville, statut |
| 2026-07-08 | ✅ Création entité `Utilisateur` | Champs : idUtilisateur, telephone, motDePasseHash, nom |
| 2026-07-08 | ✅ Création entité `MembreOrganisation` | FK → Organisation, Utilisateur |
| 2026-07-08 | ✅ Création entité `Abonnement` | FK → Organisation |
| 2026-07-08 | ✅ Création entité `Tontine` | FK → Organisation, Utilisateur (agent) |
| 2026-07-08 | ✅ Création entité `MembreTontine` | FK → Tontine, Utilisateur |
| 2026-07-08 | ✅ Création entité `Tour` | FK → Tontine, Utilisateur (bénéficiaire) |
| 2026-07-08 | ✅ Création entité `Cotisation` | FK → Tour, MembreTontine, Utilisateur |
| 2026-07-08 | ✅ Création entité `Transaction` | Trace les paiements Mobile Money |
| 2026-07-08 | ✅ Création entité `Notification` | SMS/Push via Africa's Talking |
| 2026-07-08 | ✅ Création entité `Credit` | Micro-crédit rotatif avec intérêts |
| 2026-07-08 | ✅ Création entité `Penalite` | Amendes retard, générées par Scheduler |
| 2026-07-08 | ✅ Package `Validation` | @ValidEmail, @ValidPhone, @ValidStatut + validators |
| 2026-07-08 | ✅ Package `Permission` | Enum PermissionType, entité Permission, @RequiresPermission |
| 2026-07-08 | ✅ Commentaires Javadoc | Ajout commentaires détaillés sur tous les champs de toutes les entités |
| 2026-07-08 | ✅ Création PROJECT.md | Ce document de documentation globale du projet |
| 2026-07-08 | ✅ Création des Repositories | 12 interfaces JpaRepository avec requêtes métier (filtres statut, dates, agrégats SQL) |
| 2026-07-08 | ✅ Création des DTOs | 6 Request DTOs (avec Bean Validation) + 9 Response DTOs (avec @Builder) |
| 2026-07-08 | ✅ Création des Services | 7 interfaces + 7 implémentations : Utilisateur, Organisation, Tontine, Paiement, Notification, Crédit, Pénalité |
| 2026-07-08 | ✅ Création des Controllers | 5 controllers REST : Auth, Organisation, Tontine, Paiement, Crédit, Pénalité |
| 2026-07-08 | ✅ Configuration Security JWT | JwtService, CustomUserDetailsService, JwtAuthenticationFilter, SecurityConfig avec chiffrement BCrypt |
| 2026-07-08 | ✅ Planificateur automatique | TontineScheduler (@Scheduled) pour retards, amendes et relances SMS |
| 2026-07-08 | ✅ Gestionnaire d'erreurs | GlobalExceptionHandler pour standardiser les réponses d'erreurs JSON |
| 2026-07-08 | ✅ Guide Utilisateur | Création du guide GUIDE_UTILISATEUR.md expliquant l'usage du système aux utilisateurs finaux |
| 2026-07-08 | ✅ Guide Superadmin | Création du guide GUIDE_SUPERADMIN.md décrivant les tâches de supervision du système |
