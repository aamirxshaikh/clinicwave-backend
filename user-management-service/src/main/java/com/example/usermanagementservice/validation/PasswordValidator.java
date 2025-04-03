package com.example.usermanagementservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
  private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$";

  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    // Let @NotBlank handle null or empty values
    if (password == null || password.isBlank()) {
      return true; // Skip regex validation, @NotBlank will handle this case
    }

    // Let @Size handle length constraints first
    if (password.length() < 6 || password.length() > 40) {
      return true; // Skip regex validation, @Size will handle this case
    }

    // Now apply regex validation for password strength
    return password.matches(PASSWORD_PATTERN);
  }
}
