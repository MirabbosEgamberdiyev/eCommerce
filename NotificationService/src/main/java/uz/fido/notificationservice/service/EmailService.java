package uz.fido.notificationservice.service;


import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


import uz.fido.notificationservice.model.Notification;
import uz.fido.notificationservice.repository.NotificationRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);

        // Saqlash
        Notification notification = new Notification();
        notification.setTo(to);
        notification.setSubject(subject);
        notification.setBody(text);
        notification.setSentAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }
}

