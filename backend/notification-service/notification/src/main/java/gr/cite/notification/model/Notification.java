package gr.cite.notification.model;

import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.enums.NotificationContactType;

import java.time.Instant;
import java.util.UUID;

public class Notification {

    private UUID id;
    public final static String _id = "id";

    private Tenant tenant;
    public final static String _tenant = "tenant";

    private User user;
    public static final String _user = "user";

    private UUID type;
    public static final String _type = "type";

    private NotificationContactType contactTypeHint;
    public static final String _contactTypeHint = "contactTypeHint";

    private String contactHint;
    public static final String _contactHint = "contactHint";

    private Instant notifiedAt;
    public final static String _notifiedAt = "notifiedAt";

    private String hash;
    public final static String _hash = "hash";

    private IsActive isActive;
    public final static String _isActive = "isActive";

    private Instant createdAt;
    public final static String _createdAt = "createdAt";

    private Instant updatedAt;
    public final static String _updatedAt = "updatedAt";

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UUID getType() {
        return type;
    }

    public void setType(UUID type) {
        this.type = type;
    }

    public gr.cite.notification.common.enums.NotificationContactType getContactTypeHint() {
        return contactTypeHint;
    }

    public void setContactTypeHint(gr.cite.notification.common.enums.NotificationContactType contactTypeHint) {
        this.contactTypeHint = contactTypeHint;
    }

    public String getContactHint() {
        return contactHint;
    }

    public void setContactHint(String contactHint) {
        this.contactHint = contactHint;
    }

    public Instant getNotifiedAt() {
        return notifiedAt;
    }

    public void setNotifiedAt(Instant notifiedAt) {
        this.notifiedAt = notifiedAt;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
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
}
