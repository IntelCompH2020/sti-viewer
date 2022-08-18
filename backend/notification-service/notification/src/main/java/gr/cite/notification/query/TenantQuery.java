package gr.cite.notification.query;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.scope.user.UserScope;
import gr.cite.notification.data.TenantEntity;
import gr.cite.notification.model.Tenant;
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
public class TenantQuery extends QueryBase<TenantEntity> {

	private String like;
	private Collection<UUID> ids;
	private Collection<IsActive> isActives;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public TenantQuery like(String value) {
		this.like = value;
		return this;
	}

	public TenantQuery ids(UUID value) {
		this.ids = List.of(value);
		return this;
	}

	public TenantQuery ids(UUID... value) {
		this.ids = Arrays.asList(value);
		return this;
	}

	public TenantQuery ids(Collection<UUID> values) {
		this.ids = values;
		return this;
	}

	public TenantQuery isActive(IsActive value) {
		this.isActives = List.of(value);
		return this;
	}

	public TenantQuery isActive(IsActive... value) {
		this.isActives = Arrays.asList(value);
		return this;
	}

	public TenantQuery isActive(Collection<IsActive> values) {
		this.isActives = values;
		return this;
	}

	public TenantQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.ids) || this.isEmpty(this.isActives);
	}

	@Override
	protected Class<TenantEntity> entityClass() {
		return TenantEntity.class;
	}

	@Override
	protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
		List<Predicate> predicates = new ArrayList<>();
		if (this.ids != null) {
			predicates.add(queryContext.Root.get(TenantEntity._id).in(ids));
		}

		if (this.like != null && !this.like.isEmpty()) {
			predicates.add(queryContext.CriteriaBuilder.like(queryContext.Root.get(TenantEntity._isActive), this.like));
		}

		if (this.isActives != null) {
			predicates.add(queryContext.Root.get(TenantEntity._isActive).in(isActives));
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
		if (item.match(Tenant._id)) return TenantEntity._id;
		else if (item.match(Tenant._code)) return TenantEntity._code;
		else if (item.match(Tenant._name)) return TenantEntity._name;
		else if (item.match(Tenant._config)) return TenantEntity._config;
		else if (item.match(Tenant._createdAt)) return TenantEntity._createdAt;
		else if (item.match(Tenant._updatedAt)) return TenantEntity._updatedAt;
		else if (item.match(Tenant._isActive)) return TenantEntity._isActive;
		else return null;
	}

	@Override
	protected TenantEntity convert(Tuple tuple, Set<String> columns) {
		TenantEntity item = new TenantEntity();
		item.setId(QueryBase.convertSafe(tuple, columns, TenantEntity._id, UUID.class));
		item.setCode(QueryBase.convertSafe(tuple, columns, TenantEntity._code, String.class));
		item.setName(QueryBase.convertSafe(tuple, columns, TenantEntity._name, String.class));
		item.setConfig(QueryBase.convertSafe(tuple, columns, TenantEntity._config, String.class));
		item.setCreatedAt(QueryBase.convertSafe(tuple, columns, TenantEntity._createdAt, Instant.class));
		item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, TenantEntity._updatedAt, Instant.class));
		item.setIsActive(QueryBase.convertSafe(tuple, columns, TenantEntity._isActive, IsActive.class));
		return item;
	}
}
