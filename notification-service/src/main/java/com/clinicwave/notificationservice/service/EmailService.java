package com.clinicwave.notificationservice.service;

public interface EmailService {
  void sendVerificationEmail(String to, String code);
} 