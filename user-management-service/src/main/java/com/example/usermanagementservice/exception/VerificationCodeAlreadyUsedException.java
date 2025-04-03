package com.example.usermanagementservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VerificationCodeAlreadyUsedException extends RuntimeException {
  public VerificationCodeAlreadyUsedException(String message) {
    super(message);
  }
}
