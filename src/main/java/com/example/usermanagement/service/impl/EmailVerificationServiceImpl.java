package com.example.usermanagement.service.impl;

import com.example.usermanagement.exception.*;
import com.example.usermanagement.model.entity.User;
import com.example.usermanagement.model.entity.VerificationCode;
import com.example.usermanagement.model.enums.EVerificationType;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.repository.VerificationCodeRepository;
import com.example.usermanagement.service.EmailService;
import com.example.usermanagement.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;

    @Override
    public void sendVerificationEmail(String email) {
        User user = findUserByEmail(email);
        createAndSendVerificationCode(user);
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = findUserByEmail(email);

        if (user.isEmailVerified()) {
            throw new EmailAlreadyVerifiedException(user.getEmail());
        }

        // Delete all unused email verification codes before sending a new one
        verificationCodeRepository.deleteAllUnusedCodes(user.getId(), EVerificationType.EMAIL_VERIFICATION);

        createAndSendVerificationCode(user);
    }

    @Override
    @Transactional
    public void verifyEmail(String email, String code) {
        User user = findUserByEmail(email);

        VerificationCode verificationCode = verificationCodeRepository
                .findByCodeAndType(code, EVerificationType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new InvalidVerificationCodeException("Invalid verification code"));

        if (verificationCode.isUsed()) {
            throw new VerificationCodeAlreadyUsedException("This verification code has already been used");
        }

        if (verificationCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new VerificationCodeExpiredException("This verification code has expired");
        }

        if (!verificationCode.getUser().getId().equals(user.getId())) {
            throw new InvalidVerificationCodeException("Verification code does not match the user");
        }

        // set verification code as used
        verificationCodeRepository.markAsUsed(verificationCode.getId());

        user.setEmailVerified(true);
        userRepository.save(user);
    }

    private void createAndSendVerificationCode(User user) {
        String code = generateNumericCode();
        VerificationCode verificationCode = VerificationCode.builder()
                .code(code)
                .user(user)
                .type(EVerificationType.EMAIL_VERIFICATION)
                .expiryDate(LocalDateTime.now().plusMinutes(10)) // OTP valid for 10 minutes
                .used(false)
                .build();

        verificationCodeRepository.save(verificationCode);
        emailService.sendVerificationEmail(user.getEmail(), code);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private String generateNumericCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generates a 6-digit numeric code
        return String.valueOf(code);
    }
} 