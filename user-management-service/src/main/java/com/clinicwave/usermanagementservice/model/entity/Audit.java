package com.clinicwave.usermanagementservice.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Audit {
  @CreatedDate
  @Column(updatable = false, nullable = false)
  @JsonIgnore
  private LocalDateTime createdAt;

  @LastModifiedDate
  @JsonIgnore
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @CreatedBy
  @JsonIgnore
  @Column(updatable = false, nullable = false)
  private String createdBy;

  @LastModifiedBy
  @JsonIgnore
  @Column(nullable = false)
  private String updatedBy;
}
