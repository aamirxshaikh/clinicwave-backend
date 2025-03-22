package com.example.usermanagement.service;

import java.time.Duration;

public interface RateLimiterService {
  boolean isLimitExceeded(String key);

  long getAttemptsRemaining(String key);

  Duration getTimeToReset(String key);

  int getMaxAttempts();
}
