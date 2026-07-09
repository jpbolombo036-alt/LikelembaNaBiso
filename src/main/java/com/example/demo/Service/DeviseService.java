package com.example.demo.Service;

import com.example.demo.Dto.request.DeviseRequest;
import com.example.demo.Dto.response.DeviseResponse;
import com.example.demo.Entity.Devise;

import java.util.List;

/**
 * Service de gestion des devises de référence.
 */
public interface DeviseService {

    /** Liste toutes les devises. */
    List<DeviseResponse> lister();

    /** Liste uniquement les devises actives. */
    List<DeviseResponse> listerActives();

    /** Crée une nouvelle devise. */
    DeviseResponse creer(DeviseRequest request);

    /** Active ou désactive une devise. */
    DeviseResponse changerActif(String code, boolean actif);

    /**
     * Résout une devise par son code (ou lève une exception si introuvable).
     * Utilisé par les services métier pour convertir un code string en entité Devise.
     */
    Devise resoudre(String code);
}
