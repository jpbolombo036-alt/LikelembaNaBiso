-- Migration Flyway : V3__ajouter_mode_paiement_cash.sql
-- Ajoute le mode de paiement cash et assouplit les contraintes pour les champs Mobile Money

ALTER TABLE transaction
    ADD COLUMN mode_paiement VARCHAR(20) NOT NULL DEFAULT 'MOBILE_MONEY';

ALTER TABLE transaction
    ALTER COLUMN reference_operateur DROP NOT NULL;

ALTER TABLE transaction
    ALTER COLUMN numero_telephone_payeur DROP NOT NULL;
