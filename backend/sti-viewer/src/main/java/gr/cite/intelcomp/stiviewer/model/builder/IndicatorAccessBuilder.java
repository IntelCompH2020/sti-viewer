package gr.cite.intelcomp.stiviewer.model.builder;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.IndicatorAccessEntity;
import gr.cite.intelcomp.stiviewer.data.tenant.TenantScopedBaseEntity;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.IndicatorAccess;
import gr.cite.intelcomp.stiviewer.model.Tenant;
import gr.cite.intelcomp.stiviewer.model.User;
import gr.cite.intelcomp.stiviewer.query.IndicatorQuery;
import gr.cite.intelcomp.stiviewer.query.TenantQuery;
import gr.cite.intelcomp.stiviewer.query.UserQuery;
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
public class IndicatorAccessBuilder extends BaseBuilder<IndicatorAccess, IndicatorAccessEntity> {

    private final QueryFactory queryFactory;
    private final BuilderFactory builderFactory;
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public IndicatorAccessBuilder(
            ConventionService conventionService,
            QueryFactory queryFactory, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(IndicatorAccessBuilder.class)));
        this.queryFactory = queryFactory;
        this.builderFactory = builderFactory;
    }

    public IndicatorAccessBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<IndicatorAccess> build(FieldSet fields, List<IndicatorAccessEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || data == null || fields.isEmpty())
            return new ArrayList<>();

        FieldSet tenantFields = fields.extractPrefixed(this.asPrefix(IndicatorAccess._tenant));
        Map<UUID, Tenant> tenantItemsMap = this.collectTenants(tenantFields, data);

        FieldSet indicatorFields = fields.extractPrefixed(this.asPrefix(IndicatorAccess._indicator));
        Map<UUID, Indicator> indicatorItemsMap = this.collectIndicators(indicatorFields, data);

        FieldSet userFields = fields.extractPrefixed(this.asPrefix(IndicatorAccess._user));
        Map<UUID, User> userItemsMap = this.collectUsers(userFields, data);

        List<IndicatorAccess> models = new ArrayList<>(100);

        for (IndicatorAccessEntity d : data) {
            IndicatorAccess m = new IndicatorAccess();
            if (fields.hasField(this.asIndexer(IndicatorAccess._id)))
                m.setId(d.getId());
            if (fields.hasField(this.asIndexer(IndicatorAccess._isActive)))
                m.setIsActive(d.getIsActive());
            if (fields.hasField(this.asIndexer(IndicatorAccess._createdAt)))
                m.setCreatedAt(d.getCreatedAt());
            if (fields.hasField(this.asIndexer(IndicatorAccess._updatedAt)))
                m.setUpdatedAt(d.getUpdatedAt());
            if (fields.hasField(this.asIndexer(IndicatorAccess._hash)))
                m.setHash(this.hashValue(d.getUpdatedAt()));
            if (!tenantFields.isEmpty() && tenantItemsMap != null && tenantItemsMap.containsKey(d.getTenantId()))
                m.setTenant(tenantItemsMap.get(d.getTenantId()));
            if (!indicatorFields.isEmpty() && indicatorItemsMap != null && indicatorItemsMap.containsKey(d.getIndicatorId()))
                m.setIndicator(indicatorItemsMap.get(d.getIndicatorId()));
            if (!userFields.isEmpty() && userItemsMap != null && userItemsMap.containsKey(d.getUserId()))
                m.setUser(userItemsMap.get(d.getUserId()));
            models.add(m);
        }
        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
        return models;
    }

    private Map<UUID, Tenant> collectTenants(FieldSet fields, List<IndicatorAccessEntity> data) throws MyApplicationException {
        if (fields.isEmpty() || data.isEmpty())
            return null;
        this.logger.debug("checking related - {}", Tenant.class.getSimpleName());

        Map<UUID, Tenant> itemMap;
        if (!fields.hasOtherField(this.asIndexer(Tenant._id))) {
            itemMap = this.asEmpty(
                    data.stream().map(TenantScopedBaseEntity::getTenantId).distinct().collect(Collectors.toList()),
                    x -> {
                        Tenant item = new Tenant();
                        item.setId(x);
                        return item;
                    },
                    Tenant::getId);
        } else {
            FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(Tenant._id);
            TenantQuery q = this.queryFactory.query(TenantQuery.class).authorize(this.authorize).ids(data.stream().map(TenantScopedBaseEntity::getTenantId).distinct().collect(Collectors.toList()));
            itemMap = this.builderFactory.builder(TenantBuilder.class).authorize(this.authorize).asForeignKey(q, clone, Tenant::getId);
        }
        if (!fields.hasField(Tenant._id)) {
            itemMap.forEach((id, user) -> {
                if (user != null)
                    user.setId(null);
            });
        }

        return itemMap;
    }

    private Map<UUID, User> collectUsers(FieldSet fields, List<IndicatorAccessEntity> data) throws MyApplicationException {
        if (fields.isEmpty() || data.isEmpty())
            return null;
        this.logger.debug("checking related - {}", User.class.getSimpleName());

        Map<UUID, User> itemMap;
        if (!fields.hasOtherField(this.asIndexer(User._id))) {
            itemMap = this.asEmpty(
                    data.stream().map(IndicatorAccessEntity::getUserId).distinct().collect(Collectors.toList()),
                    x -> {
                        User item = new User();
                        item.setId(x);
                        return item;
                    },
                    User::getId);
        } else {
            FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(User._id);
            UserQuery q = this.queryFactory.query(UserQuery.class).authorize(this.authorize).ids(data.stream().map(IndicatorAccessEntity::getUserId).distinct().collect(Collectors.toList()));
            itemMap = this.builderFactory.builder(UserBuilder.class).authorize(this.authorize).asForeignKey(q, clone, User::getId);
        }
        if (!fields.hasField(User._id)) {
            itemMap.forEach((id, user) -> {
                if (user != null)
                    user.setId(null);
            });
        }

        return itemMap;
    }

    private Map<UUID, Indicator> collectIndicators(FieldSet fields, List<IndicatorAccessEntity> data) throws MyApplicationException {
        if (fields.isEmpty() || data.isEmpty())
            return null;
        this.logger.debug("checking related - {}", Indicator.class.getSimpleName());

        Map<UUID, Indicator> itemMap;
        if (!fields.hasOtherField(this.asIndexer(Indicator._id))) {
            itemMap = this.asEmpty(
                    data.stream().map(IndicatorAccessEntity::getIndicatorId).distinct().collect(Collectors.toList()),
                    x -> {
                        Indicator item = new Indicator();
                        item.setId(x);
                        return item;
                    },
                    Indicator::getId);
        } else {
            FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(Indicator._id);
            IndicatorQuery q = this.queryFactory.query(IndicatorQuery.class).authorize(this.authorize).ids(data.stream().map(IndicatorAccessEntity::getIndicatorId).distinct().collect(Collectors.toList()));
            itemMap = this.builderFactory.builder(IndicatorBuilder.class).authorize(this.authorize).asForeignKey(q, clone, Indicator::getId);
        }
        if (!fields.hasField(Indicator._id)) {
            itemMap.forEach((id, user) -> {
                if (user != null)
                    user.setId(null);
            });
        }

        return itemMap;
    }

}
