package com.example.usermanagementservice.model.request;

import com.example.usermanagementservice.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 6, max = 40) @ValidPassword String newPassword,
        @NotBlank @Size(min = 6, max = 40) String confirmPassword
) {
}
