package gr.cite.user.query;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.user.authorization.AuthorizationFlags;
import gr.cite.user.authorization.Permission;
import gr.cite.user.common.enums.IsActive;
import gr.cite.user.common.scope.user.UserScope;
import gr.cite.user.data.TenantUserEntity;
import gr.cite.user.data.UserEntity;
import gr.cite.user.model.Tenant;
import gr.cite.user.model.TenantUser;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryBase;
import gr.cite.tools.data.query.QueryContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Subquery;
import java.time.Instant;
import java.util.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TenantUserQuery extends QueryBase<TenantUserEntity> {

	private Collection<UUID> ids;
	private Collection<UUID> userIds;
	private Collection<UUID> tenantIds;
	private Collection<IsActive> isActives;
	private UserQuery userQuery;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);
	private final UserScope userScope;
	private final AuthorizationService authService;

	public TenantUserQuery(
			UserScope userScope,
			AuthorizationService authService
	) {
		this.userScope = userScope;
		this.authService = authService;
	}

	public TenantUserQuery ids(UUID value) {
		this.ids = List.of(value);
		return this;
	}

	public TenantUserQuery ids(UUID... value) {
		this.ids = Arrays.asList(value);
		return this;
	}

	public TenantUserQuery ids(Collection<UUID> values) {
		this.ids = values;
		return this;
	}

	public TenantUserQuery userIds(UUID value) {
		this.userIds = List.of(value);
		return this;
	}

	public TenantUserQuery userIds(UUID... value) {
		this.userIds = Arrays.asList(value);
		return this;
	}

	public TenantUserQuery userIds(Collection<UUID> values) {
		this.userIds = values;
		return this;
	}

	public TenantUserQuery tenantIds(UUID value) {
		this.tenantIds = List.of(value);
		return this;
	}

	public TenantUserQuery tenantIds(UUID... value) {
		this.tenantIds = Arrays.asList(value);
		return this;
	}

	public TenantUserQuery tenantIds(Collection<UUID> values) {
		this.tenantIds = values;
		return this;
	}

	public TenantUserQuery isActive(IsActive value) {
		this.isActives = List.of(value);
		return this;
	}

	public TenantUserQuery isActive(IsActive... value) {
		this.isActives = Arrays.asList(value);
		return this;
	}

	public TenantUserQuery isActive(Collection<IsActive> values) {
		this.isActives = values;
		return this;
	}

	public TenantUserQuery userSubQuery(UserQuery subQuery) {
		this.userQuery = subQuery;
		return this;
	}

	public TenantUserQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	protected Class<TenantUserEntity> entityClass() {
		return TenantUserEntity.class;
	}

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.ids) || this.isEmpty(this.userIds) || this.isEmpty(this.tenantIds) || this.isEmpty(this.isActives) || this.isFalseQuery(this.userQuery);
	}


	@Override
	protected <X, Y> Predicate applyAuthZ(QueryContext<X, Y> queryContext) {
		if (this.authorize.contains(AuthorizationFlags.None)) return null;
		if (this.authorize.contains(AuthorizationFlags.Permission) && this.authService.authorize(Permission.BrowseTenantUser)) return null;
		UUID ownerId = null;
		if (this.authorize.contains(AuthorizationFlags.Owner)) ownerId = this.userScope.getUserIdSafe();

		List<Predicate> predicates = new ArrayList<>();
//		if (ownerId != null) {
//			predicates.add(queryContext.CriteriaBuilder.equal(queryContext.Root.get(TenantRequestEntity._forUserId), ownerId));
//		}
		if (predicates.size() > 0) {
			Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
			return queryContext.CriteriaBuilder.and(predicatesArray);
		} else {
			return queryContext.CriteriaBuilder.or(); //Creates a false gr.cite.user.query
		}
	}

	@Override
	protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
		List<Predicate> predicates = new ArrayList<>();
		if (this.ids != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(TenantUserEntity._id));
			for (UUID item : this.ids) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.userIds != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(TenantUserEntity._userId));
			for (UUID item : this.userIds) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.tenantIds != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(TenantUserEntity._tenantId));
			for (UUID item : this.tenantIds) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.isActives != null) {
			CriteriaBuilder.In<IsActive> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(TenantUserEntity._isActive));
			for (IsActive item : this.isActives) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.userQuery != null) {
			Subquery<UserEntity> subQuery = queryContext.Query.subquery(this.userQuery.entityClass());
			this.applySubQuery(this.userQuery, queryContext.CriteriaBuilder, subQuery);
			predicates.add(queryContext.CriteriaBuilder.in(queryContext.Root.get(TenantUserEntity._userId)).value(subQuery));
		}
		if (predicates.size() > 0) {
			Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
			return queryContext.CriteriaBuilder.and(predicatesArray);
		} else {
			return null;
		}
	}

	@Override
	protected TenantUserEntity convert(Tuple tuple, Set<String> columns) {
		TenantUserEntity item = new TenantUserEntity();
		item.setId(QueryBase.convertSafe(tuple, columns, TenantUserEntity._id, UUID.class));
		item.setUserId(QueryBase.convertSafe(tuple, columns, TenantUserEntity._userId, UUID.class));
		item.setTenantId(QueryBase.convertSafe(tuple, columns, TenantUserEntity._tenantId, UUID.class));
		item.setCreatedAt(QueryBase.convertSafe(tuple, columns, TenantUserEntity._createdAt, Instant.class));
		item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, TenantUserEntity._updatedAt, Instant.class));
		item.setIsActive(QueryBase.convertSafe(tuple, columns, TenantUserEntity._isActive, IsActive.class));
		return item;
	}

	@Override
	protected String fieldNameOf(FieldResolver item) {
		if (item.match(TenantUser._id)) return TenantUserEntity._id;
		else if (item.match(TenantUser._tenant, Tenant._id)) return TenantUserEntity._tenantId;
		else if (item.prefix(TenantUser._tenant)) return TenantUserEntity._tenantId;
		else if (item.match(TenantUser._isActive)) return TenantUserEntity._isActive;
		else if (item.match(TenantUser._createdAt)) return TenantUserEntity._createdAt;
		else if (item.match(TenantUser._updatedAt)) return TenantUserEntity._updatedAt;
		else if (item.match(TenantUser._hash)) return TenantUserEntity._updatedAt;
		else if (item.match(TenantUser._user, UserEntity._id)) return TenantUserEntity._userId;
		else if (item.prefix(TenantUser._user)) return TenantUserEntity._userId;
		else return null;
	}
}
