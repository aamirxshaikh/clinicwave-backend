package com.clinicwave.usermanagementservice.service;

public interface EmailService {
  void sendVerificationEmail(String to, String code);
}