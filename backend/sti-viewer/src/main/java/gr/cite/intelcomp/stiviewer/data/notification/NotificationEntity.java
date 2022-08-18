package gr.cite.intelcomp.stiviewer.data.notification;


import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationContactType;
import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationNotifyState;
import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationTrackingProcess;
import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationTrackingState;
import gr.cite.intelcomp.stiviewer.data.tenant.TenantScopedBaseEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification")
public class NotificationEntity extends TenantScopedBaseEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    public static final String _id = "id";

    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;
    public static final String _userId = "userId";

    @Column(name = "type", columnDefinition = "uuid")
    private UUID type;
    public static final String _type = "type";

    @Column(name = "contact_type_hint")
    @Enumerated(EnumType.STRING)
    private NotificationContactType contactTypeHint;
    public static final String _contactTypeHint = "contactTypeHint";

    @Column(name = "contact_hint")
    private String contactHint;
    public static final String _contactHint = "contactHint";

    @Column(name = "data")
    private String data;
    public static final String _data = "data";

    @Column(name = "notify_state")
    @Enumerated(EnumType.STRING)
    private NotificationNotifyState notifyState;
    public final static String _notifyState = "notifyState";

    @Column(name = "notified_with")
    @Enumerated(EnumType.STRING)
    private NotificationContactType notifiedWith;
    public static final String _notifiedWith = "notifiedWith";

    @Column(name = "notified_at")
    private Instant notifiedAt;
    public static final String _notifiedAt = "notifiedAt";

    @Column(name = "retry_count")
    private Integer retryCount;
    public static final String _retryCount = "retryCount";

    @Column(name = "tracking_state")
    private NotificationTrackingState trackingState;
    public static final String _trackingState = "trackingState";

    @Column(name = "tracking_process")
    private NotificationTrackingProcess trackingProcess;
    public static final String _trackingProcess = "trackingProcess";

    @Column(name = "tracking_data")
    private String trackingData;
    public static final String _trackingData = "trackingData";

    @Column(name = "provenance_ref")
    private String provenanceRef;
    public static final String _provenanceRef = "provenanceRef";

    @Column(name = "is_active", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private IsActive isActive;
    public static final String _isActive = "isActive";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    public static final String _createdAt = "createdAt";

    @Column(name = "updated_at", nullable = false)
    @Version
    private Instant updatedAt;
    public static final String _updatedAt = "updatedAt";

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

    public Instant getNotifiedAt() {
        return notifiedAt;
    }

    public void setNotifiedAt(Instant notifiedAt) {
        this.notifiedAt = notifiedAt;
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
