package com.example.usermanagement.exception;

import com.example.usermanagement.model.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
          ResourceNotFoundException ex, WebRequest request) {

    log.error("Resource not found exception: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.getReasonPhrase())
            .statusCode(HttpStatus.NOT_FOUND.value())
            .message(ex.getMessage())
            .path(getPath(request))
            .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InvalidRoleException.class)
  public ResponseEntity<ErrorResponse> handleInvalidRoleException(
          InvalidRoleException ex, WebRequest request) {

    log.error("Invalid role exception: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .message(ex.getMessage())
            .path(getPath(request))
            .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(CurrentPasswordInvalidException.class)
  public ResponseEntity<ErrorResponse> handleCurrentPasswordInvalidException(
          CurrentPasswordInvalidException ex, WebRequest request) {

    log.error("Current password invalid exception: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
            .status(HttpStatus.UNAUTHORIZED.getReasonPhrase())
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .message(ex.getMessage())
            .path(getPath(request))
            .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(PasswordMismatchException.class)
  public ResponseEntity<ErrorResponse> handlePasswordMismatchException(
          PasswordMismatchException ex, WebRequest request) {

    log.error("Password mismatch exception: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
            .status(HttpStatus.UNAUTHORIZED.getReasonPhrase())
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .message(ex.getMessage())
            .path(getPath(request))
            .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentialsException(
          BadCredentialsException ex, WebRequest request) {

    log.error("Bad credentials exception: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
            .status(HttpStatus.UNAUTHORIZED.getReasonPhrase())
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .message("Invalid username or password")
            .path(getPath(request))
            .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(
          AccessDeniedException ex, WebRequest request) {

    log.error("Access denied exception: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
            .status(HttpStatus.FORBIDDEN.getReasonPhrase())
            .statusCode(HttpStatus.FORBIDDEN.value())
            .message("You don't have permission to access this resource")
            .path(getPath(request))
            .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationErrors(
          MethodArgumentNotValidException ex, WebRequest request) {

    List<String> errors = ex.getBindingResult().getFieldErrors()
            .stream().map(FieldError::getDefaultMessage).toList();

    ErrorResponse errorResponse = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .message("Validation error")
            .errors(errors)
            .path(getPath(request))
            .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(
          ConstraintViolationException ex, WebRequest request) {

    List<String> errors = new ArrayList<>();
    ex.getConstraintViolations().forEach(cv -> errors.add(cv.getMessage()));

    ErrorResponse errorResponse = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .message("Validation error")
            .errors(errors)
            .path(getPath(request))
            .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
    log.error("Global exception: ", ex);

    ErrorResponse errorResponse = ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("An unexpected error occurred")
            .path(getPath(request))
            .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private String getPath(WebRequest request) {
    return ((ServletWebRequest) request).getRequest().getRequestURI();
  }
}
