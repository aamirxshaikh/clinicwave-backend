package com.example.usermanagement.controller;

import com.example.usermanagement.annotation.RateLimit;
import com.example.usermanagement.model.request.VerifyEmailRequest;
import com.example.usermanagement.model.response.MessageResponse;
import com.example.usermanagement.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/verify")
@RequiredArgsConstructor
public class EmailVerificationController {
  private final EmailVerificationService emailVerificationService;

  @PostMapping("/resend")
  @RateLimit(limit = 3, duration = 10, prefix = "rate:resend-verification-email")
  public ResponseEntity<MessageResponse> resendVerificationEmail(@RequestParam String email) {
    emailVerificationService.resendVerificationEmail(email);
    return ResponseEntity.ok(new MessageResponse("Verification code sent successfully"));
  }

  @PostMapping
  @RateLimit(duration = 30, prefix = "rate:verify-email")
  public ResponseEntity<MessageResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
    emailVerificationService.verifyEmail(request.email(), request.code());
    return ResponseEntity.ok(new MessageResponse("Email verified successfully"));
  }
} 