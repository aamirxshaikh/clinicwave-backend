package com.clinicwave.usermanagementservice.service.impl;

import com.clinicwave.usermanagementservice.exception.RefreshTokenException;
import com.clinicwave.usermanagementservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public void storeRefreshToken(String username, String refreshToken, long expiryMs) {
    String key = buildKey(username);
    if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
      throw new RefreshTokenException("User already has an active session. Please logout first.");
    }
    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    ops.set(key, refreshToken, expiryMs, TimeUnit.MILLISECONDS);
  }

  @Override
  public boolean validateRefreshToken(String username, String token) {
    String storedToken = redisTemplate.opsForValue().get(buildKey(username));
    return token.equals(storedToken);
  }

  @Override
  public void deleteRefreshToken(String username) {
    redisTemplate.delete(buildKey(username));
  }

  private String buildKey(String username) {
    return "refresh-token:" + username;
  }
}
