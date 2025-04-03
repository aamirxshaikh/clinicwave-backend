package com.example.usermanagementservice.repository;

import com.example.usermanagementservice.model.entity.VerificationCode;
import com.example.usermanagementservice.model.enums.EVerificationType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
  Optional<VerificationCode> findByCodeAndType(String code, EVerificationType type);

  @Modifying
  @Query("UPDATE VerificationCode v SET v.used = true WHERE v.id = ?1")
  void markAsUsed(Long id);

  @Modifying
  @Query("DELETE FROM VerificationCode v WHERE v.expiryDate < ?1")
  void deleteExpiredCodes(LocalDateTime expiryDate);

  @Modifying
  @Query("DELETE FROM VerificationCode v WHERE v.user.id = :userId")
  void deleteByUserId(@Param("userId") Long userId);

  @Modifying
  @Query("DELETE FROM VerificationCode vc WHERE vc.user.id = :userId AND vc.type = :type AND vc.used = false")
  void deleteAllUnusedCodes(@Param("userId") Long userId, @Param("type") EVerificationType type);
}