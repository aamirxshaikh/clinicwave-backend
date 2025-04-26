package com.clinicwave.usermanagementservice.service;

public interface TokenBlacklistService {
  void blacklistToken(String token, long expiryMs);

  boolean isTokenBlacklisted(String token);
} 