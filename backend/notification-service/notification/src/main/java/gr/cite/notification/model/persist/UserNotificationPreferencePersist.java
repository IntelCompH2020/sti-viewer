package gr.cite.notification.model.persist;

import gr.cite.notification.common.enums.NotificationContactType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserNotificationPreferencePersist {

    private UUID userId;
    private Map<UUID, List<NotificationContactType>> notificationPreferences;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Map<UUID, List<NotificationContactType>> getNotificationPreferences() {
        return notificationPreferences;
    }

    public void setNotificationPreferences(Map<UUID, List<NotificationContactType>> notificationPreferences) {
        this.notificationPreferences = notificationPreferences;
    }
}
