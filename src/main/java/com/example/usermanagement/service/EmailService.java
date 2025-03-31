package com.example.usermanagement.service;

public interface EmailService {
    void sendVerificationEmail(String to, String code);
}