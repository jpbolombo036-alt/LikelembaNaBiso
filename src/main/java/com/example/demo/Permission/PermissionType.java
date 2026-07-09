package com.example.demo.Permission;

/**
 * Enumération des permissions disponibles dans l'application.
 * Format : ACTION_RESSOURCE
 */
public enum PermissionType {

    // Permissions sur les utilisateurs
    LIRE_UTILISATEUR,
    CREER_UTILISATEUR,
    MODIFIER_UTILISATEUR,
    SUPPRIMER_UTILISATEUR,

    // Permissions sur les organisations
    LIRE_ORGANISATION,
    CREER_ORGANISATION,
    MODIFIER_ORGANISATION,
    SUPPRIMER_ORGANISATION,

    // Permissions administrateur
    GERER_ROLES,
    GERER_PERMISSIONS,
    ACCEDER_ADMINISTRATION
}
