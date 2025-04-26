package com.clinicwave.usermanagementservice.security;

import com.clinicwave.usermanagementservice.exception.ResourceNotFoundException;
import com.clinicwave.usermanagementservice.model.entity.User;
import com.clinicwave.usermanagementservice.repository.UserRepository;
import com.clinicwave.usermanagementservice.security.service.UserDetailsImpl;
import com.clinicwave.usermanagementservice.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {
  private final TokenBlacklistService tokenBlacklistService;
  private final JwtUtils jwtUtils;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {
    try {
      String jwt = jwtUtils.parseJwt(request);
      if (jwt != null && jwtUtils.validateJwtToken(jwt)
              && !tokenBlacklistService.isTokenBlacklisted(jwt)) {
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Check if the user is verified
        if (!user.isEmailVerified()) {
          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
          response.getWriter().write("Please verify your email address");
          return;
        }

        UserDetails userDetails = UserDetailsImpl.build(user);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      log.error("Cannot set user authentication: {}", e.getMessage());
    }

    filterChain.doFilter(request, response);
  }
}
