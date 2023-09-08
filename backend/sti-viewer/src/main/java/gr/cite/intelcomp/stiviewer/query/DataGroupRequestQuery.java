package gr.cite.intelcomp.stiviewer.query;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.enums.DataGroupRequestStatus;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.data.DataGroupRequestEntity;
import gr.cite.intelcomp.stiviewer.data.DatasetEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntity;
import gr.cite.intelcomp.stiviewer.data.UserEntity;
import gr.cite.intelcomp.stiviewer.model.datagrouprequest.DataGroupRequest;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryBase;
import gr.cite.tools.data.query.QueryContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataGroupRequestQuery extends QueryBase<DataGroupRequestEntity> {

    private String like;
    private Collection<UUID> ids;
    private Collection<String> groupHashes;
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);
    private Collection<IsActive> isActives;
    private Collection<DataGroupRequestStatus> status;
    private Instant createdAfter;


    public DataGroupRequestQuery like(String value) {
        this.like = value;
        return this;
    }

    public DataGroupRequestQuery ids(UUID value) {
        this.ids = List.of(value);
        return this;
    }

    public DataGroupRequestQuery ids(UUID... value) {
        this.ids = Arrays.asList(value);
        return this;
    }

    public DataGroupRequestQuery ids(Collection<UUID> values) {
        this.ids = values;
        return this;
    }

    public DataGroupRequestQuery groupHashes(String value) {
        this.groupHashes = List.of(value);
        return this;
    }

    public DataGroupRequestQuery groupHashes(String... value) {
        this.groupHashes = Arrays.asList(value);
        return this;
    }

    public DataGroupRequestQuery groupHashes(Collection<String> values) {
        this.groupHashes = values;
        return this;
    }

    public DataGroupRequestQuery isActive(IsActive value) {
        this.isActives = List.of(value);
        return this;
    }

    public DataGroupRequestQuery isActive(IsActive... value) {
        this.isActives = Arrays.asList(value);
        return this;
    }

    public DataGroupRequestQuery isActive(Collection<IsActive> values) {
        this.isActives = values;
        return this;
    }

    public DataGroupRequestQuery status(DataGroupRequestStatus value) {
        this.status = List.of(value);
        return this;
    }

    public DataGroupRequestQuery status(DataGroupRequestStatus... value) {
        this.status = Arrays.asList(value);
        return this;
    }

    public DataGroupRequestQuery status(Collection<DataGroupRequestStatus> values) {
        this.status = values;
        return this;
    }

    public DataGroupRequestQuery createdAfter(Instant createdAfter) {
        this.createdAfter = createdAfter;
        return this;
    }

    private final UserScope userScope;
    private final AuthorizationService authService;

    public DataGroupRequestQuery(
            UserScope userScope,
            AuthorizationService authService
    ) {
        this.userScope = userScope;
        this.authService = authService;
    }

    public DataGroupRequestQuery authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    protected Class<DataGroupRequestEntity> entityClass() {
        return DataGroupRequestEntity.class;
    }

    @Override
    protected Boolean isFalseQuery() {
        return this.isEmpty(this.ids) || this.isEmpty(this.groupHashes) || this.isEmpty(this.status) || this.isEmpty(this.isActives);
    }

    @Override
    protected <X, Y> Predicate applyAuthZ(QueryContext<X, Y> queryContext) {
        if (this.authorize.contains(AuthorizationFlags.None))
            return null;
        if (this.authorize.contains(AuthorizationFlags.Permission) && this.authService.authorize(Permission.BrowseDataGroupRequest))
            return null;
        UUID ownerId = null;
        if (this.authorize.contains(AuthorizationFlags.Owner))
            ownerId = this.userScope.getUserIdSafe();

        List<Predicate> predicates = new ArrayList<>();
        if (ownerId != null) {
            predicates.add(queryContext.CriteriaBuilder.equal(queryContext.Root.get(DataGroupRequestEntity._userId), ownerId));
        }

        if (!predicates.isEmpty()) {
            Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
            return queryContext.CriteriaBuilder.and(predicatesArray);
        } else {
            return queryContext.CriteriaBuilder.or(); //Creates a false query
        }
    }

    @Override
    protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
        List<Predicate> predicates = new ArrayList<>();
        if (this.like != null && !this.like.isEmpty()) {
            predicates.add(queryContext.CriteriaBuilder.like(queryContext.Root.get(DataGroupRequestEntity._name), this.like));
        }

        if (this.ids != null) {
            CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DataGroupRequestEntity._id));
            for (UUID item : this.ids) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.groupHashes != null) {
            CriteriaBuilder.In<String> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DataGroupRequestEntity._groupHash));
            for (String item : this.groupHashes) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.isActives != null) {
            CriteriaBuilder.In<IsActive> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DataGroupRequestEntity._isActive));
            for (IsActive item : this.isActives) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.status != null) {
            CriteriaBuilder.In<DataGroupRequestStatus> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DataGroupRequestEntity._status));
            for (DataGroupRequestStatus item : this.status) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.createdAfter != null) {
            predicates.add(queryContext.CriteriaBuilder.greaterThan(queryContext.Root.get(DataGroupRequestEntity._createdAt), this.createdAfter));
        }

        if (!predicates.isEmpty()) {
            Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
            return queryContext.CriteriaBuilder.and(predicatesArray);
        } else {
            return null;
        }
    }

    @Override
    protected DataGroupRequestEntity convert(Tuple tuple, Set<String> columns) {
        DataGroupRequestEntity item = new DataGroupRequestEntity();
        item.setId(QueryBase.convertSafe(tuple, columns, DataGroupRequestEntity._id, UUID.class));
        item.setName(QueryBase.convertSafe(tuple, columns, DataGroupRequestEntity._name, String.class));
        item.setStatus(QueryBase.convertSafe(tuple, columns, DataGroupRequestEntity._status, DataGroupRequestStatus.class));
        item.setUserId(QueryBase.convertSafe(tuple, columns, DataGroupRequestEntity._userId, UUID.class));
        item.setTenantId(QueryBase.convertSafe(tuple, columns, DataGroupRequestEntity._tenantId, UUID.class));
        item.setCreatedAt(QueryBase.convertSafe(tuple, columns, DataGroupRequestEntity._createdAt, Instant.class));
        item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, DataGroupRequestEntity._updatedAt, Instant.class));
        item.setConfig(QueryBase.convertSafe(tuple, columns, DataGroupRequestEntity._config, String.class));
        item.setGroupHash(QueryBase.convertSafe(tuple, columns, DataGroupRequestEntity._groupHash, String.class));
        item.setIsActive(QueryBase.convertSafe(tuple, columns, DatasetEntity._isActive, IsActive.class));
        return item;
    }

    @Override
    protected String fieldNameOf(FieldResolver item) {
        if (item.match(DataGroupRequest._id))
            return DataGroupRequestEntity._id;
        else if (item.match(DataGroupRequest._status))
            return DataGroupRequestEntity._status;
        else if (item.match(DataGroupRequest._isActive))
            return DataGroupRequestEntity._isActive;
        else if (item.match(DataGroupRequest._user, UserEntity._id))
            return DataGroupRequestEntity._userId;
        else if (item.prefix(DataGroupRequest._user))
            return DataGroupRequestEntity._userId;
        else if (item.prefix(DataGroupRequest._tenant, TenantEntity._id))
            return DataGroupRequestEntity._tenantId;
        else if (item.prefix(DataGroupRequest._tenant))
            return DataGroupRequestEntity._tenantId;
        else if (item.match(DataGroupRequest._createdAt))
            return DataGroupRequestEntity._createdAt;
        else if (item.match(DataGroupRequest._updatedAt))
            return DataGroupRequestEntity._updatedAt;
        else if (item.match(DataGroupRequest._config))
            return DataGroupRequestEntity._config;
        else if (item.prefix(DataGroupRequest._config))
            return DataGroupRequestEntity._config;
        else if (item.match(DataGroupRequest._groupHash))
            return DataGroupRequestEntity._groupHash;
        else if (item.match(DataGroupRequest._name))
            return DataGroupRequestEntity._name;
        else if (item.match(DataGroupRequest._hash))
            return DatasetEntity._updatedAt;
        else
            return null;
    }

}
