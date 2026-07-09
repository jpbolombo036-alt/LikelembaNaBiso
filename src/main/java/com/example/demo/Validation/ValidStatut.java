package com.example.demo.Validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation de validation personnalisée pour le statut d'une organisation.
 * Valeurs acceptées : ACTIF, INACTIF, SUSPENDU.
 */
@Documented
@Constraint(validatedBy = StatutValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStatut {

    String message() default "Statut invalide. Valeurs acceptées : ACTIF, INACTIF, SUSPENDU";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Valeurs autorisées (personnalisables).
     */
    String[] valeurs() default {"ACTIF", "INACTIF", "SUSPENDU"};
}
