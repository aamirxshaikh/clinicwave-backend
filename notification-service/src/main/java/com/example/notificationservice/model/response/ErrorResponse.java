package com.example.notificationservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
  @Builder.Default
  private LocalDateTime timestamp = LocalDateTime.now();
  private String status;
  private Integer statusCode;
  private String message;
  @Builder.Default
  private List<String> errors = new ArrayList<>();
  private String path;
}
