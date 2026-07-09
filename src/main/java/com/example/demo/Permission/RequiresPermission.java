package com.example.demo.Permission;

import java.lang.annotation.*;

/**
 * Annotation de sécurité personnalisée pour restreindre l'accès à une méthode.
 * Usage : @RequiresPermission(PermissionType.CREER_UTILISATEUR)
 *
 * Exemple :
 * <pre>
 *   {@literal @}RequiresPermission(PermissionType.SUPPRIMER_ORGANISATION)
 *   public void supprimerOrganisation(UUID id) { ... }
 * </pre>
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {

    /**
     * La (ou les) permission(s) requises pour accéder à la méthode.
     */
    PermissionType[] value();

    /**
     * Si true, toutes les permissions listées sont requises.
     * Si false (défaut), une seule suffit.
     */
    boolean all() default false;
}
