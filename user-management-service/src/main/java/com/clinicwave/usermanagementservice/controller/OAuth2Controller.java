package com.clinicwave.usermanagementservice.controller;

import com.clinicwave.usermanagementservice.model.response.JwtResponse;
import com.clinicwave.usermanagementservice.security.JwtUtils;
import com.clinicwave.usermanagementservice.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {
  private final JwtUtils jwtUtils;

  @GetMapping("/success")
  public ResponseEntity<JwtResponse> oauth2Success() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    String jwt = jwtUtils.generateJwtToken(authentication);
    String refreshToken = jwtUtils.generateRefreshToken(userDetails.getUsername());

    List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

    return ResponseEntity.ok(new JwtResponse(
            jwt,
            refreshToken,
            "Bearer",
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail(),
            roles
    ));
  }

  @GetMapping("/failure")
  public ResponseEntity<String> oauth2Failure() {
    return ResponseEntity.badRequest().body("OAuth2 login failed");
  }
}