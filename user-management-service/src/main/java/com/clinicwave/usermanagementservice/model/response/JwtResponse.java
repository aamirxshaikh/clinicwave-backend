package com.clinicwave.usermanagementservice.model.response;

import java.util.List;

public record JwtResponse(
        String accessToken,
        String refreshToken,
        String type,
        Long id,
        String username,
        String email,
        List<String> roles
) {
}
