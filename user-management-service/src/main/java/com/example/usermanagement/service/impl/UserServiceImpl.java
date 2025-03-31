package com.example.usermanagement.service.impl;

import com.example.usermanagement.exception.CurrentPasswordInvalidException;
import com.example.usermanagement.exception.PasswordMismatchException;
import com.example.usermanagement.exception.ResourceNotFoundException;
import com.example.usermanagement.mapper.UserMapper;
import com.example.usermanagement.model.dto.UserDto;
import com.example.usermanagement.model.entity.Role;
import com.example.usermanagement.model.entity.User;
import com.example.usermanagement.model.enums.ERole;
import com.example.usermanagement.model.request.ChangePasswordRequest;
import com.example.usermanagement.model.request.UpdateUserRequest;
import com.example.usermanagement.repository.RoleRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.repository.VerificationCodeRepository;
import com.example.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final VerificationCodeRepository verificationCodeRepository;
  private final PasswordEncoder encoder;
  private final UserMapper userMapper;

  @Override
  public List<UserDto> getAllUsers() {
    return userRepository.findAll().stream()
            .map(userMapper::userToUserDto)
            .toList();
  }

  @Override
  public UserDto getUserById(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    return userMapper.userToUserDto(user);
  }

  @Override
  public UserDto getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();

    User user = userRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));

    return userMapper.userToUserDto(user);
  }

  @Override
  @Transactional
  public UserDto updateUser(Long id, UpdateUserRequest updateUserRequest) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

    if (updateUserRequest.username() != null) {
      user.setUsername(updateUserRequest.username());
    }

    if (updateUserRequest.email() != null) {
      user.setEmail(updateUserRequest.email());
    }

    if (updateUserRequest.firstName() != null) {
      user.setFirstName(updateUserRequest.firstName());
    }

    if (updateUserRequest.lastName() != null) {
      user.setLastName(updateUserRequest.lastName());
    }

    if (updateUserRequest.enabled() != null) {
      user.setEnabled(updateUserRequest.enabled());
    }

    if (updateUserRequest.roles() != null && !updateUserRequest.roles().isEmpty()) {
      Set<Role> roles = new HashSet<>();
      updateUserRequest.roles().forEach(role -> {
        Role userRole = roleRepository.findByName(ERole.valueOf(role))
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", role));
        roles.add(userRole);
      });
      user.setRoles(roles);
    }

    User updatedUser = userRepository.save(user);
    return userMapper.userToUserDto(updatedUser);
  }

  @Override
  @Transactional
  public void deleteUser(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

    // Delete associated verification codes then delete the user
    verificationCodeRepository.deleteByUserId(user.getId());
    userRepository.delete(user);
  }

  @Override
  @Transactional
  public UserDto changePassword(Long id, ChangePasswordRequest changePasswordRequest) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

    if (!encoder.matches(changePasswordRequest.currentPassword(), user.getPassword())) {
      throw new CurrentPasswordInvalidException();
    }

    if (!changePasswordRequest.newPassword().equals(changePasswordRequest.confirmPassword())) {
      throw new PasswordMismatchException();
    }

    user.setPassword(encoder.encode(changePasswordRequest.newPassword()));
    User updatedUser = userRepository.save(user);

    return userMapper.userToUserDto(updatedUser);
  }
}
