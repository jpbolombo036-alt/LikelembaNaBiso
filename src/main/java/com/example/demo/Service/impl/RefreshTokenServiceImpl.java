package com.example.demo.Service.impl;

import com.example.demo.Dto.response.LoginResponse;
import com.example.demo.Dto.response.RefreshTokenResponse;
import com.example.demo.Entity.RefreshToken;
import com.example.demo.Entity.Utilisateur;
import com.example.demo.Repository.RefreshTokenRepository;
import com.example.demo.Security.JwtService;
import com.example.demo.Service.RefreshTokenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Override
    @Transactional
    public RefreshTokenResponse rafraichir(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Refresh token introuvable"));

        if (refreshToken.isRevoked() || refreshToken.getDateExpiration().isBefore(LocalDateTime.now())) {
            throw new EntityNotFoundException("Refresh token invalide ou expiré");
        }

        Utilisateur utilisateur = refreshToken.getUtilisateur();
        String accessToken = jwtService.genererToken(utilisateur);
        long expiresIn = jwtService.getJwtExpiration() / 1000;

        String newRefreshToken = jwtService.genererRefreshToken(utilisateur);
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        RefreshToken newToken = creerRefreshToken(utilisateur, newRefreshToken);
        refreshTokenRepository.save(newToken);

        return RefreshTokenResponse.builder()
                .token(accessToken)
                .type("Bearer")
                .expiresIn(expiresIn)
                .refreshToken(newToken.getToken())
                .build();
    }

    @Override
    @Transactional
    public void revokeToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Refresh token introuvable"));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public void logout(UUID utilisateurId) {
        List<RefreshToken> tokens = refreshTokenRepository.findByUtilisateurAndRevokedFalse(
                Utilisateur.builder().idUtilisateur(utilisateurId).build()
        );
        for (RefreshToken token : tokens) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        }
    }

    public RefreshToken creerRefreshToken(Utilisateur utilisateur, String token) {
        return RefreshToken.builder()
                .utilisateur(utilisateur)
                .token(token)
                .dateExpiration(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
    }
}
