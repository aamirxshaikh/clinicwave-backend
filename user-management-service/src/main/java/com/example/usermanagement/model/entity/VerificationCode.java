package com.example.usermanagement.model.entity;

import com.example.usermanagement.model.enums.EVerificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_codes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCode {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String code;

  @Column(nullable = false)
  private LocalDateTime expiryDate;

  @Column(nullable = false)
  private boolean used = false;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EVerificationType type;

  @Column(updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private User user;

  @PrePersist
  private void onCreate() {
    this.createdAt = LocalDateTime.now();
  }
} 