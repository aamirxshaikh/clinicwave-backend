package com.example.usermanagement.service;

import com.example.usermanagement.model.dto.UserDto;
import com.example.usermanagement.model.request.ChangePasswordRequest;
import com.example.usermanagement.model.request.UpdateUserRequest;

import java.util.List;

public interface UserService {
  List<UserDto> getAllUsers();

  UserDto getUserById(Long id);

  UserDto getCurrentUser();

  UserDto updateUser(Long id, UpdateUserRequest updateUserRequest);

  void deleteUser(Long id);

  UserDto changePassword(Long id, ChangePasswordRequest changePasswordRequest);
}
