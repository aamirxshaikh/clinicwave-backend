package com.clinicwave.usermanagementservice.service.impl;

import com.clinicwave.usermanagementservice.exception.CurrentPasswordInvalidException;
import com.clinicwave.usermanagementservice.exception.PasswordMismatchException;
import com.clinicwave.usermanagementservice.exception.ResourceNotFoundException;
import com.clinicwave.usermanagementservice.mapper.UserMapper;
import com.clinicwave.usermanagementservice.model.dto.UserDto;
import com.clinicwave.usermanagementservice.model.entity.Role;
import com.clinicwave.usermanagementservice.model.entity.User;
import com.clinicwave.usermanagementservice.model.enums.ERole;
import com.clinicwave.usermanagementservice.model.request.ChangePasswordRequest;
import com.clinicwave.usermanagementservice.model.request.UpdateUserRequest;
import com.clinicwave.usermanagementservice.model.request.UserParamsRequest;
import com.clinicwave.usermanagementservice.model.response.PageResponse;
import com.clinicwave.usermanagementservice.repository.RoleRepository;
import com.clinicwave.usermanagementservice.repository.UserRepository;
import com.clinicwave.usermanagementservice.repository.VerificationCodeRepository;
import com.clinicwave.usermanagementservice.service.UserService;
import com.clinicwave.usermanagementservice.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
  public PageResponse<UserDto> getAllUsers(UserParamsRequest paramsRequest) {
    // Create specification for filtering
    Specification<User> specification = UserSpecification.createSpecification(
            paramsRequest.getUsername(),
            paramsRequest.getEmail(),
            paramsRequest.getFirstName(),
            paramsRequest.getLastName(),
            paramsRequest.getEnabled(),
            paramsRequest.getEmailVerified()
    );

    // Create sort object
    Sort sort = Sort.by(
            paramsRequest.getSortDirection().equalsIgnoreCase("desc") ?
                    Sort.Direction.DESC : Sort.Direction.ASC,
            paramsRequest.getSortBy()
    );

    // Create pageable object
    PageRequest pageRequest = PageRequest.of(
            paramsRequest.getPage(),
            paramsRequest.getSize(),
            sort
    );

    // Execute the query
    Page<User> userPage = userRepository.findAll(specification, pageRequest);

    // Map to DTOs and create response
    List<UserDto> userDtoList = userPage.getContent().stream()
            .map(userMapper::userToUserDto)
            .toList();

    return PageResponse.<UserDto>builder()
            .content(userDtoList)
            .pageNumber(userPage.getNumber())
            .pageSize(userPage.getSize())
            .totalElements(userPage.getTotalElements())
            .totalPages(userPage.getTotalPages())
            .last(userPage.isLast())
            .first(userPage.isFirst())
            .build();
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
