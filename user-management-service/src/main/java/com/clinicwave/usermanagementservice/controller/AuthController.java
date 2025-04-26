package com.clinicwave.usermanagementservice.controller;

import com.clinicwave.usermanagementservice.annotation.RateLimit;
import com.clinicwave.usermanagementservice.model.request.LoginRequest;
import com.clinicwave.usermanagementservice.model.request.RegisterRequest;
import com.clinicwave.usermanagementservice.model.response.JwtResponse;
import com.clinicwave.usermanagementservice.model.response.MessageResponse;
import com.clinicwave.usermanagementservice.model.response.RefreshTokenResponse;
import com.clinicwave.usermanagementservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  @RateLimit(prefix = "rate:authenticate")
  public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    return ResponseEntity.ok(authService.authenticateUser(loginRequest));
  }

  @PostMapping("/register")
  @RateLimit(duration = 10, prefix = "rate:register")
  public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
    return ResponseEntity.ok(authService.registerUser(registerRequest));
  }

  @PostMapping("/refresh-token")
  @RateLimit(prefix = "rate:refresh")
  public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestHeader("X-Refresh-Token") String refreshToken) {
    return ResponseEntity.ok(authService.refreshToken(refreshToken));
  }

  @PostMapping("/logout")
  public ResponseEntity<MessageResponse> logout(
          @RequestHeader("Authorization") String authHeader,
          @RequestHeader("X-Refresh-Token") String refreshToken) {
    return ResponseEntity.ok(authService.logout(authHeader, refreshToken));
  }
}
