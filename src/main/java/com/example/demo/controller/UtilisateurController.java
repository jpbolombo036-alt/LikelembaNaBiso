package com.example.demo.controller;

import com.example.demo.Dto.request.ReinitialisationMasseRequest;
import com.example.demo.Dto.response.UtilisateurResponse;
import com.example.demo.Service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/Utilisateur")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @PostMapping("/{id}/roles/{roleId}")
    public ResponseEntity<UtilisateurResponse> assignerRole(
            @PathVariable UUID id,
            @PathVariable String roleId) {
        return ResponseEntity.ok(utilisateurService.assignerRole(id, roleId));
    }

    @GetMapping("/by-role/{roleId}")
    public ResponseEntity<List<UtilisateurResponse>> listerParRole(@PathVariable String roleId) {
        return ResponseEntity.ok(utilisateurService.listerParRole(roleId));
    }

    @PostMapping("/reinitialiser-masse")
    public ResponseEntity<Void> reinitialiserMasse(@Valid @RequestBody ReinitialisationMasseRequest request) {
        utilisateurService.reinitialiserEnMasse(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/toggle-statut/{id}")
    public ResponseEntity<UtilisateurResponse> toggleStatut(@PathVariable UUID id) {
        return ResponseEntity.ok(utilisateurService.toggleStatut(id));
    }
}
