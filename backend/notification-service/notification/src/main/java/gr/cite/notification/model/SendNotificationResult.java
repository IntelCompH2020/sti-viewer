package gr.cite.notification.model;

import gr.cite.notification.common.enums.NotificationContactType;

public class SendNotificationResult {
    private Boolean success;
    private NotificationContactType contactType;
    private String trackingData;

    public SendNotificationResult() {
    }

    public SendNotificationResult(Boolean success, NotificationContactType contactType) {
        this.success = success;
        this.contactType = contactType;
    }

    public SendNotificationResult(Boolean success, NotificationContactType contactType, String trackingData) {
        this.success = success;
        this.contactType = contactType;
        this.trackingData = trackingData;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public NotificationContactType getContactType() {
        return contactType;
    }

    public void setContactType(NotificationContactType contactType) {
        this.contactType = contactType;
    }

    public String getTrackingData() {
        return trackingData;
    }

    public void setTrackingData(String trackingData) {
        this.trackingData = trackingData;
    }
}
