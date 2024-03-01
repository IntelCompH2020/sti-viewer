package gr.cite.intelcomp.stiviewer.query;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.data.IndicatorEntity;
import gr.cite.intelcomp.stiviewer.model.Indicator;
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
public class IndicatorQuery extends QueryBase<IndicatorEntity> {

	private String like;
	private Collection<UUID> ids;
	private Collection<String> codes;
	private Collection<IsActive> isActives;
	private Collection<UUID> excludedIds;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public IndicatorQuery like(String value) {
		this.like = value;
		return this;
	}

	public IndicatorQuery ids(UUID value) {
		this.ids = List.of(value);
		return this;
	}

	public IndicatorQuery ids(UUID... value) {
		this.ids = Arrays.asList(value);
		return this;
	}

	public IndicatorQuery ids(Collection<UUID> values) {
		this.ids = values;
		return this;
	}

	public IndicatorQuery codes(String value) {
		this.codes = List.of(value);
		return this;
	}

	public IndicatorQuery codes(String... value) {
		this.codes = Arrays.asList(value);
		return this;
	}

	public IndicatorQuery codes(Collection<String> values) {
		this.codes = values;
		return this;
	}

	public IndicatorQuery isActive(IsActive value) {
		this.isActives = List.of(value);
		return this;
	}

	public IndicatorQuery isActive(IsActive... value) {
		this.isActives = Arrays.asList(value);
		return this;
	}

	public IndicatorQuery isActive(Collection<IsActive> values) {
		this.isActives = values;
		return this;
	}

	public IndicatorQuery excludedIds(Collection<UUID> values) {
		this.excludedIds = values;
		return this;
	}

	public IndicatorQuery excludedIds(UUID value) {
		this.excludedIds = List.of(value);
		return this;
	}

	public IndicatorQuery excludedIds(UUID... value) {
		this.excludedIds = Arrays.asList(value);
		return this;
	}

	public IndicatorQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	private final UserScope userScope;
	private final AuthorizationService authService;

	public IndicatorQuery(
			UserScope userScope,
			AuthorizationService authService
	) {
		this.userScope = userScope;
		this.authService = authService;
	}

	@Override
	protected Class<IndicatorEntity> entityClass() {
		return IndicatorEntity.class;
	}

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.ids) || this.isEmpty(this.isActives) || this.isEmpty(this.codes) || this.isEmpty(this.excludedIds);
	}

	@Override
	protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
		List<Predicate> predicates = new ArrayList<>();
		if (this.ids != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(IndicatorEntity._id));
			for (UUID item : this.ids) inClause.value(item);
			predicates.add(inClause);
		}

		if (this.codes != null) {
			CriteriaBuilder.In<String> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(IndicatorEntity._code));
			for (String item : this.codes) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.like != null && !this.like.isEmpty()) {
			predicates.add(queryContext.CriteriaBuilder.like(queryContext.Root.get(IndicatorEntity._name), this.like));
		}
		if (this.isActives != null) {
			CriteriaBuilder.In<IsActive> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(IndicatorEntity._isActive));
			for (IsActive item : this.isActives) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.excludedIds != null) {
			CriteriaBuilder.In<UUID> notInClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(IndicatorEntity._id));
			for (UUID item : this.excludedIds) notInClause.value(item);
			predicates.add(notInClause.not());
		}
		if (predicates.size() > 0) {
			Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
			return queryContext.CriteriaBuilder.and(predicatesArray);
		} else {
			return null;
		}
	}

	@Override
	protected IndicatorEntity convert(Tuple tuple, Set<String> columns) {
		IndicatorEntity item = new IndicatorEntity();
		item.setId(QueryBase.convertSafe(tuple, columns, IndicatorEntity._id, UUID.class));
		item.setCode(QueryBase.convertSafe(tuple, columns, IndicatorEntity._code, String.class));
		item.setName(QueryBase.convertSafe(tuple, columns, IndicatorEntity._name, String.class));
		item.setDescription(QueryBase.convertSafe(tuple, columns, IndicatorEntity._description, String.class));
		item.setConfig(QueryBase.convertSafe(tuple, columns, IndicatorEntity._config, String.class));
		item.setCreatedAt(QueryBase.convertSafe(tuple, columns, IndicatorEntity._createdAt, Instant.class));
		item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, IndicatorEntity._updatedAt, Instant.class));
		item.setIsActive(QueryBase.convertSafe(tuple, columns, IndicatorEntity._isActive, IsActive.class));
		return item;
	}

	@Override
	protected String fieldNameOf(FieldResolver item) {
		if (item.match(Indicator._id)) return Indicator._id;
		else if (item.match(Indicator._code)) return Indicator._code;
		else if (item.match(Indicator._name)) return Indicator._name;
		else if (item.match(Indicator._description)) return Indicator._description;
		else if (item.match(Indicator._config)) return Indicator._config;
		else if (item.prefix(Indicator._config)) return Indicator._config;
		else if (item.match(Indicator._createdAt)) return Indicator._createdAt;
		else if (item.match(Indicator._updatedAt)) return Indicator._updatedAt;
		else if (item.match(Indicator._isActive)) return Indicator._isActive;
		else return null;
	}

}
