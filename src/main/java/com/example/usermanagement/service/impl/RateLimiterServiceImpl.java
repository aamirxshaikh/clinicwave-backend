package com.example.usermanagement.service.impl;

import com.example.usermanagement.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RateLimiterServiceImpl implements RateLimiterService {
  private final RedisTemplate<String, Long> redisTemplate;
  private static final int MAX_ATTEMPTS = 5;
  private static final long WINDOW_MINUTES = 1;

  @Override
  public boolean isLimitExceeded(String key) {
    Long attempts = redisTemplate.opsForValue().increment(key, 1);
    if (attempts == 1) {
      // Set expiration time for the key if it is a new key
      redisTemplate.expire(key, WINDOW_MINUTES, TimeUnit.MINUTES);
    }
    return attempts > MAX_ATTEMPTS;
  }

  @Override
  public long getAttemptsRemaining(String key) {
    Long attempts = redisTemplate.opsForValue().get(key);
    if (attempts == null) {
      return MAX_ATTEMPTS;
    }
    return Math.max(0, MAX_ATTEMPTS - attempts);
  }

  @Override
  public Duration getTimeToReset(String key) {
    Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
    if (ttl == null || ttl <= 0) {
      return Duration.ZERO;
    }
    return Duration.ofSeconds(ttl);
  }

  @Override
  public int getMaxAttempts() {
    return MAX_ATTEMPTS;
  }
}
