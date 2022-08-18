package gr.cite.intelcomp.stiviewer.model.notification;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationContactType;
import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationNotifyState;
import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationTrackingProcess;
import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationTrackingState;
import gr.cite.intelcomp.stiviewer.model.Tenant;
import gr.cite.intelcomp.stiviewer.model.User;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

    private String data;
    public static final String _data = "data";

    private NotificationNotifyState notifyState;
    public final static String _notifyState = "notifyState";

    private NotificationContactType notifiedWith;
    public static final String _notifiedWith = "notifiedWith";

    private Integer retryCount;
    public static final String _retryCount = "retryCount";

    private NotificationTrackingState trackingState;
    public static final String _trackingState = "trackingState";

    private NotificationTrackingProcess trackingProcess;
    public static final String _trackingProcess = "trackingProcess";

    private String trackingData;
    public static final String _trackingData = "trackingData";

    private String provenanceRef;
    public static final String _provenanceRef = "provenanceRef";

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

    public NotificationNotifyState getNotifyState() {
        return notifyState;
    }

    public void setNotifyState(NotificationNotifyState notifyState) {
        this.notifyState = notifyState;
    }

    public NotificationContactType getNotifiedWith() {
        return notifiedWith;
    }

    public void setNotifiedWith(NotificationContactType notifiedWith) {
        this.notifiedWith = notifiedWith;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public NotificationTrackingState getTrackingState() {
        return trackingState;
    }

    public void setTrackingState(NotificationTrackingState trackingState) {
        this.trackingState = trackingState;
    }

    public NotificationTrackingProcess getTrackingProcess() {
        return trackingProcess;
    }

    public void setTrackingProcess(NotificationTrackingProcess trackingProcess) {
        this.trackingProcess = trackingProcess;
    }

    public String getTrackingData() {
        return trackingData;
    }

    public void setTrackingData(String trackingData) {
        this.trackingData = trackingData;
    }

    public String getProvenanceRef() {
        return provenanceRef;
    }

    public void setProvenanceRef(String provenanceRef) {
        this.provenanceRef = provenanceRef;
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
