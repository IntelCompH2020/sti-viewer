package gr.cite.intelcomp.stiviewer.model.builder;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.UserEntity;
import gr.cite.intelcomp.stiviewer.model.IndicatorAccess;
import gr.cite.intelcomp.stiviewer.model.TenantUser;
import gr.cite.intelcomp.stiviewer.model.User;
import gr.cite.intelcomp.stiviewer.query.IndicatorAccessQuery;
import gr.cite.intelcomp.stiviewer.query.TenantUserQuery;
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
public class UserBuilder extends BaseBuilder<User, UserEntity> {

    private final BuilderFactory builderFactory;
    private final QueryFactory queryFactory;
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public UserBuilder(
            ConventionService conventionService,
            BuilderFactory builderFactory,
            QueryFactory queryFactory
    ) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(UserBuilder.class)));
        this.builderFactory = builderFactory;
        this.queryFactory = queryFactory;
    }

    public UserBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<User> build(FieldSet fields, List<UserEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || fields.isEmpty())
            return new ArrayList<>();

        FieldSet tenantUsersFields = fields.extractPrefixed(this.asPrefix(User._tenantUsers));
        Map<UUID, List<TenantUser>> tenantUsersMap = this.collectTenantUsers(tenantUsersFields, data);

        FieldSet indicatorAccessesFields = fields.extractPrefixed(this.asPrefix(User._indicatorAccesses));
        Map<UUID, List<IndicatorAccess>> indicatorAccessesMap = this.collectIndicatorAccesses(indicatorAccessesFields, data);

        List<User> models = new ArrayList<>(100);

        if (data == null)
            return models;
        for (UserEntity d : data) {
            User m = new User();
            if (fields.hasField(this.asIndexer(User._id)))
                m.setId(d.getId());
            if (fields.hasField(this.asIndexer(User._hash)))
                m.setHash(this.hashValue(d.getUpdatedAt()));
            if (fields.hasField(this.asIndexer(User._firstName)))
                m.setFirstName(d.getFirstName());
            if (fields.hasField(this.asIndexer(User._lastName)))
                m.setLastName(d.getLastName());
            if (fields.hasField(this.asIndexer(User._timezone)))
                m.setTimezone(d.getTimezone());
            if (fields.hasField(this.asIndexer(User._culture)))
                m.setCulture(d.getCulture());
            if (fields.hasField(this.asIndexer(User._language)))
                m.setLanguage(d.getLanguage());
            if (fields.hasField(this.asIndexer(User._subjectId)))
                m.setSubjectId(d.getSubjectId());
            if (fields.hasField(this.asIndexer(User._createdAt)))
                m.setCreatedAt(d.getCreatedAt());
            if (fields.hasField(this.asIndexer(User._updatedAt)))
                m.setUpdatedAt(d.getUpdatedAt());
            if (fields.hasField(this.asIndexer(User._isActive)))
                m.setIsActive(d.getIsActive());
            if (!tenantUsersFields.isEmpty() && tenantUsersMap != null && tenantUsersMap.containsKey(d.getId()))
                m.setTenantUsers(tenantUsersMap.get(d.getId()));
            if (!indicatorAccessesFields.isEmpty() && indicatorAccessesMap != null && indicatorAccessesMap.containsKey(d.getId()))
                m.setIndicatorAccesses(indicatorAccessesMap.get(d.getId()));
            models.add(m);
        }
        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
        return models;
    }

    private Map<UUID, List<TenantUser>> collectTenantUsers(FieldSet fields, List<UserEntity> datas) throws MyApplicationException {
        if (fields.isEmpty() || datas.isEmpty())
            return null;
        this.logger.debug("checking related - {}", TenantUser.class.getSimpleName());

        Map<UUID, List<TenantUser>> itemMap;
        FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(this.asIndexer(TenantUser._user, User._id));
        TenantUserQuery query = this.queryFactory.query(TenantUserQuery.class).authorize(this.authorize).userIds(datas.stream().map(UserEntity::getId).distinct().collect(Collectors.toList()));
        itemMap = this.builderFactory.builder(TenantUserBuilder.class).authorize(this.authorize).asMasterKey(query, clone, x -> x.getUser().getId());

        if (!fields.hasField(this.asIndexer(TenantUser._user, User._id))) {
            itemMap.forEach((id, tenantUsers) -> {
                tenantUsers.forEach(tenantUser -> {
                    if (tenantUser != null && tenantUser.getUser() != null)
                        tenantUser.getUser().setId(null);
                });
            });
        }
        return itemMap;
    }

    private Map<UUID, List<IndicatorAccess>> collectIndicatorAccesses(FieldSet fields, List<UserEntity> datas) throws MyApplicationException {
        if (fields.isEmpty() || datas.isEmpty())
            return null;
        this.logger.debug("checking related - {}", IndicatorAccess.class.getSimpleName());

        Map<UUID, List<IndicatorAccess>> itemMap;
        FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(this.asIndexer(IndicatorAccess._user, User._id));
        IndicatorAccessQuery query = this.queryFactory.query(IndicatorAccessQuery.class).authorize(this.authorize).userIds(datas.stream().map(UserEntity::getId).distinct().collect(Collectors.toList()));
        itemMap = this.builderFactory.builder(IndicatorAccessBuilder.class).authorize(this.authorize).asMasterKey(query, clone, x -> x.getUser().getId());

        if (!fields.hasField(this.asIndexer(IndicatorAccess._user, User._id))) {
            itemMap.forEach((id, indicatorAccesses) -> {
                indicatorAccesses.forEach(indicatorAccess -> {
                    if (indicatorAccess != null && indicatorAccess.getUser() != null)
                        indicatorAccess.getUser().setId(null);
                });
            });
        }
        return itemMap;
    }
}
