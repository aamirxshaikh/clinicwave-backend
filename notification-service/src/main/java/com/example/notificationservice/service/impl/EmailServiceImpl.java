package com.example.notificationservice.service.impl;

import com.example.notificationservice.exception.EmailSendingException;
import com.example.notificationservice.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${spring.mail.username}")
  private String fromEmail;

  @Override
  public void sendVerificationEmail(String to, String code) {
    log.info("Sending verification email to {}", to);
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
      log.info("Verification email sent to {}", to);
    } catch (MessagingException e) {
      log.error("Failed to send verification email to {}", to, e);
      throw new EmailSendingException("Failed to send verification email");
    }
  }
}