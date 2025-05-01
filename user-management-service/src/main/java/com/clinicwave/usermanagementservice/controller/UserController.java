package com.clinicwave.usermanagementservice.controller;

import com.clinicwave.usermanagementservice.annotation.RateLimit;
import com.clinicwave.usermanagementservice.model.dto.UserDto;
import com.clinicwave.usermanagementservice.model.request.ChangePasswordRequest;
import com.clinicwave.usermanagementservice.model.request.UpdateUserRequest;
import com.clinicwave.usermanagementservice.model.request.UserParamsRequest;
import com.clinicwave.usermanagementservice.model.response.MessageResponse;
import com.clinicwave.usermanagementservice.model.response.PageResponse;
import com.clinicwave.usermanagementservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping
  public ResponseEntity<PageResponse<UserDto>> getAllUsers(UserParamsRequest paramsRequest) {
    return ResponseEntity.ok(userService.getAllUsers(paramsRequest));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @securityExpressionHandler.isCurrentUser(#id)")
  public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @GetMapping("/me")
  public ResponseEntity<UserDto> getCurrentUser() {
    return ResponseEntity.ok(userService.getCurrentUser());
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @securityExpressionHandler.isCurrentUser(#id)")
  @RateLimit(duration = 10, prefix = "rate:update", useAuthenticatedUser = true)
  public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
    return ResponseEntity.ok(userService.updateUser(id, updateUserRequest));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
  }

  @PostMapping("/{id}/change-password")
  @PreAuthorize("hasRole('ADMIN') or @securityExpressionHandler.isCurrentUser(#id)")
  @RateLimit(limit = 3, duration = 10, prefix = "rate:change-password", useAuthenticatedUser = true)
  public ResponseEntity<UserDto> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
    return ResponseEntity.ok(userService.changePassword(id, changePasswordRequest));
  }
}
