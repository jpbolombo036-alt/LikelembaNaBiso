package com.example.demo.Service;

import com.example.demo.Dto.response.RefreshTokenResponse;

import java.util.UUID;

public interface RefreshTokenService {

    RefreshTokenResponse rafraichir(String refreshToken);

    void revokeToken(String refreshToken);

    void logout(UUID utilisateurId);
}
