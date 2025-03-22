package com.example.usermanagement.aop;

import com.example.usermanagement.exception.RateLimitExceededException;
import com.example.usermanagement.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
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
    // Get client IP address
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    String clientIp = getClientIP(request);

    // Rate limit key
    String key = "rate:register:" + clientIp;

    if (rateLimiterService.isLimitExceeded(key)) {
      Duration timeToReset = rateLimiterService.getTimeToReset(key);
      log.warn("Rate limit exceeded for client IP: {}. Try again in {} seconds.", clientIp, timeToReset.toSeconds());
      throw new RateLimitExceededException("Too many registration attempts. Try again in " +
              timeToReset.toSeconds() + " seconds.");
    }

    return joinPoint.proceed();
  }

  private String getClientIP(HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }
}