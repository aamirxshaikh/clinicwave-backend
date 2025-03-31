package com.example.notificationservice.exception;

import com.example.notificationservice.model.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(EmailSendingException.class)
  public ResponseEntity<ErrorResponse> handleEmailSendingException(
          EmailSendingException ex, WebRequest request) {

    log.error("Email sending exception: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
            .status(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase())
            .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value())
            .message(ex.getMessage())
            .path(getPath(request))
            .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
    log.error("Global exception: ", ex);

    ErrorResponse errorResponse = ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("An unexpected error occurred")
            .errors(Collections.singletonList(ex.getMessage()))
            .path(getPath(request))
            .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private String getPath(WebRequest request) {
    return ((ServletWebRequest) request).getRequest().getRequestURI();
  }
}
