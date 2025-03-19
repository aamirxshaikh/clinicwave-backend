package com.example.usermanagement.service;

import com.example.usermanagement.model.request.LoginRequest;
import com.example.usermanagement.model.request.RegisterRequest;
import com.example.usermanagement.model.response.JwtResponse;
import com.example.usermanagement.model.response.MessageResponse;

public interface AuthService {
  JwtResponse authenticateUser(LoginRequest loginRequest);

  MessageResponse registerUser(RegisterRequest registerRequest);
}
