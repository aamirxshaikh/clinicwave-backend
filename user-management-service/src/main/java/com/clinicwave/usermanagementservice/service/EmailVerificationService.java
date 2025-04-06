package com.clinicwave.usermanagementservice.service;

public interface EmailVerificationService {
  void sendVerificationEmail(String email);

  void resendVerificationEmail(String email);

  void verifyEmail(String token, String code);
}