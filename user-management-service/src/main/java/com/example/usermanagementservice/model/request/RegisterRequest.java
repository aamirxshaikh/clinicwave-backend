package com.example.usermanagementservice.model.request;

import com.example.usermanagementservice.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Size(max = 100) @Email String email,
        @NotBlank @Size(min = 6, max = 40) @ValidPassword String password,
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        Set<String> roles
) {
}
