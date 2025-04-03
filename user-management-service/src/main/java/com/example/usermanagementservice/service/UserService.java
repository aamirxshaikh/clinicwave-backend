package com.example.usermanagementservice.service;

import com.example.usermanagementservice.model.dto.UserDto;
import com.example.usermanagementservice.model.request.ChangePasswordRequest;
import com.example.usermanagementservice.model.request.UpdateUserRequest;
import com.example.usermanagementservice.model.request.UserParamsRequest;
import com.example.usermanagementservice.model.response.PageResponse;

public interface UserService {
  PageResponse<UserDto> getAllUsers(UserParamsRequest paramsRequest);

  UserDto getUserById(Long id);

  UserDto getCurrentUser();

  UserDto updateUser(Long id, UpdateUserRequest updateUserRequest);

  void deleteUser(Long id);

  UserDto changePassword(Long id, ChangePasswordRequest changePasswordRequest);
}
