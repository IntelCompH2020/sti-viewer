package gr.cite.intelcomp.stiviewer.model.persist.notification;

import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationContactType;
import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationNotifyState;
import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationTrackingProcess;
import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationTrackingState;
import gr.cite.intelcomp.stiviewer.common.validation.FieldNotNullIfOtherSet;
import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@FieldNotNullIfOtherSet(message = "{validation.hashempty}")
public class NotificationPersist {

    @ValidId(message = "{validation.invalidid}")
    private UUID id;

    @NotNull(message = "{validation.empty}")
    private UUID userId;

    private UUID type;

    private NotificationContactType contactTypeHint;

    private String contactHint;

    private String data;

    private NotificationNotifyState notifyState;

    private NotificationContactType notifiedWith;

    private Integer retryCount;

    private NotificationTrackingState trackingState;

    private NotificationTrackingProcess trackingProcess;

    private String trackingData;

    private String provenanceRef;

    private Instant notifiedAt;

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

}
