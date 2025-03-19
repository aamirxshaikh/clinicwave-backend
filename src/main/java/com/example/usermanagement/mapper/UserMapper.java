package com.example.usermanagement.mapper;

import com.example.usermanagement.model.dto.UserDto;
import com.example.usermanagement.model.entity.Role;
import com.example.usermanagement.model.entity.User;
import java.util.Collections;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
  @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStringSet")
  UserDto userToUserDto(User user);

  @Named("rolesToStringSet")
  default Set<String> rolesToStringSet(Set<Role> roles) {
    if (roles == null) {
      return Collections.emptySet();
    }
    return roles.stream()
            .map(role -> role.getName().name())
            .collect(Collectors.toSet());
  }
}
