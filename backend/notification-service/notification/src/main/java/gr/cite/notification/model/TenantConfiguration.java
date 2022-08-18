package gr.cite.notification.model;

import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.enums.TenantConfigurationType;
import gr.cite.notification.common.types.tenantconfiguration.DefaultUserLocaleConfigurationDataContainer;
import gr.cite.notification.common.types.tenantconfiguration.EmailClientConfigurationDataContainer;
import gr.cite.notification.common.types.tenantconfiguration.NotifierListConfigurationDataContainer;

import java.time.Instant;
import java.util.UUID;

public class TenantConfiguration {

    public static class Field {
        public static final String ID = "id";
        public static final String TENANT_ID = "tenantId";
        public static final String TYPE = "type";
        public static final String VALUE = "value";
        public static final String EMAIL_CLIENT_DATA = "emailClientData";
        public static final String DEFAULT_USER_LOCALE_DATA = "defaultUserLocaleData";
        public static final String NOTIFIER_LIST_DATA = "notifierListData";
        public static final String IS_ACTIVE = "isActive";
        public static final String CREATED_AT = "createdAt";
        public static final String UPDATED_AT = "updatedAt";
        public static final String HASH = "hash";
    }
    private UUID id;
    private UUID tenantId;
    private TenantConfigurationType type;
    private String value;
    private EmailClientConfigurationDataContainer emailClientData;
    private DefaultUserLocaleConfigurationDataContainer defaultUserLocaleData;
    private NotifierListConfigurationDataContainer notifierListData;
    private IsActive isActive;
    private Instant createdAt;
    private Instant updatedAt;
    private String hash;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public TenantConfigurationType getType() {
        return type;
    }

    public void setType(TenantConfigurationType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EmailClientConfigurationDataContainer getEmailClientData() {
        return emailClientData;
    }

    public void setEmailClientData(EmailClientConfigurationDataContainer emailClientData) {
        this.emailClientData = emailClientData;
    }

    public DefaultUserLocaleConfigurationDataContainer getDefaultUserLocaleData() {
        return defaultUserLocaleData;
    }

    public void setDefaultUserLocaleData(DefaultUserLocaleConfigurationDataContainer defaultUserLocaleData) {
        this.defaultUserLocaleData = defaultUserLocaleData;
    }

    public NotifierListConfigurationDataContainer getNotifierListData() {
        return notifierListData;
    }

    public void setNotifierListData(NotifierListConfigurationDataContainer notifierListData) {
        this.notifierListData = notifierListData;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
