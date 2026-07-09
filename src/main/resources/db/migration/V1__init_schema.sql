-- ============================================================================
-- Migration Flyway : V1__init_schema.sql
-- Projet : Likelamba
-- SGBD    : PostgreSQL (>= 13, gen_random_uuid() native)
-- Description : Création de l'ensemble du schéma relationnel à partir des
--               entités JPA (snake_case, UUID PK, BigDecimal, dates, FK,
--               contraintes UNIQUE et indexes).
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Tables racines (sans dépendance)
-- ----------------------------------------------------------------------------

CREATE TABLE organisation (
    id_organisation UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    nom             VARCHAR(150) NOT NULL,
    type            VARCHAR(50)  NOT NULL,
    ville           VARCHAR(100) NOT NULL,
    statut          VARCHAR(20)  NOT NULL
);

CREATE TABLE utilisateur (
    id_utilisateur    UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    telephone         VARCHAR(20)  NOT NULL,
    mot_de_passe_hash VARCHAR       NOT NULL,
    nom               VARCHAR(100) NOT NULL,
    CONSTRAINT uk_utilisateur_telephone UNIQUE (telephone)
);

CREATE TABLE permissions (
    id_permission BIGSERIAL PRIMARY KEY,
    type          VARCHAR(50)  NOT NULL UNIQUE,
    nom           VARCHAR(100) NOT NULL,
    description   TEXT,
    ressource     VARCHAR(50)  NOT NULL,
    action        VARCHAR(30)  NOT NULL,
    actif         BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- Membres / abonnements (dépendent de organisation et utilisateur)
-- ----------------------------------------------------------------------------

CREATE TABLE membre_organisation (
    id_membre_organisation UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    organisation_id        UUID NOT NULL,
    user_id                UUID NOT NULL,
    role                   VARCHAR(30) NOT NULL,
    date_ajout             DATE        NOT NULL,
    CONSTRAINT fk_mo_organisation FOREIGN KEY (organisation_id)
        REFERENCES organisation (id_organisation) ON DELETE NO ACTION,
    CONSTRAINT fk_mo_utilisateur FOREIGN KEY (user_id)
        REFERENCES utilisateur (id_utilisateur) ON DELETE NO ACTION
);

CREATE TABLE abonnement (
    id_abonnement UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    organisation_id UUID NOT NULL,
    plan          VARCHAR(30) NOT NULL,
    date_debut    DATE        NOT NULL,
    date_fin      DATE,
    statut        VARCHAR(20) NOT NULL,
    CONSTRAINT fk_abonnement_organisation FOREIGN KEY (organisation_id)
        REFERENCES organisation (id_organisation) ON DELETE NO ACTION
);

-- ----------------------------------------------------------------------------
-- Tontines (dépendent de organisation et utilisateur)
-- ----------------------------------------------------------------------------

CREATE TABLE tontine (
    id_tontine          UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    organisation_id     UUID NOT NULL,
    agent_gestionnaire_id UUID NOT NULL,
    nom                 VARCHAR(150) NOT NULL,
    montant_cotisation  NUMERIC(15, 2) NOT NULL,
    devise              VARCHAR(10)  NOT NULL,
    statut              VARCHAR(20)  NOT NULL,
    CONSTRAINT fk_tontine_organisation FOREIGN KEY (organisation_id)
        REFERENCES organisation (id_organisation) ON DELETE NO ACTION,
    CONSTRAINT fk_tontine_agent FOREIGN KEY (agent_gestionnaire_id)
        REFERENCES utilisateur (id_utilisateur) ON DELETE NO ACTION
);

-- ----------------------------------------------------------------------------
-- Membres de tontine (dépendent de tontine et utilisateur)
-- ----------------------------------------------------------------------------

CREATE TABLE membre_tontine (
    id_membre_tontine UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    tontine_id        UUID NOT NULL,
    user_id           UUID NOT NULL,
    ordre_passage     INTEGER NOT NULL,
    statut            VARCHAR(20) NOT NULL,
    CONSTRAINT fk_mt_tontine FOREIGN KEY (tontine_id)
        REFERENCES tontine (id_tontine) ON DELETE NO ACTION,
    CONSTRAINT fk_mt_utilisateur FOREIGN KEY (user_id)
        REFERENCES utilisateur (id_utilisateur) ON DELETE NO ACTION
);

-- ----------------------------------------------------------------------------
-- Tours (dépendent de tontine et utilisateur)
-- ----------------------------------------------------------------------------

CREATE TABLE tour (
    id_tour        UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    tontine_id     UUID NOT NULL,
    beneficiaire_id UUID NOT NULL,
    numero_tour    INTEGER NOT NULL,
    date_prevue    DATE    NOT NULL,
    statut         VARCHAR(20) NOT NULL,
    CONSTRAINT fk_tour_tontine FOREIGN KEY (tontine_id)
        REFERENCES tontine (id_tontine) ON DELETE NO ACTION,
    CONSTRAINT fk_tour_beneficiaire FOREIGN KEY (beneficiaire_id)
        REFERENCES utilisateur (id_utilisateur) ON DELETE NO ACTION
);

-- ----------------------------------------------------------------------------
-- Cotisations (dépendent de tour, membre_tontine et utilisateur)
-- ----------------------------------------------------------------------------

CREATE TABLE cotisation (
    id_cotisation    UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    tour_id          UUID NOT NULL,
    membre_tontine_id UUID NOT NULL,
    confirme_par_id  UUID,
    montant_attendu  NUMERIC(15, 2) NOT NULL,
    montant_paye     NUMERIC(15, 2),
    date_echeance    DATE        NOT NULL,
    statut           VARCHAR(20) NOT NULL,
    CONSTRAINT fk_cotisation_tour FOREIGN KEY (tour_id)
        REFERENCES tour (id_tour) ON DELETE NO ACTION,
    CONSTRAINT fk_cotisation_membre_tontine FOREIGN KEY (membre_tontine_id)
        REFERENCES membre_tontine (id_membre_tontine) ON DELETE NO ACTION,
    CONSTRAINT fk_cotisation_confirme_par FOREIGN KEY (confirme_par_id)
        REFERENCES utilisateur (id_utilisateur) ON DELETE NO ACTION
);

-- ----------------------------------------------------------------------------
-- Transactions (dépendent de cotisation et utilisateur)
-- ----------------------------------------------------------------------------

CREATE TABLE transaction (
    id_transaction         UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    cotisation_id          UUID NOT NULL,
    payeur_id              UUID NOT NULL,
    operateur              VARCHAR(20)  NOT NULL,
    montant                NUMERIC(15, 2) NOT NULL,
    devise                 VARCHAR(10)  NOT NULL,
    reference_operateur    VARCHAR(100) UNIQUE,
    numero_telephone_payeur VARCHAR(20) NOT NULL,
    statut                 VARCHAR(20)  NOT NULL,
    message_operateur      VARCHAR(255),
    date_initiation        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_confirmation      TIMESTAMP,
    CONSTRAINT fk_transaction_cotisation FOREIGN KEY (cotisation_id)
        REFERENCES cotisation (id_cotisation) ON DELETE NO ACTION,
    CONSTRAINT fk_transaction_payeur FOREIGN KEY (payeur_id)
        REFERENCES utilisateur (id_utilisateur) ON DELETE NO ACTION
);

-- ----------------------------------------------------------------------------
-- Notifications (dépendent de utilisateur et tontine)
-- ----------------------------------------------------------------------------

CREATE TABLE notification (
    id_notification        UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    destinataire_id        UUID NOT NULL,
    tontine_id             UUID,
    type                   VARCHAR(30) NOT NULL,
    canal                  VARCHAR(10) NOT NULL,
    message                TEXT        NOT NULL,
    statut                 VARCHAR(15) NOT NULL,
    reference_prestataire  VARCHAR(100),
    date_creation          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_envoi             TIMESTAMP,
    CONSTRAINT fk_notification_destinataire FOREIGN KEY (destinataire_id)
        REFERENCES utilisateur (id_utilisateur) ON DELETE NO ACTION,
    CONSTRAINT fk_notification_tontine FOREIGN KEY (tontine_id)
        REFERENCES tontine (id_tontine) ON DELETE NO ACTION
);

-- ----------------------------------------------------------------------------
-- Crédits (dépendent de tontine et utilisateur)
-- ----------------------------------------------------------------------------

CREATE TABLE credit (
    id_credit              UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    tontine_id             UUID NOT NULL,
    emprunteur_id          UUID NOT NULL,
    approuve_par_id        UUID NOT NULL,
    montant_principal      NUMERIC(15, 2) NOT NULL,
    taux_interet_mensuel   NUMERIC(5, 2)  NOT NULL,
    duree_mois             INTEGER        NOT NULL,
    montant_total_du       NUMERIC(15, 2) NOT NULL,
    montant_rembourse      NUMERIC(15, 2),
    devise                 VARCHAR(10)    NOT NULL,
    date_octroi            DATE           NOT NULL,
    date_echeance_finale   DATE           NOT NULL,
    statut                 VARCHAR(20)    NOT NULL,
    created_at             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_credit_tontine FOREIGN KEY (tontine_id)
        REFERENCES tontine (id_tontine) ON DELETE NO ACTION,
    CONSTRAINT fk_credit_emprunteur FOREIGN KEY (emprunteur_id)
        REFERENCES utilisateur (id_utilisateur) ON DELETE NO ACTION,
    CONSTRAINT fk_credit_approuve_par FOREIGN KEY (approuve_par_id)
        REFERENCES utilisateur (id_utilisateur) ON DELETE NO ACTION
);

-- ----------------------------------------------------------------------------
-- Pénalités (dépendent de cotisation et utilisateur)
-- ----------------------------------------------------------------------------

CREATE TABLE penalite (
    id_penalite     UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    cotisation_id   UUID NOT NULL,
    membre_id       UUID NOT NULL,
    motif           VARCHAR(30) NOT NULL,
    jours_retard    INTEGER      NOT NULL,
    montant         NUMERIC(15, 2) NOT NULL,
    devise          VARCHAR(10)    NOT NULL,
    statut          VARCHAR(15)    NOT NULL,
    date_echeance   DATE,
    date_generation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_paiement   TIMESTAMP,
    CONSTRAINT fk_penalite_cotisation FOREIGN KEY (cotisation_id)
        REFERENCES cotisation (id_cotisation) ON DELETE NO ACTION,
    CONSTRAINT fk_penalite_membre FOREIGN KEY (membre_id)
        REFERENCES utilisateur (id_utilisateur) ON DELETE NO ACTION
);

-- ----------------------------------------------------------------------------
-- Refresh tokens (données volatiles : CASCADE avec l'utilisateur)
-- ----------------------------------------------------------------------------

CREATE TABLE refresh_token (
    id_refresh_token UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    user_id          UUID NOT NULL,
    token            VARCHAR(512) NOT NULL,
    date_expiration  TIMESTAMP   NOT NULL,
    revoked          BOOLEAN     NOT NULL,
    created_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token_utilisateur FOREIGN KEY (user_id)
        REFERENCES utilisateur (id_utilisateur) ON DELETE CASCADE,
    CONSTRAINT uk_refresh_token_token UNIQUE (token)
);

-- ----------------------------------------------------------------------------
-- Indexes sur les clés étrangères (perf de jointure et de recherche)
-- ----------------------------------------------------------------------------

CREATE INDEX idx_mo_organisation ON membre_organisation (organisation_id);
CREATE INDEX idx_mo_user         ON membre_organisation (user_id);

CREATE INDEX idx_abonnement_organisation ON abonnement (organisation_id);

CREATE INDEX idx_tontine_organisation ON tontine (organisation_id);
CREATE INDEX idx_tontine_agent        ON tontine (agent_gestionnaire_id);

CREATE INDEX idx_mt_tontine ON membre_tontine (tontine_id);
CREATE INDEX idx_mt_user    ON membre_tontine (user_id);

CREATE INDEX idx_tour_tontine      ON tour (tontine_id);
CREATE INDEX idx_tour_beneficiaire ON tour (beneficiaire_id);

CREATE INDEX idx_cotisation_tour          ON cotisation (tour_id);
CREATE INDEX idx_cotisation_membre_tontine ON cotisation (membre_tontine_id);
CREATE INDEX idx_cotisation_confirme_par   ON cotisation (confirme_par_id);

CREATE INDEX idx_transaction_cotisation ON transaction (cotisation_id);
CREATE INDEX idx_transaction_payeur     ON transaction (payeur_id);

CREATE INDEX idx_notification_destinataire ON notification (destinataire_id);
CREATE INDEX idx_notification_tontine      ON notification (tontine_id);

CREATE INDEX idx_credit_tontine     ON credit (tontine_id);
CREATE INDEX idx_credit_emprunteur  ON credit (emprunteur_id);
CREATE INDEX idx_credit_approuve_par ON credit (approuve_par_id);

CREATE INDEX idx_penalite_cotisation ON penalite (cotisation_id);
CREATE INDEX idx_penalite_membre     ON penalite (membre_id);

CREATE INDEX idx_refresh_token_user ON refresh_token (user_id);
