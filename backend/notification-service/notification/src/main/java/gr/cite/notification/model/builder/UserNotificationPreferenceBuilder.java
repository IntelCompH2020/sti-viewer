package gr.cite.notification.model.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.common.types.tenantconfiguration.DefaultUserLocaleConfigurationDataContainer;
import gr.cite.notification.common.types.tenantconfiguration.EmailClientConfigurationDataContainer;
import gr.cite.notification.common.types.tenantconfiguration.NotifierListConfigurationDataContainer;
import gr.cite.notification.convention.ConventionService;
import gr.cite.notification.data.TenantConfigurationEntity;
import gr.cite.notification.data.UserNotificationPreferenceEntity;
import gr.cite.notification.model.TenantConfiguration;
import gr.cite.notification.model.UserNotificationPreference;
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
public class UserNotificationPreferenceBuilder extends BaseBuilder<UserNotificationPreference, UserNotificationPreferenceEntity> {

    private EnumSet<AuthorizationFlags> authorize  = EnumSet.of(AuthorizationFlags.None);
    private final ObjectMapper mapper;
    @Autowired
    public UserNotificationPreferenceBuilder(ConventionService conventionService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(UserNotificationPreferenceBuilder.class)));
        this.mapper = new ObjectMapper();
    }

    public UserNotificationPreferenceBuilder authorize(EnumSet<AuthorizationFlags> values){
        this.authorize = values;
        return this;
    }
    @Override
    public List<UserNotificationPreference> build(FieldSet fields, List<UserNotificationPreferenceEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0),Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size) .orElse(0));
        this.logger.trace(new DataLogEntry("requested fields",fields));
        if(fields == null || data == null || fields.isEmpty()) return new ArrayList<>();

        List<UserNotificationPreference> models =  new ArrayList<>();
        for(UserNotificationPreferenceEntity d : data){
            UserNotificationPreference m = new UserNotificationPreference();
            if(fields.hasField(this.asIndexer(UserNotificationPreference.Field.USER_ID))) m.setUserId(d.getUserId());
            if(fields.hasField(this.asIndexer(UserNotificationPreference.Field.TENANT_ID))) m.setTenantId(d.getTenantId());
            if(fields.hasField(this.asIndexer(UserNotificationPreference.Field.TYPE))) m.setType(d.getType());
            if(fields.hasField(this.asIndexer(UserNotificationPreference.Field.CHANNEL))) m.setChannel(d.getChannel());
            if(fields.hasField(this.asIndexer(UserNotificationPreference.Field.ORDINAL))) m.setOrdinal(d.getOrdinal());
            if(fields.hasField(this.asIndexer(TenantConfiguration.Field.CREATED_AT))) m.setCreatedAt(d.getCreatedAt());
            models.add(m);
        }
        this.logger.debug("build {} items",Optional.of(models).map(List::size).orElse(0));
        return models;
    }
}
