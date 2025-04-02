package com.example.usermanagement.specification;

import com.example.usermanagement.model.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
  private UserSpecification() {
  }

  public static Specification<User> createSpecification(String username, String email, String firstName,
                                                        String lastName, Boolean enabled, Boolean emailVerified) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (StringUtils.hasText(username)) {
        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("username")),
                "%" + username.toLowerCase() + "%"));
      }

      if (StringUtils.hasText(email)) {
        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                "%" + email.toLowerCase() + "%"));
      }

      if (StringUtils.hasText(firstName)) {
        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")),
                "%" + firstName.toLowerCase() + "%"));
      }

      if (StringUtils.hasText(lastName)) {
        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")),
                "%" + lastName.toLowerCase() + "%"));
      }

      if (enabled != null) {
        predicates.add(criteriaBuilder.equal(root.get("enabled"), enabled));
      }

      if (emailVerified != null) {
        predicates.add(criteriaBuilder.equal(root.get("emailVerified"), emailVerified));
      }

      return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
    };
  }
}