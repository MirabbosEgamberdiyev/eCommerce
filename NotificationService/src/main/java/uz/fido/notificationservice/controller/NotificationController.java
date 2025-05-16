package uz.fido.notificationservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.fido.notificationservice.service.EmailService;

@RestController
@RequestMapping("/api/notify")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestParam String to,
                                            @RequestParam String subject,
                                            @RequestParam String body) {
        emailService.sendSimpleMessage(to, subject, body);
        return ResponseEntity.ok("Email sent successfully");
    }
}

