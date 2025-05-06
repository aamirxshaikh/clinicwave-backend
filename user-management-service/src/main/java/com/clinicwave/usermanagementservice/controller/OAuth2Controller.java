package com.clinicwave.usermanagementservice.controller;

import com.clinicwave.usermanagementservice.model.response.JwtResponse;
import com.clinicwave.usermanagementservice.security.JwtUtils;
import com.clinicwave.usermanagementservice.security.service.UserDetailsImpl;
import com.clinicwave.usermanagementservice.security.service.UserDetailsServiceImpl;
import com.clinicwave.usermanagementservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/oauth2")
@RequiredArgsConstructor
@Slf4j
public class OAuth2Controller {
  private final JwtUtils jwtUtils;
  private final UserDetailsServiceImpl userDetailsServiceImpl;
  private final RefreshTokenService refreshTokenService;

  @GetMapping("/callback")
  public ResponseEntity<JwtResponse> oAuth2Callback() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    log.info("OAuth2 authentication: {}", authentication);

    if (!(authentication.getPrincipal() instanceof OAuth2User oAuth2User)) {
      throw new RuntimeException("Expected OAuth2User but got: " +
              authentication.getPrincipal().getClass().getName());
    }

    // Extract username from OAuth2User
    String username = oAuth2User.getAttribute("login");

    // Load user details using UserDetailsService
    UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsServiceImpl.loadUserByUsername(username);

    // Generate and store tokens
    String jwt = jwtUtils.generateTokenFromUsername(username);
    String refreshToken = jwtUtils.generateRefreshToken(username);

    refreshTokenService.storeRefreshToken(username, refreshToken, jwtUtils.getRefreshExpirationMs());

    List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

    return ResponseEntity.ok(new JwtResponse(
            jwt,
            refreshToken,
            "Bearer",
            userDetails.getId(),
            username,
            userDetails.getEmail(),
            roles
    ));
  }

  @GetMapping("/failure")
  public ResponseEntity<String> oauth2Failure() {
    return ResponseEntity.badRequest().body("OAuth2 login failed");
  }
}