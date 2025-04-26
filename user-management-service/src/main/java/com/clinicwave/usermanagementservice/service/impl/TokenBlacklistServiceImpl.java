package com.clinicwave.usermanagementservice.service.impl;

import com.clinicwave.usermanagementservice.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {
  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public void blacklistToken(String token, long expiryMs) {
    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    ops.set(buildKey(token), "blacklisted", expiryMs, TimeUnit.MILLISECONDS);
  }

  @Override
  public boolean isTokenBlacklisted(String token) {
    String value = redisTemplate.opsForValue().get(buildKey(token));
    return value != null;
  }

  private String buildKey(String token) {
    return "blacklisted-token:" + token;
  }
} 