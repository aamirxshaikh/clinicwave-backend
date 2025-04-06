package com.clinicwave.usermanagementservice.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateUserRequest(
        @Size(min = 3, max = 50) String username,
        @Size(max = 100) @Email String email,
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        Boolean enabled,
        Set<String> roles
) {
}
