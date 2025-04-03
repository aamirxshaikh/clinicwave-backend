package com.example.usermanagementservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserParamsRequest {
  // Filter parameters
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private Boolean enabled;
  private Boolean emailVerified;

  // Pagination parameters
  private Integer page = 0;
  private Integer size = 10;

  // Sorting parameters
  private String sortBy = "id";
  private String sortDirection = "asc";
}