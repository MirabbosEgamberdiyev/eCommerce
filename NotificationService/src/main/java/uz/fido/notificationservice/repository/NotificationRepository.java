package uz.fido.notificationservice.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import uz.fido.notificationservice.model.Notification;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByTo(String to);
}