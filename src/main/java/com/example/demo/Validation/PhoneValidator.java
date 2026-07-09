package com.example.demo.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Logique de validation pour l'annotation @ValidPhone.
 * Accepte les formats internationaux et locaux (ex: +33612345678, 0612345678).
 */
public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+\\d{1,3}[\\s.-]?)?(\\(?\\d{1,4}\\)?[\\s.-]?)(\\d{1,4}[\\s.-]?){1,5}\\d{1,4}$"
    );

    @Override
    public void initialize(ValidPhone constraintAnnotation) {
        // Pas d'initialisation nécessaire
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null || phone.isBlank()) {
            return true; // Utiliser @NotBlank séparément si le champ est obligatoire
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
}
