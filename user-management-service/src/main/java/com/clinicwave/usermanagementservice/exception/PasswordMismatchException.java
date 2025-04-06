package com.clinicwave.usermanagementservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class PasswordMismatchException extends RuntimeException {
  public PasswordMismatchException() {
    super("New password and confirm password do not match");
  }
}
