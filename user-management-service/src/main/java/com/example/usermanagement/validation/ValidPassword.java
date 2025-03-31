package com.example.usermanagement.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
  String message() default "Password must be at least 8 characters long, include an uppercase letter, a lowercase letter, a number, and a special character";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
