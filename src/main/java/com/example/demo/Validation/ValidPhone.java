package com.example.demo.Validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation de validation personnalisée pour les numéros de téléphone.
 * Usage : @ValidPhone sur un champ String.
 */
@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhone {

    String message() default "Numéro de téléphone invalide";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
