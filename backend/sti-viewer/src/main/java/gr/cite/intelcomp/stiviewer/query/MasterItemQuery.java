package gr.cite.intelcomp.stiviewer.query;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.data.MasterItemEntity;
import gr.cite.intelcomp.stiviewer.model.MasterItem;
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
public class MasterItemQuery extends QueryBase<MasterItemEntity> {

    private String like;
    private Collection<UUID> ids;
    private Collection<IsActive> isActives;

    public MasterItemQuery like(String value) {
        this.like = value;
        return this;
    }

    public MasterItemQuery ids(UUID value) {
        this.ids = List.of(value);
        return this;
    }

    public MasterItemQuery ids(UUID... value) {
        this.ids = Arrays.asList(value);
        return this;
    }

    public MasterItemQuery ids(Collection<UUID> values) {
        this.ids = values;
        return this;
    }

    public MasterItemQuery isActive(IsActive value) {
        this.isActives = List.of(value);
        return this;
    }

    public MasterItemQuery isActive(IsActive... value) {
        this.isActives = Arrays.asList(value);
        return this;
    }

    public MasterItemQuery isActive(Collection<IsActive> values) {
        this.isActives = values;
        return this;
    }

    @Override
    protected Class<MasterItemEntity> entityClass() {
        return MasterItemEntity.class;
    }

    @Override
    protected Boolean isFalseQuery() {
        return this.isEmpty(this.ids) || this.isEmpty(this.isActives);
    }

    @Override
    protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
        List<Predicate> predicates = new ArrayList<>();
        if (this.ids != null) {
            CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(MasterItemEntity._id));
            for (UUID item : this.ids) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.like != null && !this.like.isEmpty()) {
            predicates.add(queryContext.CriteriaBuilder.like(queryContext.Root.get(MasterItemEntity._name), this.like));
        }

        if (this.isActives != null) {
            CriteriaBuilder.In<IsActive> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(MasterItemEntity._isActive));
            for (IsActive item : this.isActives) inClause.value(item);
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
    protected MasterItemEntity convert(Tuple tuple, Set<String> columns) {
        MasterItemEntity item = new MasterItemEntity();
        item.setId(QueryBase.convertSafe(tuple, columns, MasterItemEntity._id, UUID.class));
        item.setName(QueryBase.convertSafe(tuple, columns, MasterItemEntity._name, String.class));
        item.setCreatedAt(QueryBase.convertSafe(tuple, columns, MasterItemEntity._createdAt, Instant.class));
        item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, MasterItemEntity._updatedAt, Instant.class));
        item.setIsActive(QueryBase.convertSafe(tuple, columns, MasterItemEntity._isActive, IsActive.class));
        return item;
    }

    @Override
    protected String fieldNameOf(FieldResolver item) {
        if (item.match(MasterItem._id))
            return MasterItemEntity._id;
        else if (item.match(MasterItem._name))
            return MasterItemEntity._name;
        else if (item.match(MasterItem._isActive))
            return MasterItemEntity._isActive;
        else if (item.match(MasterItem._createdAt))
            return MasterItemEntity._createdAt;
        else if (item.match(MasterItem._updatedAt))
            return MasterItemEntity._updatedAt;
        else if (item.match(MasterItem._hash))
            return MasterItemEntity._updatedAt;
        else
            return null;
    }
}
