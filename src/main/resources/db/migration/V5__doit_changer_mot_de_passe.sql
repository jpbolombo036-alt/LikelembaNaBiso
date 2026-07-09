-- Migration Flyway : V5__doit_changer_mot_de_passe.sql
-- Ajoute un drapeau forçant le changement du mot de passe au premier login
-- (utilisé quand un mot de passe par défaut est attribué à la création).

ALTER TABLE utilisateur
    ADD COLUMN doit_changer_mot_de_passe BOOLEAN NOT NULL DEFAULT FALSE;
