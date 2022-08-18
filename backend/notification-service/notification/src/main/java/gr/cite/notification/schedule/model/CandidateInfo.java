package gr.cite.notification.schedule.model;

import gr.cite.notification.common.enums.NotificationNotifyState;

import java.time.Instant;
import java.util.UUID;

public class CandidateInfo {
    private UUID notificationId;
    private NotificationNotifyState previousState;
    private Instant notificationCreatedAt;

    public CandidateInfo() {
    }

    public CandidateInfo(UUID notificationId, NotificationNotifyState previousState, Instant notificationCreatedAt) {
        this.notificationId = notificationId;
        this.previousState = previousState;
        this.notificationCreatedAt = notificationCreatedAt;
    }

    public UUID getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(UUID notificationId) {
        this.notificationId = notificationId;
    }

    public NotificationNotifyState getPreviousState() {
        return previousState;
    }

    public void setPreviousState(NotificationNotifyState previousState) {
        this.previousState = previousState;
    }

    public Instant getNotificationCreatedAt() {
        return notificationCreatedAt;
    }

    public void setNotificationCreatedAt(Instant notificationCreatedAt) {
        this.notificationCreatedAt = notificationCreatedAt;
    }
}
