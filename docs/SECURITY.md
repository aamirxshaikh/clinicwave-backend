# Spring Security & JWT Implementation Documentation

## Overview

This document provides a comprehensive guide to the Spring Security and JWT implementation in the spring-boot-auth
project. It explains the security architecture, authentication flow, and key components that work together to secure the
application.

## Table of Contents

1. [Core Security Configuration](#1-core-security-configuration)
2. [JWT Implementation](#2-jwt-implementation)
3. [User Authentication](#3-user-authentication)
4. [Rate Limiting for Security](#4-rate-limiting-for-security)
5. [Security Flow](#5-security-flow)
6. [Redis Integration for Rate Limiting](#6-redis-integration-for-rate-limiting)
7. [Key Security Features and Best Practices](#7-key-security-features-and-best-practices)
8. [Areas for Potential Enhancement](#8-areas-for-potential-enhancement)

## 1. Core Security Configuration

### WebSecurityConfig

This is the central configuration class for Spring Security in the application, which:

- Uses `@EnableWebSecurity` annotation to enable Spring Security web security support
- Extends `WebSecurityConfigurerAdapter` (Spring Security 5.x approach)
- Configures the security filter chain
- Sets up authentication manager, JWT filter, and CORS/CSRF settings

Key features in this class:

- **Authentication Manager Configuration**: Sets up the authentication manager bean using the custom
  `UserDetailsServiceImpl`
- **Password Encoding**: Configures BCrypt password encoder for secure password storage
- **HTTP Security Configuration**:
    - Disables CSRF for stateless REST APIs
    - Configures exception handling with custom `AuthEntryPointJwt`
    - Sets up stateless session management
    - Configures URL authorization patterns:
        - Public endpoints: `/api/v1/auth/**`, `/api/v1/test/all`, etc.
        - Role-protected endpoints: admin, moderator, and user-specific endpoints
- **JWT Filter**: Adds the JWT authentication filter before the standard security filter

## 2. JWT Implementation

### JwtUtils

This utility class handles the JWT token generation, validation, and parsing:

- **Token Generation**: Creates a new JWT token upon successful authentication with:
    - User identity (username)
    - Issuance and expiration dates
    - Digital signature using a secret key
- **Token Validation**: Validates tokens by:
    - Checking the signature validity
    - Ensuring the token hasn't expired
- **Username Extraction**: Extracts the username from a valid token

Implementation details:

- Uses `io.jsonwebtoken` library for JWT operations
- Configures token expiration time
- Signs tokens with HMAC SHA-256 algorithm
- Provides error handling for malformed or invalid tokens

### AuthTokenFilter

This filter intercepts every HTTP request to:

- Extract the JWT token from the Authorization header
- Validate the token using `JwtUtils`
- Load the user details from `UserDetailsService`
- Set the authenticated user in the Spring Security context

This allows subsequent security checks to recognize the authenticated user without re-authentication.

### AuthEntryPointJwt

This class provides a custom implementation of `AuthenticationEntryPoint` to handle unauthorized access attempts:

- Triggers when an unauthenticated user tries to access a secured resource
- Returns a standardized JSON error response with 401 Unauthorized status
- Logs the authentication failure details

## 3. User Authentication

### UserDetailsServiceImpl

This service implements Spring Security's `UserDetailsService` interface to:

- Load user authentication data from the database
- Convert custom `User` entity to Spring Security's `UserDetails` object
- Handle user not found scenarios with appropriate exceptions

### UserDetailsImpl

This class implements Spring Security's `UserDetails` interface to adapt the user entity to Spring Security
requirements:

- Holds the authentication information (username, password, authorities)
- Provides security-related user state (account expiration, locking, etc.)
- Handles conversion from custom `User` entity
- Includes convenience methods for security checks

## 4. Rate Limiting for Security

### RateLimit Annotation

A custom annotation that enables declarative rate limiting on endpoints:

- Defines the maximum number of allowed requests in a time window
- Specifies the time window duration
- Indicates the type of key to use for rate limiting (IP-based or user-based)
- Can be applied to controller methods to prevent brute force attacks

### RateLimitingAspect

This aspect uses Spring AOP to:

- Intercept calls to methods annotated with `@RateLimit`
- Extract the rate limit parameters from the annotation
- Determine the appropriate key for rate limiting (IP address or username)
- Check if the request should be allowed or rejected based on previous requests
- Set appropriate HTTP headers with rate limit information
- Throw exceptions when rate limits are exceeded

## 5. Security Flow

The complete authentication flow in the application works as follows:

### Authentication Flow Diagram

```
┌─────────┐          ┌──────────────┐          ┌──────────────┐          ┌─────────────┐
│  Client │          │ Auth         │          │ Security     │          │ Database    │
│         │          │ Controller   │          │ Components   │          │             │
└────┬────┘          └──────┬───────┘          └──────┬───────┘          └──────┬──────┘
     │                      │                         │                         │
     │  Login Request       │                         │                         │
     │ ──────────────────►  │                         │                         │
     │                      │                         │                         │
     │                      │ Authentication Request  │                         │
     │                      │ ──────────────────────► │                         │
     │                      │                         │                         │
     │                      │                         │  Load User Details      │
     │                      │                         │ ───────────────────────►│
     │                      │                         │                         │
     │                      │                         │◄─────────────────────── │
     │                      │                         │  User Details           │
     │                      │                         │                         │
     │                      │◄─────────────────────── │                         │
     │                      │  Authentication Result  │                         │
     │                      │                         │                         │
     │                      │ Generate JWT            │                         │
     │                      │─┐                       │                         │
     │                      │ │                       │                         │
     │                      │◄┘                       │                         │
     │                      │                         │                         │
     │◄─────────────────────│                         │                         │
     │  JWT Token           │                         │                         │
     │                      │                         │                         │
     │  Authenticated Request with JWT                │                         │
     │ ───────────────────────────────────────────────►                         │
     │                      │                         │                         │
     │                      │                         │  Validate Token         │
     │                      │                         │─┐                       │
     │                      │                         │ │                       │
     │                      │                         │◄┘                       │
     │                      │                         │                         │
     │                      │                         │  Load User Details      │
     │                      │                         │ ───────────────────────►│
     │                      │                         │                         │
     │                      │                         │◄─────────────────────── │
     │                      │                         │  User Details           │
     │                      │                         │                         │
     │                      │                         │  Set Authentication     │
     │                      │                         │─┐                       │
     │                      │                         │ │                       │
     │                      │                         │◄┘                       │
     │                      │                         │                         │
     │◄───────────────────────────────────────────────│                         │
     │  Protected Resource  │                         │                         │
     │                      │                         │                         │
```

1. **Login Process**:
    - The client sends credentials to `/api/v1/auth/login` endpoint
    - Rate limiting is applied to prevent brute force attacks
    - Spring Security's `AuthenticationManager` authenticates the user using `UserDetailsServiceImpl`
    - On successful authentication, `JwtUtils` generates a JWT token
    - The token is returned to the client

2. **Authenticated Requests**:
    - For subsequent requests, the client includes the JWT in the Authorization header
    - `AuthTokenFilter` intercepts the request and extracts the token
    - The filter validates the token and loads the user details
    - If valid, it sets the authentication in the Spring Security context
    - Rate limiting may be applied based on `@RateLimit` annotations
    - Spring Security checks the user's authorities against the required permissions for the endpoint

3. **Authorization**:
    - URL-based authorization rules in `WebSecurityConfig` control access to endpoints
    - Method-level security is implemented with annotations like `@Secured`, `@PreAuthorize`, etc.
    - Custom expression handlers may be used for complex authorization rules

## 6. Redis Integration for Rate Limiting

The application uses Redis to store rate limiting data:

- **Redis Configuration**: Defined in `RedisConfig.java` with connection settings
- **Rate Limiter Service**: Implements the logic to track and enforce rate limits using Redis as storage
- **Docker Integration**: The `docker-compose.yml` file sets up a Redis container for development

Redis provides several advantages for rate limiting:

- Distributed rate limiting across multiple application instances
- Fast in-memory operations
- TTL (time-to-live) support for automatic cleanup of expired records
- Atomic operations for reliable counting

## 7. Key Security Features and Best Practices

The implementation follows several security best practices:

1. **Stateless Authentication**: Using JWT for stateless, token-based authentication
2. **Password Encoding**: BCrypt for secure password storage
3. **Rate Limiting**: Protection against brute force and DoS attacks
4. **Role-Based Access Control**: Fine-grained permission control
5. **Custom Error Handling**: User-friendly error responses
6. **Token Expiration**: Short-lived tokens with configurable expiration
7. **Secure Headers**: Rate limit information in response headers
8. **Proper Exception Handling**: Centralized exception handling for security events

## 8. Areas for Potential Enhancement

While the implementation is robust, there are some potential enhancements:

1. **Token Refresh Mechanism**: Implementing token refresh for longer sessions
2. **Token Blacklisting**: For immediate token invalidation on logout
3. **Sliding Window Rate Limiting**: More sophisticated rate limiting algorithms
4. **CSRF Protection for Browser Clients**: If the API is accessed from browsers
5. **JWE (Encrypted JWT)**: For additional security of sensitive claims
