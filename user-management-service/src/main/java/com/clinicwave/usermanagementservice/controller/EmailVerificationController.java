package com.clinicwave.usermanagementservice.controller;

import com.clinicwave.usermanagementservice.annotation.RateLimit;
import com.clinicwave.usermanagementservice.model.request.VerifyEmailRequest;
import com.clinicwave.usermanagementservice.model.response.MessageResponse;
import com.clinicwave.usermanagementservice.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/verify")
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