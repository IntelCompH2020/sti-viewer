package gr.cite.intelcomp.stiviewer.query;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.enums.DataAccessRequestStatus;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.data.DataAccessRequestEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntity;
import gr.cite.intelcomp.stiviewer.data.UserEntity;
import gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequest;
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
public class DataAccessRequestQuery extends QueryBase<DataAccessRequestEntity> {

    private Collection<UUID> ids;
    private Collection<DataAccessRequestStatus> statuses;
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);


    public DataAccessRequestQuery ids(UUID value) {
        this.ids = List.of(value);
        return this;
    }

    public DataAccessRequestQuery ids(UUID... value) {
        this.ids = Arrays.asList(value);
        return this;
    }

    public DataAccessRequestQuery ids(Collection<UUID> values) {
        this.ids = values;
        return this;
    }

    public DataAccessRequestQuery statuses(DataAccessRequestStatus value) {
        this.statuses = List.of(value);
        return this;
    }

    public DataAccessRequestQuery statuses(DataAccessRequestStatus... value) {
        this.statuses = Arrays.asList(value);
        return this;
    }

    public DataAccessRequestQuery statuses(Collection<DataAccessRequestStatus> values) {
        this.statuses = values;
        return this;
    }

    private final UserScope userScope;
    private final AuthorizationService authService;

    public DataAccessRequestQuery(
            UserScope userScope,
            AuthorizationService authService
    ) {
        this.userScope = userScope;
        this.authService = authService;
    }

    public DataAccessRequestQuery authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    protected Class<DataAccessRequestEntity> entityClass() {
        return DataAccessRequestEntity.class;
    }

    @Override
    protected Boolean isFalseQuery() {
        return this.isEmpty(this.ids) || this.isEmpty(this.statuses);
    }

    @Override
    protected <X, Y> Predicate applyAuthZ(QueryContext<X, Y> queryContext) {
        if (this.authorize.contains(AuthorizationFlags.None))
            return null;
        if (this.authorize.contains(AuthorizationFlags.Permission) && this.authService.authorize(Permission.BrowseDataAccessRequest))
            return null;
        UUID ownerId = null;
        if (this.authorize.contains(AuthorizationFlags.Owner))
            ownerId = this.userScope.getUserIdSafe();

        List<Predicate> predicates = new ArrayList<>();
        if (ownerId != null) {
            predicates.add(queryContext.CriteriaBuilder.equal(queryContext.Root.get(DataAccessRequestEntity._userId), ownerId));
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
        if (this.ids != null) {
            CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DataAccessRequestEntity._id));
            for (UUID item : this.ids) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.statuses != null) {
            CriteriaBuilder.In<DataAccessRequestStatus> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DataAccessRequestEntity._status));
            for (DataAccessRequestStatus item : this.statuses) inClause.value(item);
            predicates.add(inClause);
        }

        if (!predicates.isEmpty()) {
            Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
            return queryContext.CriteriaBuilder.and(predicatesArray);
        } else {
            return null;
        }
    }

    @Override
    protected DataAccessRequestEntity convert(Tuple tuple, Set<String> columns) {
        DataAccessRequestEntity item = new DataAccessRequestEntity();
        item.setId(QueryBase.convertSafe(tuple, columns, DataAccessRequestEntity._id, UUID.class));
        item.setStatus(QueryBase.convertSafe(tuple, columns, DataAccessRequestEntity._status, DataAccessRequestStatus.class));
        item.setUserId(QueryBase.convertSafe(tuple, columns, DataAccessRequestEntity._userId, UUID.class));
        item.setTenantId(QueryBase.convertSafe(tuple, columns, DataAccessRequestEntity._tenantId, UUID.class));
        item.setCreatedAt(QueryBase.convertSafe(tuple, columns, DataAccessRequestEntity._createdAt, Instant.class));
        item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, DataAccessRequestEntity._updatedAt, Instant.class));
        item.setConfig(QueryBase.convertSafe(tuple, columns, DataAccessRequestEntity._config, String.class));
        return item;
    }

    @Override
    protected String fieldNameOf(FieldResolver item) {
        if (item.match(DataAccessRequest._id))
            return DataAccessRequestEntity._id;
        else if (item.match(DataAccessRequest._status))
            return DataAccessRequestEntity._status;
        else if (item.match(DataAccessRequest._user, UserEntity._id))
            return DataAccessRequestEntity._userId;
        else if (item.prefix(DataAccessRequest._user))
            return DataAccessRequestEntity._userId;
        else if (item.prefix(DataAccessRequest._tenant, TenantEntity._id))
            return DataAccessRequestEntity._tenantId;
        else if (item.prefix(DataAccessRequest._tenant))
            return DataAccessRequestEntity._tenantId;
        else if (item.match(DataAccessRequest._createdAt))
            return DataAccessRequestEntity._createdAt;
        else if (item.match(DataAccessRequest._updatedAt))
            return DataAccessRequestEntity._updatedAt;
        else if (item.match(DataAccessRequest._config))
            return DataAccessRequestEntity._config;
        else if (item.prefix(DataAccessRequest._config))
            return DataAccessRequestEntity._config;
        else
            return null;
    }

}
