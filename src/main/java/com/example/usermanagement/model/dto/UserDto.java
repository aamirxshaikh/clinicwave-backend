package com.example.usermanagement.model.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record UserDto(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        Set<String> roles,
        boolean enabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy
) {
}
