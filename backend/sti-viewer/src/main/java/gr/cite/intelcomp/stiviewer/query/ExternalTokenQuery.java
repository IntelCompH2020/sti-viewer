package gr.cite.intelcomp.stiviewer.query;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.enums.ExternalTokenType;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.data.ExternalTokenEntity;
import gr.cite.intelcomp.stiviewer.model.ExternalToken;
import gr.cite.intelcomp.stiviewer.model.Tenant;
import gr.cite.intelcomp.stiviewer.model.User;
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
public class ExternalTokenQuery extends QueryBase<ExternalTokenEntity> {
    private String like;
    private Collection<UUID> ids;
    private Collection<String> tokens;
    private Collection<IsActive> isActives;
    private Collection<ExternalTokenType> types;
    private Instant expiresAtFrom;
    private Instant expiresAtTo;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    private final UserScope userScope;
    private final AuthorizationService authService;

    public ExternalTokenQuery(
            UserScope userScope,
            AuthorizationService authService
    ) {
        this.userScope = userScope;
        this.authService = authService;
    }


    public ExternalTokenQuery like(String value) {
        this.like = value;
        return this;
    }

    public ExternalTokenQuery ids(UUID value) {
        this.ids = List.of(value);
        return this;
    }

    public ExternalTokenQuery ids(UUID... value) {
        this.ids = Arrays.asList(value);
        return this;
    }

    public ExternalTokenQuery ids(Collection<UUID> values) {
        this.ids = values;
        return this;
    }

    public ExternalTokenQuery tokens(String value) {
        this.tokens = List.of(value);
        return this;
    }

    public ExternalTokenQuery tokens(String... value) {
        this.tokens = Arrays.asList(value);
        return this;
    }

    public ExternalTokenQuery tokens(Collection<String> values) {
        this.tokens = values;
        return this;
    }

    public ExternalTokenQuery isActive(IsActive value) {
        this.isActives = List.of(value);
        return this;
    }

    public ExternalTokenQuery isActive(IsActive... value) {
        this.isActives = Arrays.asList(value);
        return this;
    }

    public ExternalTokenQuery isActive(Collection<IsActive> values) {
        this.isActives = values;
        return this;
    }

    public ExternalTokenQuery types(ExternalTokenType value) {
        this.types = List.of(value);
        return this;
    }

    public ExternalTokenQuery types(ExternalTokenType... value) {
        this.types = Arrays.asList(value);
        return this;
    }

    public ExternalTokenQuery types(Collection<ExternalTokenType> value) {
        this.types = value;
        return this;
    }

    public ExternalTokenQuery authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    public ExternalTokenQuery expiresAtFrom(Instant expiresAtFrom) {
        this.expiresAtFrom = expiresAtFrom;
        return this;
    }

    public ExternalTokenQuery expiresAtTo(Instant expiresAtTo) {
        this.expiresAtTo = expiresAtTo;
        return this;
    }

    @Override
    protected Class<ExternalTokenEntity> entityClass() {
        return ExternalTokenEntity.class;
    }

    @Override
    protected Boolean isFalseQuery() {
        return this.isEmpty(this.ids) || this.isEmpty(this.isActives) || this.isEmpty(this.types);
    }

    @Override
    protected <X, Y> Predicate applyAuthZ(QueryContext<X, Y> queryContext) {
        if (this.authorize.contains(AuthorizationFlags.None))
            return null;
        if (this.authorize.contains(AuthorizationFlags.Permission) && this.authService.authorize(Permission.BrowseExternalToken))
            return null;
        UUID ownerId = null;
        if (this.authorize.contains(AuthorizationFlags.Owner))
            ownerId = this.userScope.getUserIdSafe();

        List<Predicate> predicates = new ArrayList<>();
        if (ownerId != null) {
            predicates.add(queryContext.CriteriaBuilder.equal(queryContext.Root.get(ExternalTokenEntity._ownerId), ownerId));
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
            predicates.add(queryContext.CriteriaBuilder.like(queryContext.Root.get(ExternalTokenEntity._name), this.like));
        }

        if (this.ids != null) {
            CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(ExternalTokenEntity._id));
            for (UUID item : this.ids) inClause.value(item);
            predicates.add(inClause);
        }

