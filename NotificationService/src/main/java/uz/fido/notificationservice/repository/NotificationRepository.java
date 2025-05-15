package uz.fido.notificationservice.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uz.fido.notificationservice.model.Notification;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
}
