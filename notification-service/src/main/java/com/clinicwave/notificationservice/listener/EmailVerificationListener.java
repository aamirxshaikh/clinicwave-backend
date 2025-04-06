package com.clinicwave.notificationservice.listener;

import com.clinicwave.notificationservice.model.request.EmailRequest;
import com.clinicwave.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationListener {
  private final EmailService emailService;

  @RabbitListener(queues = "${rabbitmq.email-verification-queue}")
  public void handleEmailVerification(EmailRequest emailRequest) {
    log.info("Received email verification request for {}", emailRequest.to());
    emailService.sendVerificationEmail(emailRequest.to(), emailRequest.code());
    log.info("Processed email verification request for {}", emailRequest.to());
  }
}