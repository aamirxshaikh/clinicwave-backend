package com.clinicwave.usermanagementservice.service;

import com.clinicwave.usermanagementservice.exception.ResourceNotFoundException;
import com.clinicwave.usermanagementservice.mapper.UserMapper;
import com.clinicwave.usermanagementservice.model.dto.UserDto;
import com.clinicwave.usermanagementservice.model.entity.User;
import com.clinicwave.usermanagementservice.repository.RoleRepository;
import com.clinicwave.usermanagementservice.repository.UserRepository;
import com.clinicwave.usermanagementservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock
  private UserRepository userRepository;

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserServiceImpl userService;

  private User user1;
  private User user2;
  private UserDto userDto1;
  private UserDto userDto2;

  @BeforeEach
  void setUp() {
    // Setup test data
    user1 = User.builder()
            .id(1L)
            .username("user1")
            .email("user1@example.com")
            .password("password")
            .firstName("John")
            .lastName("Doe")
            .enabled(true)
            .roles(new HashSet<>())
            .build();

    user2 = User.builder()
            .id(2L)
            .username("user2")
            .email("user2@example.com")
            .password("password")
            .firstName("Jane")
            .lastName("Smith")
            .enabled(true)
            .roles(new HashSet<>())
            .build();

    userDto1 = new UserDto(1L, "user1", "user1@example.com", "John", "Doe", new HashSet<>(), true, null, null);
    userDto2 = new UserDto(2L, "user2", "user2@example.com", "Jane", "Smith", new HashSet<>(), true, null, null);
  }

  @Test
  void whenGetAllUsers_thenReturnUserList() {
    // Arrange
    when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
    when(userMapper.userToUserDto(user1)).thenReturn(userDto1);
    when(userMapper.userToUserDto(user2)).thenReturn(userDto2);

    // Act
    List<UserDto> result = userService.getAllUsers();

    // Assert
    assertEquals(2, result.size());
    verify(userRepository, times(1)).findAll();
    verify(userMapper, times(2)).userToUserDto(any(User.class));
  }

  @Test
  void whenGetUserById_withValidId_thenReturnUser() {
    // Arrange
    when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
    when(userMapper.userToUserDto(user1)).thenReturn(userDto1);

    // Act
    UserDto result = userService.getUserById(1L);

    // Assert
    assertNotNull(result);
    assertEquals(userDto1.id(), result.id());
    assertEquals(userDto1.username(), result.username());
    verify(userRepository, times(1)).findById(1L);
    verify(userMapper, times(1)).userToUserDto(user1);
  }

  @Test
  void whenGetUserById_withInvalidId_thenThrowException() {
    // Arrange
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    verify(userRepository, times(1)).findById(99L);
    verify(userMapper, never()).userToUserDto(any(User.class));
  }
}
