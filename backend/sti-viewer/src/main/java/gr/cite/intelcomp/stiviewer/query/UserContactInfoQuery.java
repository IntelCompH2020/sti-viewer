package gr.cite.intelcomp.stiviewer.query;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.enums.UserContactType;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.data.TenantEntity;
import gr.cite.intelcomp.stiviewer.data.UserContactInfoEntity;
import gr.cite.intelcomp.stiviewer.data.UserEntity;
import gr.cite.intelcomp.stiviewer.model.Tenant;
import gr.cite.intelcomp.stiviewer.model.User;
import gr.cite.intelcomp.stiviewer.model.UserContactInfo;
import gr.cite.intelcomp.stiviewer.model.persist.UserContactInfoPersist;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryBase;
import gr.cite.tools.data.query.QueryContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Subquery;
import java.time.Instant;
import java.util.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContactInfoQuery extends QueryBase<UserContactInfoEntity> {

	private Collection<UserContactInfoPersist.ID> ids;
	private Collection<UUID> userIds;
	private Collection<UUID> tenantIds;
	private Collection<IsActive> isActives;
	private Collection<UserContactType> type;
	private UserQuery userQuery;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	private final UserScope userScope;
	private final AuthorizationService authService;

	public UserContactInfoQuery(
			UserScope userScope,
			AuthorizationService authService
	) {
		this.userScope = userScope;
		this.authService = authService;
	}

	public UserContactInfoQuery ids(UserContactInfoPersist.ID value) {
		this.ids = List.of(value);
		return this;
	}

	public UserContactInfoQuery ids(UserContactInfoPersist.ID... value) {
		this.ids = Arrays.asList(value);
		return this;
	}

	public UserContactInfoQuery ids(Collection<UserContactInfoPersist.ID> values) {
		this.ids = values;
		return this;
	}

	public UserContactInfoQuery userIds(UUID value) {
		this.userIds = List.of(value);
		return this;
	}

	public UserContactInfoQuery userIds(UUID... value) {
		this.userIds = Arrays.asList(value);
		return this;
	}

	public UserContactInfoQuery userIds(Collection<UUID> values) {
		this.userIds = values;
		return this;
	}

	public UserContactInfoQuery tenantIds(UUID value) {
		this.tenantIds = List.of(value);
		return this;
	}

	public UserContactInfoQuery tenantIds(UUID... value) {
		this.tenantIds = Arrays.asList(value);
		return this;
	}

	public UserContactInfoQuery tenantIds(Collection<UUID> values) {
		this.tenantIds = values;
		return this;
	}

	public UserContactInfoQuery isActive(IsActive value) {
		this.isActives = List.of(value);
		return this;
	}

	public UserContactInfoQuery isActive(IsActive... value) {
		this.isActives = Arrays.asList(value);
		return this;
	}

	public UserContactInfoQuery isActive(Collection<IsActive> values) {
		this.isActives = values;
		return this;
	}

	public UserContactInfoQuery type(UserContactType value) {
		this.type = List.of(value);
		return this;
	}

	public UserContactInfoQuery type(UserContactType... value) {
		this.type = Arrays.asList(value);
		return this;
	}

	public UserContactInfoQuery type(Collection<UserContactType> values) {
		this.type = values;
		return this;
	}
	
	public UserContactInfoQuery userSubQuery(UserQuery subQuery) {
		this.userQuery = subQuery;
		return this;
	}

	public UserContactInfoQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.userIds) || this.isEmpty(this.tenantIds) || this.isEmpty(this.isActives) || this.isEmpty(this.type) || this.isFalseQuery(this.userQuery);
	}

	@Override
	protected Class<UserContactInfoEntity> entityClass() {
		return UserContactInfoEntity.class;
	}

	@Override
	protected <X, Y> Predicate applyAuthZ(QueryContext<X, Y> queryContext) {
		if (this.authorize.contains(AuthorizationFlags.None))
			return null;
		if (this.authorize.contains(AuthorizationFlags.Permission) && this.authService.authorize(Permission.BrowseUserContactInfo))
			return null;
		UUID ownerId = null;
		if (this.authorize.contains(AuthorizationFlags.Owner))
			ownerId = this.userScope.getUserIdSafe();

		List<Predicate> predicates = new ArrayList<>();
		if (ownerId != null) {
			predicates.add(queryContext.CriteriaBuilder.equal(queryContext.Root.get(UserContactInfoEntity._userId), ownerId));
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

		if (this.userIds != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserContactInfoEntity._userId));
			for (UUID item : this.userIds) inClause.value(item);
			predicates.add(inClause);
		}

		if (this.tenantIds != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserContactInfoEntity._tenantId));
			for (UUID item : this.tenantIds) inClause.value(item);
			predicates.add(inClause);
		}

		if (this.isActives != null) {
			CriteriaBuilder.In<IsActive> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserContactInfoEntity._isActive));
			for (IsActive item : this.isActives) inClause.value(item);
			predicates.add(inClause);
		}

		if (this.type != null) {
			CriteriaBuilder.In<UserContactType> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserContactInfoEntity._type));
			for (UserContactType item : this.type) inClause.value(item);
			predicates.add(inClause);
		}

		if (this.userQuery != null) {
			Subquery<UserEntity> subQuery = queryContext.Query.subquery(this.userQuery.entityClass());
			this.applySubQuery(this.userQuery, queryContext.CriteriaBuilder, subQuery);
			predicates.add(queryContext.CriteriaBuilder.in(queryContext.Root.get(UserContactInfoEntity._userId)).value(subQuery));
		}

		if (!predicates.isEmpty()) {
			Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
			return queryContext.CriteriaBuilder.and(predicatesArray);
		} else {
			return null;
		}
	}

	@Override
	protected String fieldNameOf(FieldResolver item) {
		if (item.match(UserContactInfo._tenant, Tenant._id))
			return UserContactInfoEntity._tenantId;
		else if (item.prefix(UserContactInfo._tenant))
			return UserContactInfoEntity._tenantId;
		else if (item.match(UserContactInfo._type))
			return UserContactInfoEntity._type;
		else if (item.match(UserContactInfo._value))
			return UserContactInfoEntity._value;
		else if (item.match(UserContactInfo._isActive))
			return UserContactInfoEntity._isActive;
		else if (item.match(UserContactInfo._createdAt))
			return UserContactInfoEntity._createdAt;
		else if (item.match(UserContactInfo._updatedAt))
			return UserContactInfoEntity._updatedAt;
		else if (item.match(UserContactInfo._hash))
			return UserContactInfoEntity._updatedAt;
		else if (item.match(UserContactInfo._user, User._id))
			return UserContactInfoEntity._userId;
		else if (item.prefix(UserContactInfo._user))
			return UserContactInfoEntity._userId;
		else
			return null;
	}

	@Override
	protected UserContactInfoEntity convert(Tuple tuple, Set<String> columns) {
		UserContactInfoEntity item = new UserContactInfoEntity();
		item.setType(QueryBase.convertSafe(tuple, columns, UserContactInfoEntity._type, UserContactType.class));
		item.setValue(QueryBase.convertSafe(tuple, columns, UserContactInfoEntity._value, String.class));
		item.setUserId(QueryBase.convertSafe(tuple, columns, UserContactInfoEntity._userId, UUID.class));
		item.setTenantId(QueryBase.convertSafe(tuple, columns, UserContactInfoEntity._tenantId, UUID.class));
		item.setCreatedAt(QueryBase.convertSafe(tuple, columns, UserContactInfoEntity._createdAt, Instant.class));
		item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, UserContactInfoEntity._updatedAt, Instant.class));
		item.setIsActive(QueryBase.convertSafe(tuple, columns, UserContactInfoEntity._isActive, IsActive.class));
		return item;
	}

}
