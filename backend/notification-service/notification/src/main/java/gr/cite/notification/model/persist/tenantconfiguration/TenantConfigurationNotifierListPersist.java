package gr.cite.notification.model.persist.tenantconfiguration;

import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.common.validation.ValidId;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TenantConfigurationNotifierListPersist {
    @ValidId(message = "{validation.invalidid}")
    private UUID id;
    private Map<UUID, List<NotificationContactType>> notifiers;
    private String hash;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Map<UUID, List<NotificationContactType>> getNotifiers() {
        return notifiers;
    }

    public void setNotifiers(Map<UUID, List<NotificationContactType>> notifiers) {
        this.notifiers = notifiers;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
