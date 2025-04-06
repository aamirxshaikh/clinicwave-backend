package com.clinicwave.usermanagementservice.model.dto;

import java.util.Set;

public record UserDto(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        Set<String> roles,
        boolean enabled,
        boolean emailVerified
) {
}
