package com.clinicwave.usermanagementservice.service.impl;

import com.clinicwave.usermanagementservice.service.RateLimiterService;
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

  @Override
  public boolean isLimitExceeded(String key, int maxAttempts, long duration, TimeUnit timeUnit) {
    Long attempts = redisTemplate.opsForValue().increment(key, 1);
    if (attempts != null && attempts == 1)
      redisTemplate.expire(key, duration, timeUnit); // Set expiration time for the key if it is a new key
    return attempts != null && attempts > maxAttempts;
  }

  @Override
  public long getAttemptsRemaining(String key, int limit) {
    Long attempts = redisTemplate.opsForValue().get(key);
    if (attempts == null) return limit;
    return Math.max(0, limit - attempts);
  }

  @Override
  public Duration getTimeToReset(String key) {
    Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);

    return Optional.of(ttl)
            .filter(t -> t > 0)
            .map(Duration::ofSeconds)
            .orElse(Duration.ZERO);
  }
}
