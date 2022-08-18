package gr.cite.notification.model;

import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.data.composite.CompositeUserNotificationPreferenceId;
import gr.cite.notification.data.tenant.TenantScopedBaseEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

public class UserNotificationPreference {

    public static class Field {
        public static final String USER_ID = "userId";
        public static final String TYPE = "type";
        public static final String CHANNEL = "channel";
        public static final String ORDINAL = "ordinal";
        public static final String CREATED_AT = "createdAt";
        public static final String TENANT_ID = "tenantId";
    }

    private UUID userId;
    private UUID type;
    private UUID tenantId;
    private NotificationContactType channel;
    private Integer ordinal;
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

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
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
