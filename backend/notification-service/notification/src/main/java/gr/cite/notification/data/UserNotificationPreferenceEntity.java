package gr.cite.notification.data;

import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.data.composite.CompositeUserNotificationPreferenceId;
import gr.cite.notification.data.tenant.TenantScopedBaseEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_notification_preference")
@IdClass(CompositeUserNotificationPreferenceId.class)
public class UserNotificationPreferenceEntity extends TenantScopedBaseEntity {

    public static class Field {
        public static final String USER_ID = "userId";
        public static final String TYPE = "type";
        public static final String CHANNEL = "channel";
        public static final String ORDINAL = "ordinal";
        public static final String CREATED_AT = "createdAt";
    }

    @Id
    @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
    private UUID userId;

    @Id
    @Column(name = "type", columnDefinition = "uuid", nullable = false)
    private UUID type;

    @Id
    @Column(name = "channel", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationContactType channel;

    @Column(name = "ordinal", nullable = false)
    private Integer ordinal;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

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

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
