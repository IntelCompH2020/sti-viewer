package gr.cite.notification.service.track.model;

import gr.cite.notification.common.enums.NotificationTrackingProgress;
import gr.cite.notification.common.enums.NotificationTrackingState;

public class TrackerResponse {
    private NotificationTrackingState trackingState;
    private NotificationTrackingProgress trackingProgress;
    private String trackingData;

    public TrackerResponse() {
    }

    public TrackerResponse(NotificationTrackingState trackingState, NotificationTrackingProgress trackingProgress, String trackingData) {
        this.trackingState = trackingState;
        this.trackingProgress = trackingProgress;
        this.trackingData = trackingData;
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
