package com.example.usermanagement.service.impl;

import com.example.usermanagement.exception.EmailSendingException;
import com.example.usermanagement.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${spring.mail.username}")
  private String fromEmail;

  @Override
  public void sendVerificationEmail(String to, String code) {
    try {
      Context context = new Context();
      context.setVariable("code", code);

      String content = templateEngine.process("verification-email", context);

      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(fromEmail);
      helper.setTo(to);
      helper.setSubject("Verify your email address");
      helper.setText(content, true);

      mailSender.send(message);
    } catch (MessagingException e) {
      throw new EmailSendingException("Failed to send verification email");
    }
  }
} 