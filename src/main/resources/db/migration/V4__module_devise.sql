-- ============================================================================
-- Migration Flyway : V4__module_devise.sql
-- Projet : Likelamba
-- Description : Module Devise + Taux de change
--   - Création de la table de référence devise (code ISO = PK)
--   - Création de la table taux_change (historique des taux par paire)
--   - Alimentation de devise à partir des codes déjà présents dans les données
--   - Contraintes de clé étrangère devise(code) sur tontine / transaction /
--     credit / penalite
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. Table de référence des devises
-- ----------------------------------------------------------------------------

CREATE TABLE devise (
    code     VARCHAR(10)  PRIMARY KEY,
    nom      VARCHAR(100) NOT NULL,
    symbole  VARCHAR(10),
    actif    BOOLEAN      NOT NULL DEFAULT TRUE
);

-- ----------------------------------------------------------------------------
-- 2. Table des taux de change (un taux courant = date_fin IS NULL)
-- ----------------------------------------------------------------------------

CREATE TABLE taux_change (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
    devise_source   VARCHAR(10) NOT NULL,
    devise_cible    VARCHAR(10) NOT NULL,
    taux            NUMERIC(19, 6) NOT NULL,
    date_debut      TIMESTAMP   NOT NULL,
    date_fin        TIMESTAMP,
    source          VARCHAR(10) NOT NULL,
    cree_par        UUID,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_taux_devise_source FOREIGN KEY (devise_source)
        REFERENCES devise (code) ON DELETE NO ACTION,
    CONSTRAINT fk_taux_devise_cible FOREIGN KEY (devise_cible)
        REFERENCES devise (code) ON DELETE NO ACTION
);

-- ----------------------------------------------------------------------------
-- 3. Peuplement de devise à partir des codes déjà présents dans les données
--    (garantit qu'aucune ligne existante ne viole la future FK)
-- ----------------------------------------------------------------------------

INSERT INTO devise (code, nom, symbole, actif)
SELECT DISTINCT devise, devise, devise, TRUE FROM tontine
UNION
SELECT DISTINCT devise, devise, devise, TRUE FROM transaction
UNION
SELECT DISTINCT devise, devise, devise, TRUE FROM credit
UNION
SELECT DISTINCT devise, devise, devise, TRUE FROM penalite
ON CONFLICT (code) DO NOTHING;

-- Noms et symboles explicites pour les devises usuelles
UPDATE devise SET nom = 'Franc Congolais', symbole = 'FC' WHERE code = 'CDF';
UPDATE devise SET nom = 'Dollar Américain', symbole = '$' WHERE code = 'USD';

-- ----------------------------------------------------------------------------
-- 4. Contraintes de clé étrangère vers devise(code)
-- ----------------------------------------------------------------------------

ALTER TABLE tontine
    ADD CONSTRAINT fk_tontine_devise FOREIGN KEY (devise)
        REFERENCES devise (code) ON DELETE NO ACTION;

ALTER TABLE transaction
    ADD CONSTRAINT fk_transaction_devise FOREIGN KEY (devise)
        REFERENCES devise (code) ON DELETE NO ACTION;

ALTER TABLE credit
    ADD CONSTRAINT fk_credit_devise FOREIGN KEY (devise)
        REFERENCES devise (code) ON DELETE NO ACTION;

ALTER TABLE penalite
    ADD CONSTRAINT fk_penalite_devise FOREIGN KEY (devise)
        REFERENCES devise (code) ON DELETE NO ACTION;

-- ----------------------------------------------------------------------------
-- 5. Indexes sur les clés étrangères de taux_change
-- ----------------------------------------------------------------------------

CREATE INDEX idx_taux_devise_source ON taux_change (devise_source);
CREATE INDEX idx_taux_devise_cible  ON taux_change (devise_cible);
