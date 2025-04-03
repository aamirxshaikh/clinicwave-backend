package com.example.usermanagementservice.service;

import com.example.usermanagementservice.model.request.LoginRequest;
import com.example.usermanagementservice.model.request.RegisterRequest;
import com.example.usermanagementservice.model.response.JwtResponse;
import com.example.usermanagementservice.model.response.MessageResponse;

public interface AuthService {
  JwtResponse authenticateUser(LoginRequest loginRequest);

  MessageResponse registerUser(RegisterRequest registerRequest);
}
