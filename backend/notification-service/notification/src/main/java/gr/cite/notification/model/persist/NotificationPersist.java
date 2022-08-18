package gr.cite.notification.model.persist;

import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.common.enums.NotificationNotifyState;
import gr.cite.notification.common.validation.ValidId;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public class NotificationPersist {

    public static class Field {
        public static final String _id = "id";
        public static final String _userId = "userId";
        public static final String _type = "type";
        public static final String _contactTypeHint = "contactTypeHint";
        public static final String _contactHint = "contactHint";
        public final static String _notifiedAt = "notifiedAt";
        public final static String _notifyState = "notifyState";
        public final static String _notifiedWith = "notifiedWith";
        public final static String _data = "data";
        public final static String _retryCount = "retryCount";
    }

    @ValidId(message = "{validation.invalidid}")
    private UUID id;

    @NotNull(message = "{validation.empty}")
    private UUID userId;

    private UUID type;

    private gr.cite.notification.common.enums.NotificationContactType contactTypeHint;

    private String contactHint;

    private Instant notifiedAt;

    private NotificationNotifyState notifyState;

    private NotificationContactType notifiedWith;

    private String data;

    private Integer retryCount;

    private String hash;

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

    public NotificationNotifyState getNotifyState() {
        return notifyState;
    }

    public void setNotifyState(NotificationNotifyState notifyState) {
        this.notifyState = notifyState;
    }

    public gr.cite.notification.common.enums.NotificationContactType getNotifiedWith() {
        return notifiedWith;
    }

    public void setNotifiedWith(gr.cite.notification.common.enums.NotificationContactType notifiedWith) {
        this.notifiedWith = notifiedWith;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

}
