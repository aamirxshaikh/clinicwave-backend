package com.clinicwave.usermanagementservice.service.impl;

import com.clinicwave.usermanagementservice.model.request.EmailRequest;
import com.clinicwave.usermanagementservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
  private final RabbitTemplate rabbitTemplate;

  @Value("${rabbitmq.email-verification-queue}")
  private String emailVerificationQueue;

  @Override
  public void sendVerificationEmail(String to, String code) {
    log.info("Sending verification email to {}", to);
    EmailRequest emailRequest = new EmailRequest(to, code);
    rabbitTemplate.convertAndSend(emailVerificationQueue, emailRequest);
    log.info("Verification email sent to {}", to);
  }
}