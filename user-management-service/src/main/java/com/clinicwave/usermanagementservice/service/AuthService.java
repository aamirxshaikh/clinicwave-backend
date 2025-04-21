package com.clinicwave.usermanagementservice.service;

import com.clinicwave.usermanagementservice.model.request.LoginRequest;
import com.clinicwave.usermanagementservice.model.request.RegisterRequest;
import com.clinicwave.usermanagementservice.model.response.JwtResponse;
import com.clinicwave.usermanagementservice.model.response.MessageResponse;
import com.clinicwave.usermanagementservice.model.response.RefreshTokenResponse;

public interface AuthService {
  JwtResponse authenticateUser(LoginRequest loginRequest);

  MessageResponse registerUser(RegisterRequest registerRequest);

  RefreshTokenResponse refreshToken(String refreshToken);
}
