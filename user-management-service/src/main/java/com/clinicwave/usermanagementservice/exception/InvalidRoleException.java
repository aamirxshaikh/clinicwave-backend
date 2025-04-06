package com.clinicwave.usermanagementservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRoleException extends RuntimeException {
  public InvalidRoleException(String fieldValue) {
    super(String.format("Role '%s' is invalid", fieldValue));
  }
}
