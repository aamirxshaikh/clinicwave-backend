package com.clinicwave.usermanagementservice.config;

import com.clinicwave.usermanagementservice.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfig {
  private final VerificationCodeRepository verificationCodeRepository;

  @Scheduled(cron = "0 0 * * * *") // Run every hour
  @Transactional
  public void cleanupExpiredVerificationCodes() {
    verificationCodeRepository.deleteExpiredCodes(LocalDateTime.now());
  }
} 