package gr.cite.intelcomp.stiviewer.model.builder;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.UserInvitationEntity;
import gr.cite.intelcomp.stiviewer.data.tenant.TenantScopedBaseEntity;
import gr.cite.intelcomp.stiviewer.model.Tenant;
import gr.cite.intelcomp.stiviewer.model.UserInvitation;
import gr.cite.intelcomp.stiviewer.query.TenantQuery;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserInvitationBuilder extends BaseBuilder<UserInvitation, UserInvitationEntity> {

    private final BuilderFactory builderFactory;
    private final QueryFactory queryFactory;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    public UserInvitationBuilder(ConventionService conventionService, LoggerService logger, BuilderFactory builderFactory, QueryFactory queryFactory) {
        super(conventionService, logger);
        this.builderFactory = builderFactory;
        this.queryFactory = queryFactory;
    }

    public UserInvitationBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<UserInvitation> build(FieldSet fields, List<UserInvitationEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();

        FieldSet tenantFields = fields.extractPrefixed(this.asPrefix(UserInvitation._tenant));
        Map<UUID, Tenant> tenantMap = this.collectTenants(tenantFields, data);

        List<UserInvitation> models = new ArrayList<>(100);

        for (UserInvitationEntity d : data) {
            UserInvitation m = new UserInvitation();
            if (fields.hasField(this.asIndexer(UserInvitation._token)))
                m.setToken(d.getToken());
            if (fields.hasField(this.asIndexer(UserInvitation._email)))
                m.setEmail(d.getEmail());
            if (fields.hasField(this.asIndexer(UserInvitation._isConsumed)))
                m.setConsumed(d.getConsumed());
            if (fields.hasField(this.asIndexer(UserInvitation._expiresAt)))
                m.setExpiresAt(d.getExpiresAt());
            if (fields.hasField(this.asIndexer(UserInvitation._createdAt)))
                m.setCreatedAt(d.getCreatedAt());
            if (fields.hasField(this.asIndexer(UserInvitation._updatedAt)))
                m.setUpdatedAt(d.getUpdatedAt());
            if (fields.hasField(this.asIndexer(UserInvitation._hash)))
                m.setHash(this.hashValue(Instant.ofEpochMilli(d.getUpdatedAt().longValue())));
            if (!tenantFields.isEmpty() && tenantMap != null && tenantMap.containsKey(d.getTenantId()))
                m.setTenant(tenantMap.get(d.getTenantId()));
            models.add(m);
        }
        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
        return models;
    }

    private Map<UUID, Tenant> collectTenants(FieldSet fields, List<UserInvitationEntity> data) throws MyApplicationException {
        if (fields.isEmpty() || data.isEmpty()) return null;
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
            itemMap = itemMap.values().stream().filter(Objects::nonNull).peek(x -> x.setId(null)).collect(Collectors.toMap(Tenant::getId, Function.identity()));
        }

        return itemMap;
    }

}
