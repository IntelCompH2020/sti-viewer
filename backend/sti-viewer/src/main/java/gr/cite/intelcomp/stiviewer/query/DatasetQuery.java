package gr.cite.intelcomp.stiviewer.query;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.data.DatasetEntity;
import gr.cite.intelcomp.stiviewer.model.Dataset;
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
public class DatasetQuery extends QueryBase<DatasetEntity> {

	private String like;
	private Collection<UUID> ids;
	private Collection<IsActive> isActives;

	public DatasetQuery like(String value) {
		this.like = value;
		return this;
	}

	public DatasetQuery ids(UUID value) {
		this.ids = List.of(value);
		return this;
	}

	public DatasetQuery ids(UUID... value) {
		this.ids = Arrays.asList(value);
		return this;
	}

	public DatasetQuery ids(Collection<UUID> values) {
		this.ids = values;
		return this;
	}

	public DatasetQuery isActive(IsActive value) {
		this.isActives = List.of(value);
		return this;
	}

	public DatasetQuery isActive(IsActive... value) {
		this.isActives = Arrays.asList(value);
		return this;
	}

	public DatasetQuery isActive(Collection<IsActive> values) {
		this.isActives = values;
		return this;
	}

	@Override
	protected Class<DatasetEntity> entityClass() {
		return DatasetEntity.class;
	}

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.ids) || this.isEmpty(this.isActives);
	}

	@Override
	protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
		List<Predicate> predicates = new ArrayList<>();
		if (this.ids != null) {
			CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DatasetEntity._id));
			for (UUID item : this.ids) inClause.value(item);
			predicates.add(inClause);
		}
		if (this.like != null && !this.like.isEmpty()) {
			predicates.add(queryContext.CriteriaBuilder.like(queryContext.Root.get(DatasetEntity._title), this.like));
		}
		if (this.isActives != null) {
			CriteriaBuilder.In<IsActive> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DatasetEntity._isActive));
			for (IsActive item : this.isActives) inClause.value(item);
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
	protected DatasetEntity convert(Tuple tuple, Set<String> columns) {
		DatasetEntity item = new DatasetEntity();
		item.setId(QueryBase.convertSafe(tuple, columns, DatasetEntity._id, UUID.class));
		item.setTitle(QueryBase.convertSafe(tuple, columns, DatasetEntity._title, String.class));
		item.setNotes(QueryBase.convertSafe(tuple, columns, DatasetEntity._notes, String.class));
		item.setCreatedAt(QueryBase.convertSafe(tuple, columns, DatasetEntity._createdAt, Instant.class));
		item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, DatasetEntity._updatedAt, Instant.class));
		item.setIsActive(QueryBase.convertSafe(tuple, columns, DatasetEntity._isActive, IsActive.class));
		return item;
	}

	@Override
	protected String fieldNameOf(FieldResolver item) {
		if (item.match(Dataset._id)) return DatasetEntity._id;
		else if (item.match(Dataset._title)) return DatasetEntity._title;
		else if (item.match(Dataset._notes)) return DatasetEntity._notes;
		else if (item.match(Dataset._isActive)) return DatasetEntity._isActive;
		else if (item.match(Dataset._createdAt)) return DatasetEntity._createdAt;
		else if (item.match(Dataset._updatedAt)) return DatasetEntity._updatedAt;
		else if (item.match(Dataset._hash)) return DatasetEntity._updatedAt;
		else return null;
	}
}
