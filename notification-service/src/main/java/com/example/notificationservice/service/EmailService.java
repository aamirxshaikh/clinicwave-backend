package com.example.notificationservice.service;

public interface EmailService {
  void sendVerificationEmail(String to, String code);
} 