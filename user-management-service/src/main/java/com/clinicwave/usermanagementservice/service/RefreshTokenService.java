package com.clinicwave.usermanagementservice.service;

import com.clinicwave.usermanagementservice.model.entity.RefreshToken;
import com.clinicwave.usermanagementservice.model.entity.User;

import java.util.Optional;

public interface RefreshTokenService {
  Optional<RefreshToken> findByToken(String token);

  RefreshToken createRefreshToken(User user);

  RefreshToken verifyExpiration(RefreshToken token);

  void deleteByUserId(Long userId);
}
