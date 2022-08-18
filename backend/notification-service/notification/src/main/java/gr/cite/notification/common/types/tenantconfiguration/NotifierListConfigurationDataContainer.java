package gr.cite.notification.common.types.tenantconfiguration;

import gr.cite.notification.common.enums.NotificationContactType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NotifierListConfigurationDataContainer {

    public static class Field {
        public static final String NOTIFIERS = "notifiers";
    }
    private Map<UUID, List<NotificationContactType>> notifiers;

    public NotifierListConfigurationDataContainer() {
    }

    public NotifierListConfigurationDataContainer(Map<UUID, List<NotificationContactType>> notifiers) {
        this.notifiers = notifiers;
    }

    public Map<UUID, List<NotificationContactType>> getNotifiers() {
        return notifiers;
    }

    public void setNotifiers(Map<UUID, List<NotificationContactType>> notifiers) {
        this.notifiers = notifiers;
    }
}
