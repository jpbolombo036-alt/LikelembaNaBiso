# Plan : Dashboard cash pour l'agent gestionnaire

## Decision retenue
Scope dashboard cash agent = **Attente + confirmé**.
L'appelant reçoit :
- déclarations cash en attente sur ses tontines
- stats cash confirmées par période
- alertes liées aux cash

## Constat du code existant
- Cash existe en base (V3), entité `Transaction` avec `mode_paiement` et enum `ModePaiement`.
- PaiementController expose déjà : `declarerPaiementCash`, `confirmerPaiementCash`, `getDeclarationsCashEnAttente(tontineId)`.
- DashboardService/DashboardServiceImpl existent, mais sont globales et mélangent tous les rôles.
- `TransactionRepository` n'a pas de méthode dédiée pour filtrer cash par tontine/agent.

## Ce qui sera ajouté / modifié
### 1. DTOs
- Créer `StatsCashAgent` : compteurs et montants cash pour les tontines de l'agent.
- Créer `CashDashboardResponse` :
  - `utilisateurNom`
  - `organisationNom`
  - `StatsCashAgent statsCash`
  - `List<TransactionResponse> declarationsEnAttente`
  - `List<Alerte> alertesCash`
- Éventuellement réutiliser `Alerte` existant.

### 2. Repository
Étendre `TransactionRepository` avec des requêtes utile cash :
- `List<Transaction> findByModePaiementAndTour_Tontine_AgentGestionnaire(ModePaiement mode, Utilisateur agent);`
- ou variante pour INITIEE + CASH.

### 3. Service
- Ajouter dans `DashboardService` :
  - `CashDashboardResponse obtenirDashboardCashAgent(UUID agentId);`
- Implémentation dans `DashboardServiceImpl` :
  - charger l'utilisateur agent
  - charger ses tontines via `tontineRepository.findByAgentGestionnaire(agent)`
  - requêter les transactions cash sur ces tontines
  - construire `declarationsEnAttente` (statut `INITIEE`)
  - construire `statsCash` (REUSSIE, ECHOUEE)
  - alimenter `alertesCash` (ex : déclarations INITIEE anciennes, montant en attente élevé)

### 4. Controller
- Ajouter dans `DashboardController` :
  - `GET /api/dashboard/cash/agent`
  - Authentification JWT obligatoire
  - Sécurité métier : l'utilisateur courant doit être `agentGestionnaire` d'au moins une tontine

### 5. Sécurité
- `DashboardController` extrait l'id depuis JWT
- Le service lève une erreur si l'utilisateur n'est pas agent gestionnaire

### 6. Migration
- Aucune migration SQL nécessaire : V3 couvre déjà cash.

## Risques / points d'attention
- Volumétrie : filtrage en mémoire sur toutes les transactions risque d'être lourd. Préférer une requête dédiée dans `TransactionRepository`.
- Responsabilité : un utilisateur peut être agent de plusieurs tontines/organisations. Le dashboard cash agrège sur toutes ces tontines.
- Cohérence : dates/fuseaux horaires non gérées aujourd'hui. Pour un MVP, on reste sur `LocalDateTime` serveur.

## Validation
- Compilation OK.
- Test manuel : agent récupère son dashboard cash.
- Cas limites : agent sans tontine, sans cash, cash partiellement confirmé.
