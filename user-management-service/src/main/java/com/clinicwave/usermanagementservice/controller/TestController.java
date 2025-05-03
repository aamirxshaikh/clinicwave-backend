package com.clinicwave.usermanagementservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {
  @GetMapping("/all")
  public String allAccess() {
    return "Public Content.";
  }

  @GetMapping("/user")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public String userAccess() {
    return "User Content.";
  }

  @GetMapping("/mod")
  @PreAuthorize("hasRole('MODERATOR')")
  public String moderatorAccess() {
    return "Moderator Board.";
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminAccess() {
    return "Admin Board.";
  }

  @GetMapping("/oauth")
  public ResponseEntity<String> testOAuth2() {
    return ResponseEntity.ok("""
            To test GitHub OAuth2:
            1. Open this URL in your browser:
               http://localhost:8080/api/v1/oauth2/authorization/github
            2. Login to GitHub if not already logged in
            3. Authorize the application
            4. You will be redirected to /oauth2/success
            5. The response will contain your JWT tokens
            """);
  }
}
