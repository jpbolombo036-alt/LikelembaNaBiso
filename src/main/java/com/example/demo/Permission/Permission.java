package com.example.demo.Permission;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entité représentant une permission dans le système RBAC.
 * Une permission définit une action autorisée sur une ressource.
 */
@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permission")
    private Long idPermission;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, unique = true, length = 50)
    private PermissionType type;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "ressource", nullable = false, length = 50)
    private String ressource;

    @Column(name = "action", nullable = false, length = 30)
    private String action;

    @Column(name = "actif")
    @Builder.Default
    private boolean actif = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
