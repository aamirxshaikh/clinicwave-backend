package com.clinicwave.usermanagementservice.service;

import com.clinicwave.usermanagementservice.model.request.LoginRequest;
import com.clinicwave.usermanagementservice.model.request.RegisterRequest;
import com.clinicwave.usermanagementservice.model.response.JwtResponse;
import com.clinicwave.usermanagementservice.model.response.MessageResponse;

public interface AuthService {
  JwtResponse authenticateUser(LoginRequest loginRequest);

  MessageResponse registerUser(RegisterRequest registerRequest);
}
