package gr.cite.notification.service.tenantconfiguration;

import gr.cite.notification.common.types.tenantconfiguration.DefaultUserLocaleConfigurationDataContainer;
import gr.cite.notification.common.types.tenantconfiguration.EmailClientConfigurationDataContainer;
import gr.cite.notification.common.types.tenantconfiguration.NotifierListConfigurationDataContainer;
import gr.cite.notification.model.TenantConfiguration;
import gr.cite.notification.model.persist.tenantconfiguration.TenantConfigurationEmailClientPersist;
import gr.cite.notification.model.persist.tenantconfiguration.TenantConfigurationNotifierListPersist;
import gr.cite.notification.model.persist.tenantconfiguration.TenantConfigurationUserLocaleIntegrationPersist;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.util.Set;
import java.util.UUID;

public interface TenantConfigurationService {
    EmailClientConfigurationDataContainer collectTenantEmailClient();
    DefaultUserLocaleConfigurationDataContainer collectTenantUserLocale();
    NotifierListConfigurationDataContainer collectTenantNotifierList();
    NotifierListConfigurationDataContainer collectTenantAvailableNotifierList(Set<UUID> notificationTypes);
    TenantConfiguration persist(TenantConfigurationEmailClientPersist emailClientPersist, FieldSet fieldSet);
    TenantConfiguration persist(TenantConfigurationUserLocaleIntegrationPersist userLocaleIntegrationPersist, FieldSet fieldSet);
    TenantConfiguration persist(TenantConfigurationNotifierListPersist notifierListPersist, FieldSet fieldSet);
    void deleteAndSave(UUID id) throws InvalidApplicationException;

}
