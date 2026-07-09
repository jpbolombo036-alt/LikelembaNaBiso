package com.example.demo.Service.impl;

import com.example.demo.Dto.request.DeviseRequest;
import com.example.demo.Dto.response.DeviseResponse;
import com.example.demo.Entity.Devise;
import com.example.demo.Repository.DeviseRepository;
import com.example.demo.Service.DeviseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des devises de référence.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeviseServiceImpl implements DeviseService {

    private final DeviseRepository deviseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DeviseResponse> lister() {
        return deviseRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviseResponse> listerActives() {
        return deviseRepository.findByActifTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DeviseResponse creer(DeviseRequest request) {
        String code = request.getCode().toUpperCase();
        if (deviseRepository.existsByCode(code)) {
            throw new IllegalStateException("Cette devise existe déjà : " + code);
        }

        Devise devise = Devise.builder()
                .code(code)
                .nom(request.getNom())
                .symbole(request.getSymbole())
                .actif(request.getActif() == null ? Boolean.TRUE : request.getActif())
                .build();

        Devise saved = deviseRepository.save(devise);
        log.info("Devise créée : {}", saved.getCode());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public DeviseResponse changerActif(String code, boolean actif) {
        Devise devise = resoudre(code);
        devise.setActif(actif);
        Devise saved = deviseRepository.save(devise);
        log.info("Devise {} → actif={}", code, actif);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Devise resoudre(String code) {
        return deviseRepository.findById(code.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Devise introuvable : " + code));
    }

    private DeviseResponse toResponse(Devise d) {
        return DeviseResponse.builder()
                .code(d.getCode())
                .nom(d.getNom())
                .symbole(d.getSymbole())
                .actif(d.getActif())
                .build();
    }
}
