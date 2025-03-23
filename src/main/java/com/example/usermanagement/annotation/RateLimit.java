package com.example.usermanagement.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
  /**
   * Maximum number of requests allowed within the time window
   */
  int limit() default 5;

  /**
   * Time window duration
   */
  long duration() default 1;

  /**
   * Time unit for the duration
   */
  TimeUnit timeUnit() default TimeUnit.MINUTES;

  /**
   * Rate limit key prefix to identify the limited resource
   */
  String prefix() default "rate:default:";

  /**
   * Whether to use the authenticated user ID as the rate limit key
   * If false, client IP address will be used
   */
  boolean useAuthenticatedUser() default false;
}
