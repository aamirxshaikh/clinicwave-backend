package com.example.usermanagement.service;

import com.example.usermanagement.model.dto.UserDto;
import com.example.usermanagement.model.request.ChangePasswordRequest;
import com.example.usermanagement.model.request.UpdateUserRequest;
import com.example.usermanagement.model.request.UserParamsRequest;
import com.example.usermanagement.model.response.PageResponse;

public interface UserService {
  PageResponse<UserDto> getAllUsers(UserParamsRequest paramsRequest);

  UserDto getUserById(Long id);

  UserDto getCurrentUser();

  UserDto updateUser(Long id, UpdateUserRequest updateUserRequest);

  void deleteUser(Long id);

  UserDto changePassword(Long id, ChangePasswordRequest changePasswordRequest);
}
