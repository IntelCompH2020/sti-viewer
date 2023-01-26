package gr.cite.intelcomp.stiviewer.query;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.data.IndicatorAccessEntity;
import gr.cite.intelcomp.stiviewer.model.IndicatorAccess;
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
public class IndicatorAccessQuery extends QueryBase<IndicatorAccessEntity> {

	private Collection<UUID> ids;
	private Collection<UUID> indicatorIds;
	private Collection<UUID> userIds;
	private Boolean hasUser;
	private Collection<UUID> tenantIds;
	private Collection<IsActive> isActives;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);


	public IndicatorAccessQuery ids(UUID value) {
		this.ids = List.of(value);
		return this;
	}

	public IndicatorAccessQuery ids(UUID... value) {
		this.ids = Arrays.asList(value);
		return this;
	}

	public IndicatorAccessQuery ids(Collection<UUID> values) {
		this.ids = values;
		return this;
	}

	public IndicatorAccessQuery isActive(IsActive value) {
		this.isActives = List.of(value);
		return this;
	}

	public IndicatorAccessQuery isActive(IsActive... value) {
		this.isActives = Arrays.asList(value);
		return this;
	}

	public IndicatorAccessQuery isActive(Collection<IsActive> values) {
		this.isActives = values;
		return this;
	}

	public IndicatorAccessQuery tenantIds(UUID value) {
		this.tenantIds = List.of(value);
		return this;
	}

	public IndicatorAccessQuery tenantIds(UUID... value) {
		this.tenantIds = Arrays.asList(value);
		return this;
	}

	public IndicatorAccessQuery tenantIds(Collection<UUID> values) {
		this.tenantIds = values;
		return this;
	}

	public IndicatorAccessQuery indicatorIds(UUID value) {
		this.indicatorIds = List.of(value);
		return this;
	}

	public IndicatorAccessQuery indicatorIds(UUID... value) {
		this.indicatorIds = Arrays.asList(value);
		return this;
	}

	public IndicatorAccessQuery indicatorIds(Collection<UUID> values) {
		this.indicatorIds = values;
		return this;
	}

	public IndicatorAccessQuery userIds(UUID value) {
		this.userIds = List.of(value);
		return this;
	}

	public IndicatorAccessQuery userIds(UUID... value) {
		this.userIds = Arrays.asList(value);
		return this;
	}

	public IndicatorAccessQuery userIds(Collection<UUID> values) {
		this.userIds = values;
		return this;
	}

	public IndicatorAccessQuery hasUser(Boolean values) {
		this.hasUser = values;
		return this;
	}

	public IndicatorAccessQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	private final UserScope userScope;
	private final AuthorizationService authService;

	public IndicatorAccessQuery(
			UserScope userScope,
			AuthorizationService authService
	) {
		this.userScope = userScope;
		this.authService = authService;
	}

	@Override
	protected Class<IndicatorAccessEntity> entityClass() {
		return IndicatorAccessEntity.class;
	}

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.indicatorIds) || this.isEmpty(this.isActives) || this.isEmpty(this.userIds) || this.isEmpty(this.ids);
	}

	@Override
	protected <X, Y> Predicate applyAuthZ(QueryContext<X, Y> queryContext) {
		if (this.authorize.contains(AuthorizationFlags.None)) return null;
		if (this.authorize.contains(AuthorizationFlags.Permission) && this.authService.authorize(Permission.BrowseIndicatorAccess)) return null;
		UUID ownerId = null;
		if (this.authorize.contains(AuthorizationFlags.Owner)) ownerId = this.userScope.getUserIdSafe();

		List<Predicate> predicates = new ArrayList<>();
		if (ownerId != null) {
			predicates.add(queryContext.CriteriaBuilder.equal(queryContext.Root.get(IndicatorAccessEntity._userId), ownerId));
		}
		if (predicates.size() > 0) {
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
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(IndicatorAccessEntity._id));
			for (UUID item : this.ids) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.indicatorIds != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(IndicatorAccessEntity._indicatorId));
			for (UUID item : this.indicatorIds) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.userIds != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(IndicatorAccessEntity._userId));
			for (UUID item : this.userIds) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.tenantIds != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(IndicatorAccessEntity._tenantId));
			for (UUID item : this.tenantIds) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.isActives != null) {
			CriteriaBuilder.In<IsActive> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(IndicatorAccessEntity._isActive));
			for (IsActive item : this.isActives) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.hasUser != null) {
			Predicate isNull = queryContext.CriteriaBuilder.isNull(queryContext.Root.get(IndicatorAccessEntity._userId));
			predicates.add(this.hasUser == true ? isNull.not() : isNull);
		}
		if (predicates.size() > 0) {
			Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
			return queryContext.CriteriaBuilder.and(predicatesArray);
		} else {
			return null;
		}
	}

	@Override
	protected IndicatorAccessEntity convert(Tuple tuple, Set<String> columns) {
		IndicatorAccessEntity item = new IndicatorAccessEntity();
		item.setId(QueryBase.convertSafe(tuple, columns, IndicatorAccessEntity._id, UUID.class));
		item.setIsActive(QueryBase.convertSafe(tuple, columns, IndicatorAccessEntity._isActive, IsActive.class));
		item.setUserId(QueryBase.convertSafe(tuple, columns, IndicatorAccessEntity._userId, UUID.class));
		item.setIndicatorId(QueryBase.convertSafe(tuple, columns, IndicatorAccessEntity._indicatorId, UUID.class));
		item.setTenantId(QueryBase.convertSafe(tuple, columns, IndicatorAccessEntity._tenantId, UUID.class));
		item.setCreatedAt(QueryBase.convertSafe(tuple, columns, IndicatorAccessEntity._createdAt, Instant.class));
		item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, IndicatorAccessEntity._updatedAt, Instant.class));
		item.setConfig(QueryBase.convertSafe(tuple, columns, IndicatorAccessEntity._config, String.class));
		return item;
	}

	@Override
	protected String fieldNameOf(FieldResolver item) {
		if (item.match(IndicatorAccess._id)) return IndicatorAccessEntity._id;
		else if (item.match(IndicatorAccess._isActive)) return IndicatorAccessEntity._isActive;
		else if (item.prefix(IndicatorAccess._user)) return IndicatorAccessEntity._userId;
		else if (item.prefix(IndicatorAccess._indicator)) return IndicatorAccessEntity._indicatorId;
		else if (item.match(IndicatorAccess._indicator)) return IndicatorAccessEntity._indicatorId;
		else if (item.prefix(IndicatorAccess._tenant)) return IndicatorAccessEntity._tenantId;
		else if (item.match(IndicatorAccess._createdAt)) return IndicatorAccessEntity._createdAt;
		else if (item.match(IndicatorAccess._updatedAt)) return IndicatorAccessEntity._updatedAt;
		else if (item.match(IndicatorAccess._config)) return IndicatorAccessEntity._config;
		else if (item.prefix(IndicatorAccess._config)) return IndicatorAccessEntity._config;
		else return null;
	}

}