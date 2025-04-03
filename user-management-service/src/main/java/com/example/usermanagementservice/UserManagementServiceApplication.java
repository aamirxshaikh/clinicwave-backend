package com.example.usermanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class UserManagementServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(UserManagementServiceApplication.class, args);
  }
}
