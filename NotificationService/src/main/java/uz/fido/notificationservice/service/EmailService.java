package uz.fido.notificationservice.service;


import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import uz.fido.notificationservice.model.Notification;
import uz.fido.notificationservice.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;
    private final Validator validator;

    public void sendSimpleMessage(String to, String subject, String text) {
        logger.debug("Preparing to send email to: {}", to);

        // Create and send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("mirabbosegamberdiyev7@gmail.com"); // Match SMTP username
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
            logger.info("Email sent to: {}", to);
        } catch (MailException e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }

        // Save notification
        Notification notification = new Notification();
        notification.setTo(to);
        notification.setSubject(subject);
        notification.setBody(text);
        notification.setSentAt(LocalDateTime.now());

        // Validate notification
        Set<jakarta.validation.ConstraintViolation<Notification>> violations = validator.validate(notification);
        if (!violations.isEmpty()) {
            String error = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .reduce("", (a, b) -> a + b + "; ");
            logger.warn("Invalid notification data: {}", error);
            throw new IllegalArgumentException("Invalid notification data: " + error);
        }

        notificationRepository.save(notification);
        logger.debug("Notification saved for recipient: {}", to);
    }
}