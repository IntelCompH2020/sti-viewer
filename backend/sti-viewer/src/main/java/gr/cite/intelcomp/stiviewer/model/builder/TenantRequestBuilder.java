package gr.cite.intelcomp.stiviewer.model.builder;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.TenantRequestEntity;
import gr.cite.intelcomp.stiviewer.model.Tenant;
import gr.cite.intelcomp.stiviewer.model.TenantRequest;
import gr.cite.intelcomp.stiviewer.model.User;
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
public class TenantRequestBuilder extends BaseBuilder<TenantRequest, TenantRequestEntity> {
    private final BuilderFactory builderFactory;
    private final QueryFactory queryFactory;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public TenantRequestBuilder(
            ConventionService conventionService,
            BuilderFactory builderFactory,
            QueryFactory queryFactory
    ) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DetailItemBuilder.class)));
        this.builderFactory = builderFactory;
        this.queryFactory = queryFactory;
    }

    public TenantRequestBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<TenantRequest> build(FieldSet fields, List<TenantRequestEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();

        FieldSet forUserFields = fields.extractPrefixed(this.asPrefix(TenantRequest._forUser));
        Map<UUID, User> forUserMap = this.collectUsers(forUserFields, data);

        FieldSet assignedTenantFields = fields.extractPrefixed(this.asPrefix(TenantRequest._assignedTenant));
        Map<UUID, Tenant> assignedTenantMap = this.collectAssignedTenants(assignedTenantFields, data);

        List<TenantRequest> models = new ArrayList<>(100);

        for (TenantRequestEntity d : data) {
            TenantRequest m = new TenantRequest();
            if (fields.hasField(this.asIndexer(TenantRequest._id)))
                m.setId(d.getId());
            if (fields.hasField(this.asIndexer(TenantRequest._hash)))
                m.setHash(this.hashValue(d.getUpdatedAt()));
            if (fields.hasField(this.asIndexer(TenantRequest._message)))
                m.setMessage(d.getMessage());
            if (fields.hasField(this.asIndexer(TenantRequest._status)))
                m.setStatus(d.getStatus());
            if (fields.hasField(this.asIndexer(TenantRequest._email)))
                m.setEmail(d.getEmail());
            if (fields.hasField(this.asIndexer(TenantRequest._createdAt)))
                m.setCreatedAt(d.getCreatedAt());
            if (fields.hasField(this.asIndexer(TenantRequest._updatedAt)))
                m.setUpdatedAt(d.getUpdatedAt());
            if (!forUserFields.isEmpty() && forUserMap != null && forUserMap.containsKey(d.getForUserId()))
                m.setForUser(forUserMap.get(d.getForUserId()));
            if (!assignedTenantFields.isEmpty() && assignedTenantMap != null && d.getAssignedTenantId() != null && assignedTenantMap.containsKey(d.getAssignedTenantId()))
                m.setAssignedTenant(assignedTenantMap.get(d.getAssignedTenantId()));
            models.add(m);
        }
        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
        return models;
    }

    private Map<UUID, User> collectUsers(FieldSet fields, List<TenantRequestEntity> data) throws MyApplicationException {
        if (fields.isEmpty() || data.isEmpty()) return null;
        this.logger.debug("checking related - {}", User.class.getSimpleName());

        Map<UUID, User> itemMap;
        if (!fields.hasOtherField(this.asIndexer(User._id))) {
            itemMap = this.asEmpty(
                    data.stream().map(TenantRequestEntity::getForUserId).distinct().collect(Collectors.toList()),
                    x -> {
                        User item = new User();
                        item.setId(x);
                        return item;
                    },
                    User::getId);
        } else {
            FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(User._id);
            UserQuery q = this.queryFactory.query(UserQuery.class).authorize(this.authorize).ids(data.stream().map(TenantRequestEntity::getForUserId).distinct().collect(Collectors.toList()));
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

    private Map<UUID, Tenant> collectAssignedTenants(FieldSet fields, List<TenantRequestEntity> datas) throws MyApplicationException {
        if (fields.isEmpty() || datas.isEmpty()) return null;
        this.logger.debug("checking related - {}", Tenant.class.getSimpleName());

        Map<UUID, Tenant> itemMap = null;
        if (!fields.hasOtherField(this.asIndexer(Tenant._id))) {
            itemMap = this.asEmpty(
                    datas.stream().map(TenantRequestEntity::getAssignedTenantId).filter(Objects::nonNull).distinct().collect(Collectors.toList()),
                    x -> {
                        Tenant item = new Tenant();
                        item.setId(x);
                        return item;
                    },
                    Tenant::getId);
        } else {
            FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(Tenant._id);
            TenantQuery q = this.queryFactory.query(TenantQuery.class).authorize(this.authorize).ids(datas.stream().map(TenantRequestEntity::getAssignedTenantId).filter(Objects::nonNull).distinct().collect(Collectors.toList()));
            itemMap = this.builderFactory.builder(TenantBuilder.class).authorize(this.authorize).asForeignKey(q, clone, Tenant::getId);
        }
        if (!fields.hasField(Tenant._id)) {
            itemMap.forEach((id, tenant) -> {
                if (tenant != null)
                    tenant.setId(null);
            });
        }

        return itemMap;
    }
}
