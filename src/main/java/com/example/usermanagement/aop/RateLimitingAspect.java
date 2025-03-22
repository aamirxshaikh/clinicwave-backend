package com.example.usermanagement.aop;

import com.example.usermanagement.exception.RateLimitExceededException;
import com.example.usermanagement.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitingAspect {
  private final RateLimiterService rateLimiterService;

  @Around("execution(* com.example.usermanagement.controller.AuthController.authenticateUser(..))")
  public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
    // Get request and response
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    if (attributes == null) {
      log.warn("RequestContextHolder returned null attributes - cannot apply rate limiting");
      return joinPoint.proceed(); // Continue without rate limiting
    }

    HttpServletRequest request = attributes.getRequest();
    HttpServletResponse response = attributes.getResponse();

    // Get client IP address
    String clientIp = getClientIP(request);

    // Rate limit key
    String key = "rate:register:" + clientIp;

    if (rateLimiterService.isLimitExceeded(key)) {
      // Add rate limit headers when the limit is exceeded
      if (response != null) addRateLimitHeaders(response, key);

      Duration timeToReset = rateLimiterService.getTimeToReset(key);
      log.warn("Rate limit exceeded for client IP: {}. Try again in {} seconds.", clientIp, timeToReset.toSeconds());
      throw new RateLimitExceededException("Too many registration attempts. Try again in " +
              timeToReset.toSeconds() + " seconds.");
    }

    // Add rate limit headers to successful responses
    if (response != null) addRateLimitHeaders(response, key);

    return joinPoint.proceed();
  }

  private void addRateLimitHeaders(HttpServletResponse response, String key) {
    Duration timeToReset = rateLimiterService.getTimeToReset(key);
    response.addHeader("X-RateLimit-Limit", String.valueOf(rateLimiterService.getMaxAttempts()));
    response.addHeader("X-RateLimit-Remaining", String.valueOf(rateLimiterService.getAttemptsRemaining(key)));
    response.addHeader("X-RateLimit-Reset", String.valueOf(timeToReset));
    response.addHeader("Retry-After", String.valueOf(timeToReset));
  }

  private String getClientIP(HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }
}