package com.clinicwave.usermanagementservice.service;

import com.clinicwave.usermanagementservice.model.dto.UserDto;
import com.clinicwave.usermanagementservice.model.request.ChangePasswordRequest;
import com.clinicwave.usermanagementservice.model.request.UpdateUserRequest;
import com.clinicwave.usermanagementservice.model.request.UserParamsRequest;
import com.clinicwave.usermanagementservice.model.response.PageResponse;

public interface UserService {
  PageResponse<UserDto> getAllUsers(UserParamsRequest paramsRequest);

  UserDto getUserById(Long id);

  UserDto getCurrentUser();

  UserDto updateUser(Long id, UpdateUserRequest updateUserRequest);

  void deleteUser(Long id);

  UserDto changePassword(Long id, ChangePasswordRequest changePasswordRequest);
}
