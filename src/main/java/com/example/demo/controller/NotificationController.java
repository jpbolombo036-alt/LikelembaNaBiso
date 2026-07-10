package com.example.demo.controller;

import com.example.demo.Dto.response.NotificationResponse;
import com.example.demo.Entity.Notification;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Service.NotificationService;
import com.example.demo.Security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtService jwtService;

    @GetMapping("/agent")
    public ResponseEntity<List<NotificationResponse>> listerPourAgent(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @RequestParam(name = "type", required = false) String type) {
        UUID agentId = jwtService.extraireIdUtilisateur(extractToken(authorizationHeader));
        Utilisateur agent = Utilisateur.builder().idUtilisateur(agentId).build();
        List<NotificationResponse> notifications = notificationService.listerPourAgent(agent, type).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/{id}/lu")
    public ResponseEntity<Void> marquerCommeLue(
            @RequestHeader(name = "Authorization") String authorizationHeader,
            @PathVariable("id") UUID idNotification) {
        UUID agentId = jwtService.extraireIdUtilisateur(extractToken(authorizationHeader));
        notificationService.marquerCommeLue(idNotification, agentId);
        return ResponseEntity.noContent().build();
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .idNotification(n.getIdNotification())
                .type(n.getType())
                .canal(n.getCanal())
                .message(n.getMessage())
                .statut(n.getStatut())
                .tontineNom(n.getTontine() != null ? n.getTontine().getNom() : null)
                .dateCreation(n.getDateCreation())
                .dateEnvoi(n.getDateEnvoi())
                .build();
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Header Authorization manquant ou invalide");
        }
        return authorizationHeader.substring(7);
    }
}
