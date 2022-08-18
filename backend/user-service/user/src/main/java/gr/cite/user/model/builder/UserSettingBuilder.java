package gr.cite.user.model.builder;

import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import gr.cite.user.authorization.AuthorizationFlags;
import gr.cite.user.common.JsonHandlingService;
import gr.cite.user.convention.ConventionService;
import gr.cite.user.data.UserSettingsEntity;
import gr.cite.user.model.UserSettings;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.time.Instant;
import java.util.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserSettingBuilder extends BaseBuilder<UserSettings.UserSetting, UserSettingsEntity> {
    private final BuilderFactory builderFactory;
    private final QueryFactory queryFactory;
    private final JsonHandlingService jsonHandlingService;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    public UserSettingBuilder(ConventionService conventionService,BuilderFactory builderFactory, QueryFactory queryFactory, JsonHandlingService jsonHandlingService) {
        super(conventionService,  new LoggerService(LoggerFactory.getLogger(UserSettingBuilder.class)));
        this.builderFactory = builderFactory;
        this.queryFactory = queryFactory;
        this.jsonHandlingService = jsonHandlingService;
    }
    public UserSettingBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<UserSettings.UserSetting> build(FieldSet fields, List<UserSettingsEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();


        List<UserSettings.UserSetting> models = new ArrayList<>();

        for (UserSettingsEntity d : data) {
            UserSettings.UserSetting m = new UserSettings.UserSetting();
            if (fields.hasField(this.asIndexer(UserSettings.UserSetting._id))) m.setId(d.getId());
            if (fields.hasField(this.asIndexer(UserSettings.UserSetting._name))) m.setName(d.getName());
            if (fields.hasField(this.asIndexer(UserSettings.UserSetting._value))) m.setValue(d.getValue());
            if (fields.hasField(this.asIndexer(UserSettings.UserSetting._key))) m.setKey(d.getKey());
            if (fields.hasField(this.asIndexer(UserSettings.UserSetting._type))) m.setType(d.getType());
            if (fields.hasField(this.asIndexer(UserSettings.UserSetting._entityId))) m.setEntityId(d.getEntityId());
            if (fields.hasField(this.asIndexer(UserSettings.UserSetting._entityType))) m.setEntityType(d.getEntityType());
            if (fields.hasField(this.asIndexer(UserSettings.UserSetting._createdAt))) m.setCreatedAt(d.getCreatedAt());
            if (fields.hasField(this.asIndexer(UserSettings.UserSetting._updatedAt))) m.setUpdatedAt(d.getUpdatedAt());
           // if (fields.hasField(this.asIndexer(UserSettings.UserSetting._hash))) m.setHash(this.hashValue(Instant.ofEpochMilli(d.getUpdatedAt().toEpochMilli())));
            models.add(m);
        }
        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
        return models;
    }
}
