package com.example.usermanagement.aop;

import com.example.usermanagement.annotation.RateLimit;
import com.example.usermanagement.exception.RateLimitExceededException;
import com.example.usermanagement.security.service.UserDetailsImpl;
import com.example.usermanagement.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitingAspect {
  private final RateLimiterService rateLimiterService;

  @Around("@annotation(com.example.usermanagement.annotation.RateLimit)")
  public Object rateLimitByAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
    // Get method signature and extract annotation
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    RateLimit rateLimitAnnotation = method.getAnnotation(RateLimit.class);

    // Setup rate limiting parameters
    int limit = rateLimitAnnotation.limit();
    long duration = rateLimitAnnotation.duration();
    TimeUnit timeUnit = rateLimitAnnotation.timeUnit();
    String prefix = rateLimitAnnotation.prefix();
    boolean useAuthenticatedUser = rateLimitAnnotation.useAuthenticatedUser();

    // Get request attributes
    ServletRequestAttributes attributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    if (attributes == null) {
      log.warn("RequestContextHolder returned null attributes - cannot apply rate limiting");
      return joinPoint.proceed();
    }

    HttpServletRequest request = attributes.getRequest();
    HttpServletResponse response = attributes.getResponse();

    // Determine the rate limit key
    String identifier;
    if (useAuthenticatedUser) {
      // Use user ID from authenticated principal
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
        identifier = userDetails.getId().toString();
      } else {
        // Fall back to IP address if no authenticated user
        identifier = getClientIP(request);
      }
    } else {
      // Use IP address
      identifier = getClientIP(request);
    }

    String key = prefix + identifier;

    // Apply rate limiting
    boolean limitExceeded = rateLimiterService.isLimitExceeded(key, limit, duration, timeUnit);

    if (limitExceeded) {
      if (response != null) {
        addRateLimitHeaders(response, key, limit);
      }

      Duration timeToReset = rateLimiterService.getTimeToReset(key);
      String readableTime = formatDuration(timeToReset);
      log.warn("Rate limit exceeded for key: {}. Try again in {}.",
              key, readableTime);

      throw new RateLimitExceededException("Too many requests. Try again in " + readableTime);
    }

    // Add rate limit headers to successful responses
    if (response != null) {
      addRateLimitHeaders(response, key, limit);
    }

    return joinPoint.proceed();
  }

  private String formatDuration(Duration duration) {
    long seconds = duration.getSeconds();
    long hours = seconds / 3600;
    long minutes = (seconds % 3600) / 60;
    long remainingSeconds = seconds % 60;

    if (hours > 0) {
      return String.format("%d hours, %d minutes, and %d seconds", hours, minutes, remainingSeconds);
    } else if (minutes > 0) {
      return String.format("%d minutes and %d seconds", minutes, remainingSeconds);
    } else {
      return String.format("%d seconds", remainingSeconds);
    }
  }

  private void addRateLimitHeaders(HttpServletResponse response, String key, int limit) {
    Duration timeToReset = rateLimiterService.getTimeToReset(key);
    response.addHeader("X-RateLimit-Limit", String.valueOf(limit));
    response.addHeader("X-RateLimit-Remaining", String.valueOf(rateLimiterService.getAttemptsRemaining(key, limit)));
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