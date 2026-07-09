package com.example.demo.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

/**
 * Logique de validation pour l'annotation @ValidStatut.
 */
public class StatutValidator implements ConstraintValidator<ValidStatut, String> {

    private List<String> valeursAutorisees;

    @Override
    public void initialize(ValidStatut constraintAnnotation) {
        this.valeursAutorisees = Arrays.asList(constraintAnnotation.valeurs());
    }

    @Override
    public boolean isValid(String statut, ConstraintValidatorContext context) {
        if (statut == null || statut.isBlank()) {
            return true; // Utiliser @NotBlank séparément si le champ est obligatoire
        }
        return valeursAutorisees.contains(statut.toUpperCase());
    }
}
