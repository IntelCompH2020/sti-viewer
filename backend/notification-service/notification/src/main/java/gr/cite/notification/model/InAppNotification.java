package gr.cite.notification.model;

import gr.cite.notification.common.enums.InAppNotificationPriority;
import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.enums.NotificationInAppTracking;
import gr.cite.notification.data.tenant.TenantScopedBaseEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;
public class InAppNotification {
    public static class Field {
        public static final String ID = "id";
        public static final String USER = "user";
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
        public static final String TENANT = "tenant";
        public static final String HASH = "hash";
    }

    private UUID id;
    private User user;
    private IsActive isActive;
    private UUID type;
    private Instant readTime;
    private NotificationInAppTracking trackingState;
    private InAppNotificationPriority priority;
    private String subject;
    private String body;
    private String extraData;
    private Instant createdAt;
    private Instant updatedAt;
    private Tenant tenant;
    private String hash;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
