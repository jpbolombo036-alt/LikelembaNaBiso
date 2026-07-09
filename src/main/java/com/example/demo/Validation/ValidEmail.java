package com.example.demo.Validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation de validation personnalisée pour les adresses email.
 * Usage : @ValidEmail sur un champ String.
 */
@Documented
@Constraint(validatedBy = EmailValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {

    String message() default "Adresse email invalide";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
