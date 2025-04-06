package com.clinicwave.usermanagementservice.repository;

import com.clinicwave.usermanagementservice.model.entity.Role;
import com.clinicwave.usermanagementservice.model.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
  Optional<Role> findByName(ERole name);
}
