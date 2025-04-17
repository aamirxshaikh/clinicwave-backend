package com.clinicwave.usermanagementservice.model.response;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {
}
