package com.example.usermanagement.security;

import com.example.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SecurityExpressionHandler {
  private final UserService userService;

  public boolean isCurrentUser(Long userId) {
    return Objects.equals(userService.getCurrentUser().id(), userId);
  }
}