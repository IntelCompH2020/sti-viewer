package gr.cite.notification.service.contact.model;

import java.util.UUID;

public class InAppContact implements Contact{
    private UUID userId;

    public InAppContact() {
    }

    public InAppContact(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
