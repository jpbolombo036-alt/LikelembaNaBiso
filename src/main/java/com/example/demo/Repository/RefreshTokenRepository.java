package com.example.demo.Repository;

import com.example.demo.Entity.RefreshToken;
import com.example.demo.Entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUtilisateurAndRevokedFalse(Utilisateur utilisateur);

    List<RefreshToken> findByDateExpirationBefore(LocalDateTime dateTime);
}
