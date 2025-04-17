package com.clinicwave.usermanagementservice.service.impl;

import com.clinicwave.usermanagementservice.exception.RefreshTokenException;
import com.clinicwave.usermanagementservice.exception.ResourceNotFoundException;
import com.clinicwave.usermanagementservice.model.entity.RefreshToken;
import com.clinicwave.usermanagementservice.model.entity.User;
import com.clinicwave.usermanagementservice.repository.RefreshTokenRepository;
import com.clinicwave.usermanagementservice.repository.UserRepository;
import com.clinicwave.usermanagementservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
  @Value("${app.jwt.refresh-expiration}")
  private Long refreshTokenDurationMs;

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  @Override
  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  @Override
  @Transactional
  public RefreshToken createRefreshToken(User user) {
    // Check if user already has a refresh token and delete it
    refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);

    // Create new refresh token
    RefreshToken refreshToken = RefreshToken.builder()
            .user(user)
            .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
            .token(UUID.randomUUID().toString())
            .build();

    return refreshTokenRepository.save(refreshToken);
  }

  @Override
  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      throw new RefreshTokenException(token.getToken(), "was expired. Please make a new login request");
    }
    return token;
  }

  @Override
  @Transactional
  public void deleteByUserId(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));
    refreshTokenRepository.deleteByUser(user);
  }
}