        if (expiresAtFrom != null) {
            predicates.add(queryContext.CriteriaBuilder.greaterThanOrEqualTo(queryContext.Root.get(ExternalTokenEntity._expiresAt), expiresAtFrom));
        }

        if (expiresAtTo != null) {
            predicates.add(queryContext.CriteriaBuilder.lessThanOrEqualTo(queryContext.Root.get(ExternalTokenEntity._expiresAt), expiresAtTo));
        }

        if (this.tokens != null) {
            CriteriaBuilder.In<String> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(ExternalTokenEntity._token));
            for (String item : this.tokens) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.isActives != null) {
            CriteriaBuilder.In<IsActive> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(ExternalTokenEntity._isActive));
            for (IsActive item : this.isActives) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.types != null) {
            CriteriaBuilder.In<ExternalTokenType> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(ExternalTokenEntity._type));
            for (ExternalTokenType item : this.types) inClause.value(item);
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
    protected ExternalTokenEntity convert(Tuple tuple, Set<String> columns) {
        ExternalTokenEntity item = new ExternalTokenEntity();
        item.setId(QueryBase.convertSafe(tuple, columns, ExternalTokenEntity._id, UUID.class));
        item.setToken(QueryBase.convertSafe(tuple, columns, ExternalTokenEntity._token, String.class));
        item.setDefinition(QueryBase.convertSafe(tuple, columns, ExternalTokenEntity._definition, String.class));
        item.setOwnerId(QueryBase.convertSafe(tuple, columns, ExternalTokenEntity._ownerId, UUID.class));
        item.setExpiresAt(QueryBase.convertSafe(tuple, columns, ExternalTokenEntity._expiresAt, Instant.class));
        item.setName(QueryBase.convertSafe(tuple, columns, ExternalTokenEntity._name, String.class));
        item.setTenantId(QueryBase.convertSafe(tuple, columns, ExternalTokenEntity._tenantId, UUID.class));
        item.setCreatedAt(QueryBase.convertSafe(tuple, columns, ExternalTokenEntity._createdAt, Instant.class));
        item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, ExternalTokenEntity._updatedAt, Instant.class));
        item.setIsActive(QueryBase.convertSafe(tuple, columns, ExternalTokenEntity._isActive, IsActive.class));
        item.setType(QueryBase.convertSafe(tuple, columns, ExternalTokenEntity._type, ExternalTokenType.class));
        return item;
    }

    @Override
    protected String fieldNameOf(FieldResolver item) {
        if (item.match(ExternalToken._id))
            return ExternalTokenEntity._id;
        else if (item.match(ExternalToken._expiresAt))
            return ExternalTokenEntity._expiresAt;
        else if (item.match(ExternalToken._name))
            return ExternalTokenEntity._name;
        else if (item.prefix(ExternalToken._tenant, Tenant._id))
            return ExternalTokenEntity._tenantId;
        else if (item.prefix(ExternalToken._tenant))
            return ExternalTokenEntity._tenantId;
        else if (item.prefix(ExternalToken._owner, User._id))
            return ExternalTokenEntity._ownerId;
        else if (item.prefix(ExternalToken._owner))
            return ExternalTokenEntity._ownerId;
        else if (item.match(ExternalToken._isActive))
            return ExternalTokenEntity._isActive;
        else if (item.match(ExternalToken._type))
            return ExternalTokenEntity._type;
        else if (item.match(ExternalToken._createdAt))
            return ExternalTokenEntity._createdAt;
        else if (item.match(ExternalToken._updatedAt))
            return ExternalTokenEntity._updatedAt;
        else if (item.match(ExternalToken._hash))
            return ExternalTokenEntity._updatedAt;
        else
            return null;
    }
}
