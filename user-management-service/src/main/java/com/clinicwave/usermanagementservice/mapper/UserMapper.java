package com.clinicwave.usermanagementservice.mapper;

import com.clinicwave.usermanagementservice.model.dto.UserDto;
import com.clinicwave.usermanagementservice.model.entity.Role;
import com.clinicwave.usermanagementservice.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
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
