package gr.cite.notification.data;

import gr.cite.notification.common.enums.*;
import gr.cite.notification.data.tenant.TenantScopedBaseEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification")
public class NotificationEntity {

    public static class Field {
        public final static String _id = "id";
        public static final String _userId = "userId";
        public static final String _type = "type";
        public static final String _contactTypeHint = "contactTypeHint";
        public static final String _contactHint = "contactHint";
        public final static String _notifiedAt = "notifiedAt";
        public final static String _isActive = "isActive";
        public final static String _createdAt = "createdAt";
        public final static String _updatedAt = "updatedAt";
        public final static String _data = "data";
        public final static String _retryCount = "retryCount";
        public final static String _notifyState = "notifyState";
        public final static String _notifiedWith = "notifiedWith";
        public final static String _tenantId = "tenantId";
        public final static String _trackingState = "trackingState";
        public final static String _trackingProgress = "trackingProgress";
        public final static String _trackingData = "trackingData";
    }

    @Id
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "type", columnDefinition = "uuid", nullable = false)
    private UUID type;

    @Column(name = "contact_type_hint")
    @Enumerated(EnumType.STRING)
    private NotificationContactType contactTypeHint;

    @Column(name = "contact_hint")
    private String contactHint;

    @Column(name = "notify_state", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationNotifyState notifyState;

    @Column(name = "notifiedWith")
    private NotificationContactType notifiedWith;
    @Column(name = "notified_at")
    private Instant notifiedAt;

    @Column(name = "data")
    private String data;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "is_active", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private IsActive isActive;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @Version
    private Instant updatedAt;

    @Column(name = "tenant_id", columnDefinition = "uuid", nullable = false)
    private UUID tenantId;

    @Column(name = "tracking_state", nullable = false)
    private NotificationTrackingState trackingState;

    @Column(name = "tracking_process", nullable = false)
    private NotificationTrackingProgress trackingProgress;

    @Column(name = "tracking_data")
    private String trackingData;

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

    public UUID getType() {
        return type;
    }

    public void setType(UUID type) {
        this.type = type;
    }

    public NotificationContactType getContactTypeHint() {
        return contactTypeHint;
    }

    public void setContactTypeHint(NotificationContactType contactTypeHint) {
        this.contactTypeHint = contactTypeHint;
    }

    public String getContactHint() {
        return contactHint;
    }

    public void setContactHint(String contactHint) {
        this.contactHint = contactHint;
    }

    public NotificationContactType getNotifiedWith() {
        return notifiedWith;
    }

    public void setNotifiedWith(NotificationContactType notifiedWith) {
        this.notifiedWith = notifiedWith;
    }

    public NotificationNotifyState getNotifyState() {
        return notifyState;
    }

    public void setNotifyState(NotificationNotifyState notifyState) {
        this.notifyState = notifyState;
    }

    public Instant getNotifiedAt() {
        return notifiedAt;
    }

    public void setNotifiedAt(Instant notifiedAt) {
        this.notifiedAt = notifiedAt;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public IsActive getIsActive() {
        return isActive;
    }

    public void setIsActive(IsActive isActive) {
        this.isActive = isActive;
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

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public NotificationTrackingState getTrackingState() {
        return trackingState;
    }

    public void setTrackingState(NotificationTrackingState trackingState) {
        this.trackingState = trackingState;
    }

    public NotificationTrackingProgress getTrackingProgress() {
        return trackingProgress;
    }

    public void setTrackingProgress(NotificationTrackingProgress trackingProgress) {
        this.trackingProgress = trackingProgress;
    }

    public String getTrackingData() {
        return trackingData;
    }

    public void setTrackingData(String trackingData) {
        this.trackingData = trackingData;
    }
}
