package com.clinicwave.usermanagementservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyVerifiedException extends RuntimeException {
  public EmailAlreadyVerifiedException(String email) {
    super(String.format("Email '%s' is already verified", email));
  }
}
