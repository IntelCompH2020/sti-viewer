package gr.cite.notification.data;

import gr.cite.notification.common.enums.InAppNotificationPriority;
import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.enums.NotificationInAppTracking;
import gr.cite.notification.data.tenant.TenantScopedBaseEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "in_app_notification")
public class InAppNotificationEntity extends TenantScopedBaseEntity {
    public static class Field {
        public static final String ID = "id";
        public static final String USER_ID = "userId";
        public static final String IS_ACTIVE = "isActive";
        public static final String TYPE = "type";
        public static final String READ_TIME = "readTime";
        public static final String TRACKING_STATE = "trackingState";
        public static final String PRIORITY = "priority";
        public static final String SUBJECT = "subject";
        public static final String BODY = "body";
        public static final String EXTRA_DATA = "extraData";
        public static final String CREATED_AT = "createdAt";
        public static final String UPDATED_AT = "updatedAt";
    }

    @Id
    @Column(name = "id", columnDefinition = "uuid", nullable = false)
    private UUID id;

    @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
    private UUID userId;

    @Column(name = "is_active", nullable = false)
    @Enumerated(EnumType.STRING)
    private IsActive isActive;

    @Column(name = "type", columnDefinition = "uuid", nullable = false)
    private UUID type;

    @Column(name = "read_time")
    private Instant readTime;

    @Column(name = "tracking_state", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationInAppTracking trackingState;

    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private InAppNotificationPriority priority;

    @Column(name = "subject")
    private String subject;

    @Column(name = "body")
    private String body;

    @Column(name = "extra_data")
    private String extraData;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @Version
    private Instant updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public IsActive getIsActive() {
        return isActive;
    }

    public void setIsActive(IsActive isActive) {
        this.isActive = isActive;
    }

    public UUID getType() {
        return type;
    }

    public void setType(UUID type) {
        this.type = type;
    }

    public Instant getReadTime() {
        return readTime;
    }

    public void setReadTime(Instant readTime) {
        this.readTime = readTime;
    }

    public NotificationInAppTracking getTrackingState() {
        return trackingState;
    }

    public void setTrackingState(NotificationInAppTracking trackingState) {
        this.trackingState = trackingState;
    }

    public InAppNotificationPriority getPriority() {
        return priority;
    }

    public void setPriority(InAppNotificationPriority priority) {
        this.priority = priority;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
