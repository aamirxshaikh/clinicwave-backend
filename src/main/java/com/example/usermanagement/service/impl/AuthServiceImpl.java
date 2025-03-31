package com.example.usermanagement.service.impl;

import com.example.usermanagement.exception.InvalidRoleException;
import com.example.usermanagement.model.response.JwtResponse;
import com.example.usermanagement.model.request.LoginRequest;
import com.example.usermanagement.model.response.MessageResponse;
import com.example.usermanagement.model.request.RegisterRequest;
import com.example.usermanagement.model.enums.ERole;
import com.example.usermanagement.model.entity.Role;
import com.example.usermanagement.model.entity.User;
import com.example.usermanagement.repository.RoleRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.security.JwtUtils;
import com.example.usermanagement.security.service.UserDetailsImpl;
import com.example.usermanagement.service.AuthService;
import com.example.usermanagement.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
public class AuthServiceImpl implements AuthService {
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final EmailVerificationService emailVerificationService;
  private final PasswordEncoder encoder;
  private final JwtUtils jwtUtils;

  @Override
  public JwtResponse authenticateUser(LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

    return new JwtResponse(
            jwt,
            "Bearer",
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail(),
            roles
    );
  }

  @Override
  @Transactional
  public MessageResponse registerUser(RegisterRequest registerRequest) {
    // Check if username is already taken
    if (Boolean.TRUE.equals(userRepository.existsByUsername(registerRequest.username()))) {
      return new MessageResponse("Error: Username is already taken!");
    }

    // Check if email is already in use
    if (Boolean.TRUE.equals(userRepository.existsByEmail(registerRequest.email()))) {
      return new MessageResponse("Error: Email is already in use!");
    }

    // Create new user's account
    User user = User.builder()
            .username(registerRequest.username())
            .email(registerRequest.email())
            .password(encoder.encode(registerRequest.password()))
            .firstName(registerRequest.firstName())
            .lastName(registerRequest.lastName())
            .enabled(true)
            .emailVerified(false)
            .build();

    Set<String> strRoles = registerRequest.roles();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null || strRoles.isEmpty()) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        try {
          Role userRole = roleRepository.findByName(ERole.valueOf(role))
                  .orElseThrow(() -> new RuntimeException("Error: Role " + role + " is not found."));
          roles.add(userRole);
        } catch (IllegalArgumentException e) {
          log.error("Invalid role name: {}", role);
          throw new InvalidRoleException(role);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    // Send verification email
    emailVerificationService.sendVerificationEmail(user.getEmail());

    return new MessageResponse("User registered successfully! Please check your email to verify your account.");
  }
}
