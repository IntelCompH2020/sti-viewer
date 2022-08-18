package gr.cite.notification.data.composite;

import gr.cite.notification.common.enums.NotificationContactType;

import java.io.Serializable;
import java.util.UUID;

public class CompositeUserNotificationPreferenceId implements Serializable {
    private UUID userId;
    private UUID type;
    private NotificationContactType channel;

    public CompositeUserNotificationPreferenceId() {
    }

    public CompositeUserNotificationPreferenceId(UUID userId, UUID type, NotificationContactType channel) {
        this.userId = userId;
        this.type = type;
        this.channel = channel;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getType() {
        return type;
    }

    public void setType(UUID type) {
        this.type = type;
    }

    public NotificationContactType getChannel() {
        return channel;
    }

    public void setChannel(NotificationContactType channel) {
        this.channel = channel;
    }
}
