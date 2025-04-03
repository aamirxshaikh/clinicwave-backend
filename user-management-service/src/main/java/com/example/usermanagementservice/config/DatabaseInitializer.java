package com.example.usermanagementservice.config;

import com.example.usermanagementservice.model.entity.Role;
import com.example.usermanagementservice.model.entity.User;
import com.example.usermanagementservice.model.enums.ERole;
import com.example.usermanagementservice.repository.RoleRepository;
import com.example.usermanagementservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    initRoles();
    createAdminUserIfNotExists();
  }

  private void initRoles() {
    if (roleRepository.count() == 0) {
      log.info("Initializing roles...");

      for (ERole role : ERole.values()) {
        Role roleEntity = new Role(role);
        roleRepository.save(roleEntity);
      }

      log.info("Roles initialized successfully!");
    }
  }

  private void createAdminUserIfNotExists() {
    if (Boolean.FALSE.equals(userRepository.existsByUsername("admin"))) {
      log.info("Creating admin user...");

      Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Admin Role not found."));

      Set<Role> roles = new HashSet<>();
      roles.add(adminRole);

      User admin = User.builder()
              .username("admin")
              .email("admin@example.com")
              .password(passwordEncoder.encode("admin123"))
              .firstName("Admin")
              .lastName("User")
              .enabled(true)
              .roles(roles)
              .emailVerified(true)
              .build();

      userRepository.save(admin);
      log.info("Admin user created successfully!");
    }
  }
}
