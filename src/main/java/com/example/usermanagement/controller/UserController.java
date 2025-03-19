package com.example.usermanagement.controller;

import com.example.usermanagement.model.dto.UserDto;
import com.example.usermanagement.model.request.ChangePasswordRequest;
import com.example.usermanagement.model.request.UpdateUserRequest;
import com.example.usermanagement.model.response.MessageResponse;
import com.example.usermanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<UserDto>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
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
  public ResponseEntity<UserDto> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
    return ResponseEntity.ok(userService.changePassword(id, changePasswordRequest));
  }
}
