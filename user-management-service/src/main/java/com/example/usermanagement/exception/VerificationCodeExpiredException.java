package com.example.usermanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.GONE)
public class VerificationCodeExpiredException extends RuntimeException {
  public VerificationCodeExpiredException(String message) {
    super(message);
  }
}
