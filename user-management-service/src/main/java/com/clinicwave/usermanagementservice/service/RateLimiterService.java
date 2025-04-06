package com.clinicwave.usermanagementservice.service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface RateLimiterService {
  boolean isLimitExceeded(String key, int maxAttempts, long duration, TimeUnit timeUnit);

  long getAttemptsRemaining(String key, int limit);

  Duration getTimeToReset(String key);
}
