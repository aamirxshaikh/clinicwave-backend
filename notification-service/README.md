# Notification Service

A Spring Boot microservice for handling notifications in the Clinicwave platform, supporting email notifications and message queuing.

## Features

- Email Notifications
- RabbitMQ Message Queue Integration
- Template-based Email Content
- Asynchronous Notification Processing
- Exception Handling
- Message Queue Listener

## Technology Stack

- Java 21
- Spring Boot 3.4.4
- Spring AMQP (RabbitMQ)
- Spring Mail
- Thymeleaf (Email Templates)
- Lombok
- Maven

## Components

### Message Queue Listener
- Listens to RabbitMQ queues for notification events
- Processes notifications asynchronously
- Handles different types of notifications

### Email Service
- Sends email notifications using Spring Mail
- Supports HTML email templates using Thymeleaf
- Configurable email properties

### Configuration
- RabbitMQ connection settings
- Email server configuration
- Template configuration

## Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Configure the following properties in `application.properties`:
   - RabbitMQ connection details
   - SMTP server settings
   - Email templates path
4. Run the application using Maven:
   ```
   mvn spring-boot:run
   ```

## Configuration

### RabbitMQ Configuration
```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

### Email Configuration
```properties
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=your-email@example.com
spring.mail.password=your-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## Message Queue Structure

The service listens to the following queues:
- `notification.queue` - Main queue for notification events
- `dead-letter.queue` - Queue for failed notification attempts

## Email Templates

Email templates are stored in `src/main/resources/templates/email/` and include:
- Verification email template

[//]: # (- Welcome email template)

[//]: # (- Appointment confirmation template)

[//]: # (- Password reset template)

[//]: # (- Custom notification template)

## Error Handling

The service includes comprehensive error handling for:
- Message queue connection issues
- Email sending failures
- Template processing errors
- Invalid message formats
