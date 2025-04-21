package com.clinicwave.usermanagementservice.service;

public interface RefreshTokenService {
  void storeRefreshToken(String username, String refreshToken, long expiryMs);

  boolean validateRefreshToken(String username, String token);

  void deleteRefreshToken(String username);
}
