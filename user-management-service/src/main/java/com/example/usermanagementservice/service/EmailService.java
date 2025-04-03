package com.example.usermanagementservice.service;

public interface EmailService {
  void sendVerificationEmail(String to, String code);
}