# Sequential Flow Analysis of Spring Security & JWT

This document provides a detailed walkthrough of the request flow for different authentication and authorization
scenarios in our Spring Boot Auth API. It explains which classes are involved at each step and their purpose in the
security architecture.

## Table of Contents

1. [Scenario 1: Valid User Login Flow](#scenario-1-valid-user-login-flow)
2. [Scenario 2: Invalid Login Attempt](#scenario-2-invalid-login-attempt)
3. [Scenario 3: Admin User Accessing Protected API](#scenario-3-admin-user-accessing-protected-api)
4. [Scenario 4: Non-Admin User Attempting to Access Admin-Only API](#scenario-4-non-admin-user-attempting-to-access-admin-only-api)
5. [Key Classes and Their Functions](#key-classes-and-their-functions)
6. [Understanding the Sequential Logic](#understanding-the-sequential-logic)

## Scenario 1: Valid User Login Flow

When a user attempts to log in with valid credentials, here's the sequential flow:

1. **Client Request**
    - The client sends a POST request to `/api/v1/auth/login` with username and password

2. **AuthController**
    - The request first hits the `AuthController` class
    - This controller handles authentication-related endpoints including login
    - It receives the login request with credentials

3. **@RateLimit Annotation (if applied)**
    - If the login endpoint has the `@RateLimit` annotation, it triggers
    - The rate limiting mechanism checks if the IP/user has exceeded the allowed request rate

4. **RateLimitingAspect**
    - If rate limiting is applied, this aspect intercepts the request before processing
    - It checks against Redis to see if the client should be allowed to proceed
    - If within limits, the request continues; otherwise, a `RateLimitExceededException` is thrown

5. **AuthenticationManager**
    - The controller creates an `Authentication` token with the provided credentials
    - It passes this token to Spring's `AuthenticationManager`
    - The `AuthenticationManager` is configured in `WebSecurityConfig`

6. **UserDetailsServiceImpl**
    - The `AuthenticationManager` delegates to `UserDetailsServiceImpl`
    - This service implements Spring Security's `UserDetailsService` interface
    - It has a `loadUserByUsername` method that retrieves the user from the database

7. **UserRepository**
    - The `UserDetailsServiceImpl` uses `UserRepository` to query the database
    - It searches for a user with the provided username

8. **UserDetailsImpl**
    - Once the user is found, `UserDetailsServiceImpl` converts it to a `UserDetailsImpl` object
    - `UserDetailsImpl` implements Spring Security's `UserDetails` interface
    - It contains the user's credentials and authorities (roles)

9. **PasswordEncoder**
    - The `AuthenticationManager` uses `PasswordEncoder` (BCrypt)
    - It verifies that the provided password matches the encoded password in the database

10. **JwtUtils**
    - After successful authentication, control returns to the `AuthController`
    - The controller calls `JwtUtils.generateJwtToken()` to create a new JWT
    - `JwtUtils` creates a token containing the username and expiration time
    - It signs the token with the secret key using HMAC SHA-256

11. **Response**
    - The `AuthController` creates a response containing:
        - The JWT token
        - User information (id, username, email, roles)
    - This response is sent back to the client
    - The client will store this token for future requests

## Scenario 2: Invalid Login Attempt

When a user attempts to log in with invalid credentials:

1. **Client Request**
    - The client sends a POST request to `/api/v1/auth/login` with incorrect username or password

2. **AuthController**
    - The request is received by the `AuthController`

3. **@RateLimit Annotation (if applied)**
    - If rate limiting is applied, the system checks whether to allow another attempt
    - This helps prevent brute force attacks

4. **RateLimitingAspect**
    - If within rate limits, the request proceeds
    - Each failed attempt is counted toward the rate limit

5. **AuthenticationManager**
    - It receives the authentication request with invalid credentials

6. **UserDetailsServiceImpl**
    - If the username doesn't exist, this service throws a `UsernameNotFoundException`
    - This exception is caught by Spring Security's authentication mechanism

7. **PasswordEncoder**
    - If the username exists but the password is wrong, the `PasswordEncoder` determines the mismatch

8. **Authentication Exception**
    - Spring Security generates a `BadCredentialsException`
    - This is caught by the authentication flow

9. **Global Exception Handler**
    - The application's `GlobalExceptionHandler` intercepts the exception
    - It formats an appropriate error response

10. **Response**
    - A 401 Unauthorized response is returned
    - The response contains an error message like "Invalid username or password"
    - The rate limiting headers may indicate remaining attempts

## Scenario 3: Admin User Accessing Protected API

After a successful login as an admin user, when accessing a protected admin-only endpoint:

1. **Client Request**
    - The client makes a request to a protected endpoint (e.g., `/api/v1/users`)
    - The request includes the JWT token in the Authorization header:
      ```
      Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
      ```

2. **AuthTokenFilter**
    - This filter intercepts all HTTP requests
    - It extracts the JWT token from the Authorization header
    - The filter is registered in the security filter chain in `WebSecurityConfig`

3. **JwtUtils**
    - `AuthTokenFilter` calls `JwtUtils.validateJwtToken()` to verify the token
    - It checks:
        - The token signature is valid (using the secret key)
        - The token hasn't expired
    - If valid, it extracts the username from the token

4. **UserDetailsServiceImpl**
    - `AuthTokenFilter` uses this service to load the user details based on the username from the token
    - It retrieves the full user object with roles from the database

5. **UserDetailsImpl**
    - The user details, including authorities (roles), are loaded into a `UserDetailsImpl` object

6. **SecurityContext**
    - `AuthTokenFilter` creates a `UsernamePasswordAuthenticationToken` with:
        - The `UserDetailsImpl` object
        - The user's authorities
    - It sets this authentication in the `SecurityContextHolder`
    - This makes the authenticated user available throughout the request

7. **Spring Security Authorization Check**
    - The request continues to the URL security patterns configured in `WebSecurityConfig`
    - Spring Security checks if the user's roles include `ROLE_ADMIN`
    - Since our user is an admin, this check passes

8. **UserController**
    - The request reaches the `UserController`
    - If method-level security is also used (like `@PreAuthorize("hasRole('ADMIN')")`), another check occurs
    - The controller processes the request normally

9. **Response**
    - The controller returns the requested data
    - Since authentication and authorization were successful, a 200 OK response is returned

## Scenario 4: Non-Admin User Attempting to Access Admin-Only API

When a regular user tries to access an admin-only protected endpoint:

1. **Client Request**
    - A regular user (with `ROLE_USER` but not `ROLE_ADMIN`) makes a request to an admin-only endpoint
    - The JWT token is included in the header

2. **AuthTokenFilter**
    - The filter extracts and validates the JWT token as described earlier
    - The token is valid since it's a legitimate user token

3. **JwtUtils**
    - Validates the token and extracts the username

4. **UserDetailsServiceImpl**
    - Loads the user details, including the `ROLE_USER` authority (but not `ROLE_ADMIN`)

5. **SecurityContext**
    - The authentication with `ROLE_USER` authority is set in the security context

6. **Spring Security Authorization Check**
    - Spring Security checks URL patterns in `WebSecurityConfig`
    - The request is for a URL restricted to `ROLE_ADMIN` users
    - The current user only has `ROLE_USER`, so authorization fails

7. **AccessDeniedException**
    - Spring Security throws an `AccessDeniedException`

8. **CustomAccessDeniedHandler** (if configured)
    - If you have a custom `AccessDeniedHandler`, it processes the exception
    - Otherwise, Spring Security's default handler is used

9. **Response**
    - A 403 Forbidden response is returned
    - The response indicates the user lacks permission to access the resource

## Key Classes and Their Functions

### Authentication Flow Classes

1. **AuthController**
    - Purpose: Handles authentication endpoints (login, register)
    - Location: `controller.com.clinicwave.usermanagementservice.AuthController`
    - Key methods: `authenticateUser()`, `registerUser()`

2. **WebSecurityConfig**
    - Purpose: Central security configuration for the application
    - Location: `security.com.clinicwave.usermanagementservice.WebSecurityConfig`
    - Key functions:
        - Configures authentication manager
        - Sets up security filter chain
        - Defines URL access rules
        - Registers JWT filter

3. **JwtUtils**
    - Purpose: Manages JWT token operations
    - Location: `security.com.clinicwave.usermanagementservice.JwtUtils`
    - Key methods:
        - `generateJwtToken()`: Creates new tokens after successful authentication
        - `validateJwtToken()`: Verifies token integrity and expiration
        - `getUserNameFromJwtToken()`: Extracts the subject claim

4. **AuthTokenFilter**
    - Purpose: Intercepts HTTP requests to validate JWT tokens
    - Location: `security.com.clinicwave.usermanagementservice.AuthTokenFilter`
    - Key method: `doFilterInternal()`: Processes each HTTP request to extract/validate JWT

5. **UserDetailsServiceImpl**
    - Purpose: Loads user data from the database for authentication
    - Location: `service.security.com.clinicwave.usermanagementservice.UserDetailsServiceImpl`
    - Key method: `loadUserByUsername()`: Fetches user data for authentication

6. **UserDetailsImpl**
    - Purpose: Adapts the user model to Spring Security's user representation
    - Location: `service.security.com.clinicwave.usermanagementservice.UserDetailsImpl`
    - Key methods: `getAuthorities()`, `getPassword()`, `getUsername()`

### Rate Limiting Classes

1. **RateLimit**
    - Purpose: Annotation to specify rate limiting parameters
    - Location: `annotation.com.clinicwave.usermanagementservice.RateLimit`
    - Key attributes: `limit`, `duration`, `keyType`

2. **RateLimitingAspect**
    - Purpose: Implements rate limiting logic using AOP
    - Location: `aop.com.clinicwave.usermanagementservice.RateLimitingAspect`
    - Key method: `enforceRateLimit()`: Intercepts annotated methods to apply rate limiting

3. **RateLimiterService**
    - Purpose: Tracks request counts and enforces limits
    - Location: `service.com.clinicwave.usermanagementservice.RateLimiterService`
    - Key methods: `checkRateLimit()`, `incrementCounter()`

### Error Handling Classes

1. **AuthEntryPointJwt**
    - Purpose: Handles unauthorized authentication attempts
    - Location: `security.com.clinicwave.usermanagementservice.AuthEntryPointJwt`
    - Key method: `commence()`: Triggered when authentication fails

2. **GlobalExceptionHandler**
    - Purpose: Centralizes exception handling
    - Location: `exception.com.clinicwave.usermanagementservice.GlobalExceptionHandler`
    - Key methods: Error handlers for various exceptions

## Understanding the Sequential Logic

The beauty of Spring Security lies in its chain of responsibility pattern:

1. Each request passes through a series of filters
2. Each filter has a specific responsibility
3. Filters can terminate the chain early (e.g., rate limiting exceeded or invalid token)
4. Security context is built progressively as the request passes through filters
5. Spring Security makes authorization decisions based on the final security context

This sequential approach enables the separation of concerns:

- Authentication (verifying identity)
- Authorization (checking permissions)
- Rate limiting (preventing abuse)
- Token management (issuing, validating)

Each component focuses on its specific responsibility, making the security architecture modular and maintainable.
