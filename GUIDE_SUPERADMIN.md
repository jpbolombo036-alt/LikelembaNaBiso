# 👑 Guide d'Administration (Superadmin) — Likelamba

> **Ce guide est réservé aux administrateurs de la plateforme Likelamba.**
> Il explique comment superviser le système, gérer les organisations, suivre les transactions Mobile Money, administrer les abonnements et contrôler la sécurité globale de l'application.

---

## 🧭 Sommaire
1. [🔒 1. Contrôle d'accès & Rôles Admin](#-1-contrôle-daccès--rôles-admin)
2. [🏢 2. Supervision des Organisations & Abonnements](#-2-supervision-des-organisations--abonnements)
3. [💸 3. Suivi & Réconciliation des Transactions Mobile Money](#-3-suivi--réconciliation-des-transactions-mobile-money)
4. [📈 4. Audit & Historique des Notifications SMS](#-4-audit--historique-des-notifications-sms)
5. [⚙️ 5. Gestion des Crédits et des Pénalités Globales](#-5-gestion-des-crédits-et-des-pénalités-globales)

---

## 🔒 1. Contrôle d'accès & Rôles Admin

Le Superadmin utilise l'annotation de sécurité `@RequiresPermission` sur les endpoints sensibles pour restreindre leur exécution.

### Permissions de haut niveau (`PermissionType`) :
* **`ACCEDER_ADMINISTRATION`** : Accès global au tableau de bord superadmin.
* **`GERER_ROLES`** : Permet d'élever un utilisateur au rang de gestionnaire ou d'administrateur.
* **`GERER_PERMISSIONS`** : Permet de modifier les permissions affectées aux rôles.

---

## 🏢 2. Supervision des Organisations & Abonnements

Le Superadmin gère le modèle économique de Likelamba en contrôlant les abonnements des groupes.

### 1. Validation et blocage des organisations
* Le Superadmin peut passer le statut d'une organisation à `SUSPENDU` ou `INACTIF` via `PATCH /api/organisations/{id}/statut` en cas de fraude ou de non-paiement de l'abonnement.
* Une organisation suspendue ne peut plus lancer de nouveaux tours de tontine.

### 2. Gestion des Abonnements (`Abonnement`)
* Suivi des formules : `GRATUIT` (avec limites sur le nombre de membres ou de tontines) ou `PREMIUM`/`STANDARD`.
* Le planificateur automatique (`TontineScheduler`) surveille les dates d'expiration des abonnements et alerte le Superadmin si un groupe doit être rétrogradé en formule gratuite.

---

## 💸 3. Suivi & Réconciliation des Transactions Mobile Money

Le module de transaction de Likelamba permet au Superadmin d'avoir un contrôle financier total et d'éviter les réclamations.

### 1. Dashboard des transactions
* **`GET /api/paiements/{transactionId}`** : Permet de vérifier le statut exact d'un paiement auprès de CinetPay ou Flutterwave si un membre signale que son argent a été débité mais que sa cotisation n'est pas validée.

### 2. États d'une transaction :
* `INITIEE` : Le client a cliqué sur payer.
* `EN_ATTENTE` : Le push OTP a été envoyé sur le téléphone du client en RDC.
* `REUSSIE` : Le compte Mobile Money a été débité et la cotisation associée est automatiquement marquée `PAYE`.
* `ECHOUEE` : Solde insuffisant, code PIN erroné ou timeout réseau.

---

## 📈 4. Audit & Historique des Notifications SMS

Les SMS coûtent de l'argent et sont critiques pour le bon fonctionnement des tontines. Le Superadmin dispose d'un journal d'audit :

* **Table `Notification`** : Enregistre le destinataire, le message envoyé, le canal (SMS), le statut de délivrabilité (`ENVOYE`, `ECHEC`, `LU`) et la référence du prestataire (Africa's Talking ID).
* Le Superadmin peut surveiller la file d'attente des SMS échoués pour relancer les envois en cas de panne de l'opérateur local.

---

## ⚙️ 5. Gestion des Crédits et des Pénalités Globales

Pour assurer la stabilité et l'éthique de la plateforme :

### 1. Arbitrage des litiges sur pénalités
* Si un membre conteste une pénalité automatique générée par le système, le Superadmin (ou le gestionnaire délégué) peut utiliser l'endpoint :
  `PATCH /api/penalites/{id}/dispenser`
  Cela annule la pénalité avec le statut `DISPENSEE`, libérant le membre pour le tour de tontine suivant.

### 2. Surveillance des encours de crédit
* **`CreditRepository.sumMontantRestantDuByTontine`** : Permet au Superadmin de connaître la somme totale des crédits actifs non remboursés sur la plateforme afin de maîtriser le risque de liquidité des groupes.
