package gr.cite.intelcomp.stiviewer.query;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.data.DetailItemEntity;
import gr.cite.intelcomp.stiviewer.data.MasterItemEntity;
import gr.cite.intelcomp.stiviewer.model.DetailItem;
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
public class DetailItemQuery extends QueryBase<DetailItemEntity> {

	private String like;
	private Collection<UUID> ids;
	private Collection<UUID> masterItemIds;
	private Collection<IsActive> isActives;
	private MasterItemQuery masterQuery;

	public DetailItemQuery like(String value) {
		this.like = value;
		return this;
	}

	public DetailItemQuery ids(UUID value) {
		this.ids = List.of(value);
		return this;
	}

	public DetailItemQuery ids(UUID... value) {
		this.ids = Arrays.asList(value);
		return this;
	}

	public DetailItemQuery ids(Collection<UUID> values) {
		this.ids = values;
		return this;
	}

	public DetailItemQuery masterItemIds(UUID value) {
		this.masterItemIds = List.of(value);
		return this;
	}

	public DetailItemQuery masterItemIds(UUID... value) {
		this.masterItemIds = Arrays.asList(value);
		return this;
	}

	public DetailItemQuery masterItemIds(Collection<UUID> values) {
		this.masterItemIds = values;
		return this;
	}

	public DetailItemQuery isActive(IsActive value) {
		this.isActives = List.of(value);
		return this;
	}

	public DetailItemQuery isActive(IsActive... value) {
		this.isActives = Arrays.asList(value);
		return this;
	}

	public DetailItemQuery isActive(Collection<IsActive> values) {
		this.isActives = values;
		return this;
	}

	public DetailItemQuery masterItemSubQuery(MasterItemQuery subQuery) {
		this.masterQuery = subQuery;
		return this;
	}

	@Override
	protected Class<DetailItemEntity> entityClass() {
		return DetailItemEntity.class;
	}

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.ids) || this.isEmpty(this.masterItemIds) || this.isEmpty(this.isActives) || this.isFalseQuery(this.masterQuery);
	}

	@Override
	protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
		List<Predicate> predicates = new ArrayList<>();
		if (this.ids != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DetailItemEntity._id));
			for (UUID item : this.ids) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.masterItemIds != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DetailItemEntity._masterId));
			for (UUID item : this.masterItemIds) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.like != null && !this.like.isEmpty()) {
			predicates.add(queryContext.CriteriaBuilder.like(queryContext.Root.get(DetailItemEntity._name), this.like));
		}
		if (this.isActives != null) {
			CriteriaBuilder.In<IsActive> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DetailItemEntity._isActive));
			for (IsActive item : this.isActives) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.masterQuery != null) {
			Subquery<MasterItemEntity> subQuery = queryContext.Query.subquery(this.masterQuery.entityClass());
			this.applySubQuery(this.masterQuery, queryContext.CriteriaBuilder, subQuery);
			predicates.add(queryContext.CriteriaBuilder.in(queryContext.Root.get(DetailItemEntity._masterId)).value(subQuery));
		}
		if (predicates.size() > 0) {
			Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
			return queryContext.CriteriaBuilder.and(predicatesArray);
		} else {
			return null;
		}
	}

	@Override
	protected DetailItemEntity convert(Tuple tuple, Set<String> columns) {
		DetailItemEntity item = new DetailItemEntity();
		item.setId(QueryBase.convertSafe(tuple, columns, DetailItemEntity._id, UUID.class));
		item.setName(QueryBase.convertSafe(tuple, columns, DetailItemEntity._name, String.class));
		item.setMasterId(QueryBase.convertSafe(tuple, columns, DetailItemEntity._masterId, UUID.class));
		item.setCreatedAt(QueryBase.convertSafe(tuple, columns, DetailItemEntity._createdAt, Instant.class));
		item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, DetailItemEntity._updatedAt, Instant.class));
		item.setIsActive(QueryBase.convertSafe(tuple, columns, DetailItemEntity._isActive, IsActive.class));
		return item;
	}

	@Override
	protected String fieldNameOf(FieldResolver item) {
		if (item.match(DetailItem._id)) return DetailItemEntity._id;
		else if (item.match(DetailItem._name)) return DetailItemEntity._name;
		else if (item.match(DetailItem._isActive)) return DetailItemEntity._isActive;
		else if (item.match(DetailItem._createdAt)) return DetailItemEntity._createdAt;
		else if (item.match(DetailItem._updatedAt)) return DetailItemEntity._updatedAt;
		else if (item.match(DetailItem._hash)) return DetailItemEntity._updatedAt;
		else if (item.match(DetailItem._master, MasterItemEntity._id)) return DetailItemEntity._masterId;
		else if (item.prefix(DetailItem._master)) return DetailItemEntity._masterId;
		else return null;
	}
}
