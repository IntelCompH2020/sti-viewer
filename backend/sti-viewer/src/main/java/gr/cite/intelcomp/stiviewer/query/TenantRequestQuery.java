package gr.cite.intelcomp.stiviewer.query;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.enums.TenantRequestStatus;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.data.TenantRequestEntity;
import gr.cite.intelcomp.stiviewer.model.TenantRequest;
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
public class TenantRequestQuery extends QueryBase<TenantRequestEntity> {

	private String like;
	private Collection<UUID> ids;
	private Collection<UUID> forUserIds;
	private Collection<UUID> assignedTenantIds;
	private Collection<TenantRequestStatus> tenantRequestStatuses;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	private final UserScope userScope;
	private final AuthorizationService authService;

	public TenantRequestQuery(
			UserScope userScope,
			AuthorizationService authService
	) {
		this.userScope = userScope;
		this.authService = authService;
	}

	public TenantRequestQuery like(String value) {
		this.like = value;
		return this;
	}

	public TenantRequestQuery ids(UUID value) {
		this.ids = List.of(value);
		return this;
	}

	public TenantRequestQuery ids(UUID... value) {
		this.ids = Arrays.asList(value);
		return this;
	}

	public TenantRequestQuery ids(Collection<UUID> values) {
		this.ids = values;
		return this;
	}

	public TenantRequestQuery assignedTenantIds(UUID value) {
		this.assignedTenantIds = List.of(value);
		return this;
	}

	public TenantRequestQuery assignedTenantIds(UUID... value) {
		this.assignedTenantIds = Arrays.asList(value);
		return this;
	}

	public TenantRequestQuery assignedTenantIds(Collection<UUID> values) {
		this.assignedTenantIds = values;
		return this;
	}

	public TenantRequestQuery forUserIds(UUID value) {
		this.forUserIds = List.of(value);
		return this;
	}

	public TenantRequestQuery forUserIds(UUID... value) {
		this.forUserIds = Arrays.asList(value);
		return this;
	}

	public TenantRequestQuery forUserIds(Collection<UUID> values) {
		this.forUserIds = values;
		return this;
	}

	public TenantRequestQuery statuses(TenantRequestStatus value) {
		this.tenantRequestStatuses = List.of(value);
		return this;
	}

	public TenantRequestQuery statuses(TenantRequestStatus... value) {
		this.tenantRequestStatuses = Arrays.asList(value);
		return this;
	}

	public TenantRequestQuery statuses(Collection<TenantRequestStatus> values) {
		this.tenantRequestStatuses = values;
		return this;
	}

	public TenantRequestQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.ids) || this.isEmpty(this.forUserIds) || this.isEmpty(this.assignedTenantIds) || this.isEmpty(this.tenantRequestStatuses);
	}

	@Override
	protected Class<TenantRequestEntity> entityClass() {
		return TenantRequestEntity.class;
	}

	@Override
	protected <X, Y> Predicate applyAuthZ(QueryContext<X, Y> queryContext) {
		if (this.authorize.contains(AuthorizationFlags.None)) return null;
		if (this.authorize.contains(AuthorizationFlags.Permission) && this.authService.authorize(Permission.BrowseTenantRequest)) return null;
		UUID ownerId = null;
		if (this.authorize.contains(AuthorizationFlags.Owner)) ownerId = this.userScope.getUserIdSafe();

		List<Predicate> predicates = new ArrayList<>();
		if (ownerId != null) {
			predicates.add(queryContext.CriteriaBuilder.equal(queryContext.Root.get(TenantRequestEntity._forUserId), ownerId));
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
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(TenantRequestEntity._id));
			for (UUID item : this.ids) inClause.value(item);
			predicates.add(inClause);
		}

		if (this.forUserIds != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(TenantRequestEntity._forUserId));
			for (UUID item : this.forUserIds) inClause.value(item);
			predicates.add(inClause);
		}

		if (this.assignedTenantIds != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(TenantRequestEntity._assignedTenantId));
			for (UUID item : this.assignedTenantIds) inClause.value(item);
			predicates.add(inClause);
		}

		if (this.like != null && !this.like.isEmpty()) {
			predicates.add(queryContext.CriteriaBuilder.like(queryContext.Root.get(TenantRequestEntity._message), this.like));
		}

		if (this.tenantRequestStatuses != null) {
			CriteriaBuilder.In<TenantRequestStatus> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(TenantRequestEntity._status));
			for (TenantRequestStatus item : this.tenantRequestStatuses) inClause.value(item);
			predicates.add(inClause);
		}
		if (predicates.size() > 0) {
			Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
			return queryContext.CriteriaBuilder.and(predicatesArray);
		} else {
			return null;
		}
	}

	@Override
	protected String fieldNameOf(FieldResolver item) {
		if (item.match(TenantRequestEntity._id)) return TenantRequestEntity._id;
		else if (item.match(TenantRequest._message)) return TenantRequestEntity._message;
		else if (item.match(TenantRequest._status)) return TenantRequestEntity._status;
		else if (item.match(TenantRequest._email)) return TenantRequestEntity._email;
		else if (item.match(TenantRequest._createdAt)) return TenantRequestEntity._createdAt;
		else if (item.match(TenantRequest._updatedAt)) return TenantRequestEntity._updatedAt;
		else if (item.prefix(TenantRequest._forUser)) return TenantRequestEntity._forUserId;
		else if (item.prefix(TenantRequest._assignedTenant)) return TenantRequestEntity._assignedTenantId;
		else return null;
	}

	@Override
	protected TenantRequestEntity convert(Tuple tuple, Set<String> columns) {
		TenantRequestEntity item = new TenantRequestEntity();
		item.setId(QueryBase.convertSafe(tuple, columns, TenantRequestEntity._id, UUID.class));
		item.setMessage(QueryBase.convertSafe(tuple, columns, TenantRequestEntity._message, String.class));
		item.setEmail(QueryBase.convertSafe(tuple, columns, TenantRequestEntity._email, String.class));
		item.setStatus(QueryBase.convertSafe(tuple, columns, TenantRequestEntity._status, TenantRequestStatus.class));
		item.setCreatedAt(QueryBase.convertSafe(tuple, columns, TenantRequestEntity._createdAt, Instant.class));
		item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, TenantRequestEntity._updatedAt, Instant.class));
		item.setForUserId(QueryBase.convertSafe(tuple, columns, TenantRequestEntity._forUserId, UUID.class));
		item.setAssignedTenantId(QueryBase.convertSafe(tuple, columns, TenantRequestEntity._assignedTenantId, UUID.class));
		return item;
	}
}
