package gr.cite.notification.service.inappnotification;

import javax.management.InvalidApplicationException;
import java.util.List;
import java.util.UUID;

public interface InAppNotificationService {

    void markAsRead(List<UUID> ids);
    void markAsRead(UUID id);
    void deleteAndSave(UUID id) throws InvalidApplicationException;
    void markAsReadAllUserNotification(UUID userId);
}
