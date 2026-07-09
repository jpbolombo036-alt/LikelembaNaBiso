package com.example.demo.Dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenResponse {

    private String token;
    private String type;
    private long expiresIn;
    private String refreshToken;
}
