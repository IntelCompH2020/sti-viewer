package gr.cite.notification.model.builder;


import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.common.JsonHandlingService;
import gr.cite.notification.convention.ConventionService;
import gr.cite.notification.data.InAppNotificationEntity;
import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.model.InAppNotification;
import gr.cite.notification.model.Notification;
import gr.cite.notification.model.Tenant;
import gr.cite.notification.model.User;
import gr.cite.notification.query.TenantQuery;
import gr.cite.notification.query.UserQuery;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InAppNotificationBuilder extends BaseBuilder<InAppNotification, InAppNotificationEntity> {

    private final QueryFactory queryFactory;
    private final BuilderFactory builderFactory;
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public InAppNotificationBuilder(
            ConventionService conventionService,
            QueryFactory queryFactory, BuilderFactory builderFactory, JsonHandlingService jsonHandlingService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(InAppNotificationBuilder.class)));
        this.queryFactory = queryFactory;
        this.builderFactory = builderFactory;
    }

    public InAppNotificationBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<InAppNotification> build(FieldSet fields, List<InAppNotificationEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();

        FieldSet tenantFields = fields.extractPrefixed(this.asPrefix(InAppNotification.Field.TENANT));
        Map<UUID, Tenant> tenantItemsMap = this.collectTenants(tenantFields, data);

        FieldSet userFields = fields.extractPrefixed(this.asPrefix(InAppNotification.Field.USER));
        Map<UUID, User> userItemsMap = this.collectUsers(userFields, data);

        List<InAppNotification> models = new ArrayList<>();

        for (InAppNotificationEntity d : data) {
            InAppNotification m = new InAppNotification();
            if (fields.hasField(this.asIndexer(InAppNotification.Field.ID))) m.setId(d.getId());
            if (fields.hasField(this.asIndexer(InAppNotification.Field.TYPE))) m.setType(d.getType());
            if (fields.hasField(this.asIndexer(InAppNotification.Field.IS_ACTIVE))) m.setIsActive(d.getIsActive());
            if (fields.hasField(this.asIndexer(InAppNotification.Field.CREATED_AT))) m.setCreatedAt(d.getCreatedAt());
            if (fields.hasField(this.asIndexer(InAppNotification.Field.UPDATED_AT))) m.setUpdatedAt(d.getUpdatedAt());
            if (fields.hasField(this.asIndexer(InAppNotification.Field.HASH))) m.setHash(this.hashValue(d.getUpdatedAt()));
            if (fields.hasField(this.asIndexer(InAppNotification.Field.READ_TIME))) m.setReadTime(d.getReadTime());
            if (fields.hasField(this.asIndexer(InAppNotification.Field.TRACKING_STATE))) m.setTrackingState(d.getTrackingState());
            if (fields.hasField(this.asIndexer(InAppNotification.Field.BODY))) m.setBody(d.getBody());
            if (fields.hasField(this.asIndexer(InAppNotification.Field.EXTRA_DATA))) m.setExtraData(d.getExtraData());
            if (fields.hasField(this.asIndexer(InAppNotification.Field.PRIORITY))) m.setPriority(d.getPriority());
            if (fields.hasField(this.asIndexer(InAppNotification.Field.SUBJECT))) m.setSubject(d.getSubject());
            if (!tenantFields.isEmpty() && tenantItemsMap != null && tenantItemsMap.containsKey(d.getTenantId())) m.setTenant(tenantItemsMap.get(d.getTenantId()));
            if (!userFields.isEmpty() && userItemsMap != null && userItemsMap.containsKey(d.getUserId())) m.setUser(userItemsMap.get(d.getUserId()));
            models.add(m);
        }
        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
        return models;
    }

    private Map<UUID, Tenant> collectTenants(FieldSet fields, List<InAppNotificationEntity> data) throws MyApplicationException {
        if (fields.isEmpty() || data.isEmpty()) return null;
        this.logger.debug("checking related - {}", Tenant.class.getSimpleName());

        Map<UUID, Tenant> itemMap;
        if (!fields.hasOtherField(this.asIndexer(Tenant._id))) {
            itemMap = this.asEmpty(
                    data.stream().map(InAppNotificationEntity::getTenantId).distinct().collect(Collectors.toList()),
                    x -> {
                        Tenant item = new Tenant();
                        item.setId(x);
                        return item;
                    },
                    Tenant::getId);
        } else {
            FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(Tenant._id);
            TenantQuery q = this.queryFactory.query(TenantQuery.class).authorize(this.authorize).ids(data.stream().map(InAppNotificationEntity::getTenantId).distinct().collect(Collectors.toList()));
            itemMap = this.builderFactory.builder(TenantBuilder.class).authorize(this.authorize).asForeignKey(q, clone, Tenant::getId);
        }
        if (!fields.hasField(Tenant._id)) {
            itemMap.values().stream().filter(Objects::nonNull).peek(x -> x.setId(null)).collect(Collectors.toList());
        }

        return itemMap;
    }

    private Map<UUID, User> collectUsers(FieldSet fields, List<InAppNotificationEntity> data) throws MyApplicationException {
        if (fields.isEmpty() || data.isEmpty()) return null;
        this.logger.debug("checking related - {}", User.class.getSimpleName());

        Map<UUID, User> itemMap;
        if (!fields.hasOtherField(this.asIndexer(User._id))) {
            itemMap = this.asEmpty(
                    data.stream().map(InAppNotificationEntity::getUserId).distinct().collect(Collectors.toList()),
                    x -> {
                        User item = new User();
                        item.setId(x);
                        return item;
                    },
                    User::getId);
        } else {
            FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(User._id);
            UserQuery q = this.queryFactory.query(UserQuery.class).authorize(this.authorize).ids(data.stream().map(InAppNotificationEntity::getUserId).distinct().collect(Collectors.toList()));
            itemMap = this.builderFactory.builder(UserBuilder.class).authorize(this.authorize).asForeignKey(q, clone, User::getId);
        }
        if (!fields.hasField(User._id)) {
            itemMap.values().stream().filter(Objects::nonNull).peek(x -> x.setId(null)).collect(Collectors.toList());
        }

        return itemMap;
    }

}
