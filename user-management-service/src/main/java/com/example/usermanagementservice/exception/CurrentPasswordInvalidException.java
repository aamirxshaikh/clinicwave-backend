package com.example.usermanagementservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class CurrentPasswordInvalidException extends RuntimeException {
  public CurrentPasswordInvalidException() {
    super("Current password is invalid");
  }
}
