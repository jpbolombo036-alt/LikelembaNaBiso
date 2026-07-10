# Plan : Inbox notifications agent + clarification risques cash

## Contexte (issu des questions ouvertes)

Deux risques soulevés après l'implémentation du dashboard cash agent :

1. **Double paiement** : bloquer une déclaration cash si le membre a déjà une transaction
   MOBILE_MONEY `REUSSIE` sur la même cotisation.
2. **Perte d'info en cas d'absence** : historique des notifications agent.

### État réel du code (vérifié)
- **Risque 1 — DÉJÀ RÉSOLU.** `PaiementServiceImpl.declarerPaiementCash` et `initierPaiement`
  utilisent `transactionRepository.existsByCotisationAndStatut(cotisation, "REUSSIE")`
  (`TransactionRepository.existsByCotisationAndStatut`). Ce garde-fou est **agnostique du mode** :
  dès qu'une transaction (cash OU mobile money) est `REUSSIE` sur la cotisation, la déclaration est
  refusée. Pas de modification requise.
- **Risque 2 — PARTIELLEMENT RÉSOLU.** `Notification` est **persistée** à chaque envoi
  (`NotificationRepository.save`, table `notification`). Le dashboard cash calcule
  `declarationsEnAttente` depuis `Transaction` (indépendant des SMS), donc absence ≠ perte d'info
  actionnable. **Trous** :
  - Aucun endpoint n'expose l'historique persisté (pas de `NotificationController`).
  - `statut = "LU"` existe dans le modèle mais n'est **jamais** positionné.
  - Le badge « non lues » du dashboard global compte `EN_ATTENTE` (`DashboardServiceImpl` ligne ~60),
    or toutes les notifications sont aussitôt marquées `ENVOYE` → badge toujours à 0 (bug latent).

Décision utilisateur : **ajouter un endpoint inbox agent** (voir tâches ci-dessous).

## Décisions
- Garde-fou double-paiement cash/MM : conservé tel quel (mode-agnostique). Pas de changement.
- Cycle de statut notification : `EN_ATTENTE` → `ENVOYE` (non lu) → `LU` (lu). `ECHEC` conservé.
- « Non lu » = `statut != "LU"`.
- L'inbox est propre à l'agent connecté (JWT) ; il ne peut lire/marquer que ses propres notifications.

## Tâches d'implémentation (endpoint inbox)

1. **Repository** — `NotificationRepository.java`
   - `List<Notification> findByDestinataireAndStatutNotOrderByDateCreationDesc(Utilisateur d, String statut)`
     (lister tout sauf `LU`).
   - Surcharge avec filtre type :
     `findByDestinataireAndTypeAndStatutNotOrderByDateCreationDesc(Utilisateur, String type, String statut)`.
   - `long countByDestinataireAndStatutNot(Utilisateur d, String statut)` (badge non lues).

2. **DTO** — créer `Dto/response/NotificationResponse.java`
   - champs : `idNotification`, `type`, `canal`, `message`, `statut`, `tontineNom`,
     `dateCreation`, `dateEnvoi`. Mapper depuis `Notification`.

3. **Service** — `NotificationService` + `NotificationServiceImpl.java`
   - `List<Notification> listerPourAgent(Utilisateur agent, String typeFiltre)` :
     renvoie les notifications non lues (`statut != "LU"`), triées desc ; `typeFiltre` optionnel
     (ex : `DECLARATION_CASH`, `CONFIRMATION_CASH`, `REJET_CASH`).
   - `void marquerCommeLue(UUID idNotification, UUID agentId)` :
     charge la notification, vérifie `destinataire.id == agentId` (sinon `IllegalAccessError`),
     met `statut = "LU"`.

4. **Controller** — créer `controller/NotificationController.java`
   - `GET /api/notifications/agent` (JWT requis) → `List<NotificationResponse>` ;
     param optionnel `?type=` pour filtrer. Id agent extrait via `jwtService.extraireIdUtilisateur`.
   - `POST /api/notifications/{id}/lu` (JWT requis) → `204 No Content`, marque comme lue.
   - Sécurité : hérite de `anyRequest().authenticated()` (voir `SecurityConfig`).

5. **Correction badge global** — `DashboardServiceImpl.java` (ligne ~60)
   - Remplacer `notificationRepository.countByDestinataireAndStatut(utilisateur, "EN_ATTENTE")`
     par `notificationRepository.countByDestinataireAndStatutNot(utilisateur, "LU")`.
   - Améliore le badge pour tous les rôles, pas seulement l'agent.

6. **(Optionnel / hardening)** — empêcher le « double en attente » : une cotisation ne devrait pas
   avoir simultanément une MM `INITIEE/EN_ATTENTE` ET une cash `INITIEE`. À considérer seulement si
   le métier le demande ; hors périmètre MVP sauf demande explicite.

## Risques / points d'attention
- Pas de migration SQL : `statut` (String) et `LU` existent déjà ; seul le comportement change.
- L'inbox ne doit pas devenir une source de vérité pour les déclarations cash (le dashboard cash
  reste la source via `Transaction`). L'inbox est purement informatif/historique.
- Vérifié : les notifications cash (`DECLARATION_CASH`, `CONFIRMATION_CASH`, `REJET_CASH`) sont déjà
  persistées par `NotificationServiceImpl` → immédiatement visibles dans l'inbox.

## Validation
- `.\mvnw.cmd -q -DskipTests compile` → OK (la BDD Postgres n'est pas requise pour la compilation ;
  le test `DemoApplicationTests.contextLoads` échoue hors-ligne faute de BDD, ce n'est pas un défaut code).
- Manuel (avec BDD) :
  - Un membre déclare cash → agent reçoit `DECLARATION_CASH` (SMS + ligne `notification`).
  - `GET /api/notifications/agent` liste la notification (`statut=ENVOYE`).
  - `POST /api/notifications/{id}/lu` → `statut=LU` ; le badge « non lues » décroît.
  - `GET /api/notifications/agent?type=DECLARATION_CASH` filtre correctement.
  - Agent A ne peut pas marquer une notification de l'agent B (403/IllegalAccessError).
  - Cas limite : agent sans notification → liste vide, badge 0.
