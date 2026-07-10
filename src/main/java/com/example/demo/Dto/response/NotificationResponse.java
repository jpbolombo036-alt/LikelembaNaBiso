package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class NotificationResponse {

    private UUID idNotification;

    private String type;

    private String canal;

    private String message;

    private String statut;

    private String tontineNom;

    private LocalDateTime dateCreation;

    private LocalDateTime dateEnvoi;
}
