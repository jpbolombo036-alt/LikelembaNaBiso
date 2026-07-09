-- Migration Flyway : V2__add_utilisateur_actif.sql
-- Ajoute la colonne actif à l'entité Utilisateur (ajoutée après V1)

ALTER TABLE utilisateur
    ADD COLUMN actif BOOLEAN NOT NULL DEFAULT TRUE;
