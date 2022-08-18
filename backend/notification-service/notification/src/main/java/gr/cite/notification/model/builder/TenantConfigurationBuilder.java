package gr.cite.notification.model.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.common.enums.TenantConfigurationType;
import gr.cite.notification.common.types.tenantconfiguration.DefaultUserLocaleConfigurationDataContainer;
import gr.cite.notification.common.types.tenantconfiguration.EmailClientConfigurationDataContainer;
import gr.cite.notification.common.types.tenantconfiguration.NotifierListConfigurationDataContainer;
import gr.cite.notification.convention.ConventionService;
import gr.cite.notification.data.TenantConfigurationEntity;
import gr.cite.notification.model.Tenant;
import gr.cite.notification.model.TenantConfiguration;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.*;

@Component
@RequestScope
public class TenantConfigurationBuilder extends BaseBuilder<TenantConfiguration, TenantConfigurationEntity> {

    private EnumSet<AuthorizationFlags> authorize  = EnumSet.of(AuthorizationFlags.None);
    private final ObjectMapper mapper;
    @Autowired
    public TenantConfigurationBuilder(ConventionService conventionService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(TenantConfigurationBuilder.class)));
        this.mapper = new ObjectMapper();
    }

    public TenantConfigurationBuilder authorize(EnumSet<AuthorizationFlags> values){
        this.authorize = values;
        return this;
    }
    @Override
    public List<TenantConfiguration> build(FieldSet fields, List<TenantConfigurationEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0),Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size) .orElse(0));
        this.logger.trace(new DataLogEntry("requested fields",fields));
        if(fields == null || data == null || fields.isEmpty()) return new ArrayList<>();

        List<TenantConfiguration> models =  new ArrayList<>();
        for(TenantConfigurationEntity d : data){
            TenantConfiguration m = new TenantConfiguration();
            if(fields.hasField(this.asIndexer(TenantConfiguration.Field.ID))) m.setId(d.getId());
            if(fields.hasField(this.asIndexer(TenantConfiguration.Field.TENANT_ID))) m.setTenantId(d.getTenantId());
            if(fields.hasField(this.asIndexer(TenantConfiguration.Field.TYPE))) m.setType(d.getType());
            if(fields.hasField(this.asIndexer(TenantConfiguration.Field.VALUE))) m.setValue(d.getValue());
            if(fields.hasField(this.asIndexer(TenantConfiguration.Field.DEFAULT_USER_LOCALE_DATA))) {
                try {
                    m.setDefaultUserLocaleData(mapper.readValue(d.getValue(), DefaultUserLocaleConfigurationDataContainer.class));

                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (!fields.extractPrefixed(this.asIndexer(TenantConfiguration.Field.DEFAULT_USER_LOCALE_DATA)).isEmpty()) {
                try {
                    DefaultUserLocaleConfigurationDataContainer container = mapper.readValue(d.getValue(), DefaultUserLocaleConfigurationDataContainer.class);
                    if (container != null) {
                        m.setDefaultUserLocaleData(new DefaultUserLocaleConfigurationDataContainer());
                        if (fields.hasField(this.asIndexer(TenantConfiguration.Field.DEFAULT_USER_LOCALE_DATA, DefaultUserLocaleConfigurationDataContainer.Field.LANGUAGE)))
                            m.getDefaultUserLocaleData().setLanguage(container.getLanguage());
                        if (fields.hasField(this.asIndexer(TenantConfiguration.Field.DEFAULT_USER_LOCALE_DATA, DefaultUserLocaleConfigurationDataContainer.Field.TIME_ZONE)))
                            m.getDefaultUserLocaleData().setTimeZone(container.getTimeZone());
                        if (fields.hasField(this.asIndexer(TenantConfiguration.Field.DEFAULT_USER_LOCALE_DATA, DefaultUserLocaleConfigurationDataContainer.Field.CULTURE)))
                            m.getDefaultUserLocaleData().setCulture(container.getCulture());
                    }
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if(fields.hasField(this.asIndexer(TenantConfiguration.Field.EMAIL_CLIENT_DATA))) {
                try {
                    m.setEmailClientData(mapper.readValue(d.getValue(), EmailClientConfigurationDataContainer.class));
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (!fields.extractPrefixed(this.asIndexer(TenantConfiguration.Field.EMAIL_CLIENT_DATA)).isEmpty()) {
                try {
                    EmailClientConfigurationDataContainer container = mapper.readValue(d.getValue(), EmailClientConfigurationDataContainer.class);
                    if (container != null) {
                        m.setEmailClientData(new EmailClientConfigurationDataContainer());
                        if (fields.hasField(this.asIndexer(TenantConfiguration.Field.EMAIL_CLIENT_DATA, EmailClientConfigurationDataContainer.Field.ENABLE_SSL)))
                            m.getEmailClientData().setEnableSSL(container.getEnableSSL());
                        if (fields.hasField(this.asIndexer(TenantConfiguration.Field.EMAIL_CLIENT_DATA, EmailClientConfigurationDataContainer.Field.REQUIRE_CREDENTIALS)))
                            m.getEmailClientData().setRequireCredentials(container.getRequireCredentials());
                        if (fields.hasField(this.asIndexer(TenantConfiguration.Field.EMAIL_CLIENT_DATA, EmailClientConfigurationDataContainer.Field.HOST_SERVER)))
                            m.getEmailClientData().setHostServer(container.getHostServer());
                        if (fields.hasField(this.asIndexer(TenantConfiguration.Field.EMAIL_CLIENT_DATA, EmailClientConfigurationDataContainer.Field.HOST_PORT_NO)))
                            m.getEmailClientData().setHostPortNo(container.getHostPortNo());
                        if (fields.hasField(this.asIndexer(TenantConfiguration.Field.EMAIL_CLIENT_DATA, EmailClientConfigurationDataContainer.Field.CERTIFICATE_PATH)))
                            m.getEmailClientData().setCertificatePath(container.getCertificatePath());
                        if (fields.hasField(this.asIndexer(TenantConfiguration.Field.EMAIL_CLIENT_DATA, EmailClientConfigurationDataContainer.Field.EMAIL_ADDRESS)))
                            m.getEmailClientData().setEmailAddress(container.getEmailAddress());
                        if (fields.hasField(this.asIndexer(TenantConfiguration.Field.EMAIL_CLIENT_DATA, EmailClientConfigurationDataContainer.Field.EMAIL_USER_NAME)))
                            m.getEmailClientData().setEmailUserName(container.getEmailUserName());
                        if (fields.hasField(this.asIndexer(TenantConfiguration.Field.EMAIL_CLIENT_DATA, EmailClientConfigurationDataContainer.Field.EMAIL_PASSWORD)))
                            m.getEmailClientData().setEmailPassword(container.getEmailPassword());
                    }
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if(fields.hasField(this.asIndexer(TenantConfiguration.Field.NOTIFIER_LIST_DATA))) {
                try {
                    m.setNotifierListData(mapper.readValue(d.getValue(), NotifierListConfigurationDataContainer.class));
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (!fields.extractPrefixed(this.asIndexer(TenantConfiguration.Field.NOTIFIER_LIST_DATA)).isEmpty()) {
                try {
                    NotifierListConfigurationDataContainer container = mapper.readValue(d.getValue(), NotifierListConfigurationDataContainer.class);
                    if (container != null) {
                        m.setNotifierListData(new NotifierListConfigurationDataContainer());
                        if (fields.hasField(this.asIndexer(TenantConfiguration.Field.NOTIFIER_LIST_DATA, NotifierListConfigurationDataContainer.Field.NOTIFIERS)))
                            m.getNotifierListData().setNotifiers(container.getNotifiers());
                    }
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if(fields.hasField(this.asIndexer(TenantConfiguration.Field.IS_ACTIVE))) m.setIsActive(d.getIsActive());
            if(fields.hasField(this.asIndexer(TenantConfiguration.Field.CREATED_AT))) m.setCreatedAt(d.getCreatedAt());
            if(fields.hasField(this.asIndexer(TenantConfiguration.Field.UPDATED_AT))) m.setUpdatedAt(d.getUpdatedAt());
            if(fields.hasField(this.asIndexer(TenantConfiguration.Field.HASH))) m.setHash(this.hashValue(d.getUpdatedAt()));
            models.add(m);
        }
        this.logger.debug("build {} items",Optional.of(models).map(List::size).orElse(0));
        return models;
    }
}
