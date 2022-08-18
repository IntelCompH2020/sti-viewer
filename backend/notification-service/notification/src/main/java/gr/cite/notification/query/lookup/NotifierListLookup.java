package gr.cite.notification.query.lookup;

import java.util.Set;
import java.util.UUID;

public class NotifierListLookup {
    private Set<UUID> notificationTypes;

    public Set<UUID> getNotificationTypes() {
        return notificationTypes;
    }

    public void setNotificationTypes(Set<UUID> notificationTypes) {
        this.notificationTypes = notificationTypes;
    }
}
