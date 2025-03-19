package com.example.usermanagement.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 6, max = 40) String newPassword,
        @NotBlank @Size(min = 6, max = 40) String confirmPassword
) {
}
