package com.example.usermanagement.service.impl;

import com.example.usermanagement.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
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
    if (attempts != null && attempts == 1) {
      // Set expiration time for the key if it is a new key
      redisTemplate.expire(key, WINDOW_MINUTES, TimeUnit.MINUTES);
    }
    return attempts != null && attempts > MAX_ATTEMPTS;
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

    return Optional.of(ttl)
            .filter(t -> t > 0)
            .map(Duration::ofSeconds)
            .orElse(Duration.ZERO);
  }

  @Override
  public int getMaxAttempts() {
    return MAX_ATTEMPTS;
  }
}
