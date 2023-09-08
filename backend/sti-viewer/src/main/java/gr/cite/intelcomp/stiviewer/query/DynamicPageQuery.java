package gr.cite.intelcomp.stiviewer.query;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.enums.DynamicPageType;
import gr.cite.intelcomp.stiviewer.common.enums.DynamicPageVisibility;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.data.DynamicPageContentEntity;
import gr.cite.intelcomp.stiviewer.data.DynamicPageEntity;
import gr.cite.intelcomp.stiviewer.model.DynamicPage;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryBase;
import gr.cite.tools.data.query.QueryContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.Instant;
import java.util.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DynamicPageQuery extends QueryBase<DynamicPageEntity> {

    private String like;
    private Collection<UUID> ids;
    private Collection<UUID> creatorIds;
    private Collection<String> defaultLanguages;
    private Collection<IsActive> isActives;
    private Collection<DynamicPageVisibility> visibility;
    private Collection<DynamicPageType> type;
    private Collection<UUID> excludedIds;

    public DynamicPageQuery like(String value) {
        this.like = value;
        return this;
    }

    public DynamicPageQuery ids(UUID value) {
        this.ids = List.of(value);
        return this;
    }

    public DynamicPageQuery ids(UUID... value) {
        this.ids = Arrays.asList(value);
        return this;
    }

    public DynamicPageQuery ids(Collection<UUID> values) {
        this.ids = values;
        return this;
    }

    public DynamicPageQuery creatorIds(UUID value) {
        this.creatorIds = List.of(value);
        return this;
    }

    public DynamicPageQuery creatorIds(UUID... value) {
        this.creatorIds = Arrays.asList(value);
        return this;
    }

    public DynamicPageQuery creatorIds(Collection<UUID> values) {
        this.creatorIds = values;
        return this;
    }

    public DynamicPageQuery defaultLanguages(String value) {
        this.defaultLanguages = List.of(value);
        return this;
    }

    public DynamicPageQuery defaultLanguages(String... value) {
        this.defaultLanguages = Arrays.asList(value);
        return this;
    }

    public DynamicPageQuery defaultLanguages(Collection<String> values) {
        this.defaultLanguages = values;
        return this;
    }

    public DynamicPageQuery isActive(IsActive value) {
        this.isActives = List.of(value);
        return this;
    }

    public DynamicPageQuery isActive(IsActive... value) {
        this.isActives = Arrays.asList(value);
        return this;
    }

    public DynamicPageQuery isActive(Collection<IsActive> values) {
        this.isActives = values;
        return this;
    }

    public DynamicPageQuery visibility(DynamicPageVisibility value) {
        this.visibility = List.of(value);
        return this;
    }

    public DynamicPageQuery visibility(DynamicPageVisibility... value) {
        this.visibility = Arrays.asList(value);
        return this;
    }

    public DynamicPageQuery visibility(Collection<DynamicPageVisibility> values) {
        this.visibility = values;
        return this;
    }

    public DynamicPageQuery type(DynamicPageType value) {
        this.type = List.of(value);
        return this;
    }

    public DynamicPageQuery type(DynamicPageType... value) {
        this.type = Arrays.asList(value);
        return this;
    }

    public DynamicPageQuery type(Collection<DynamicPageType> values) {
        this.type = values;
        return this;
    }

    public DynamicPageQuery excludedIds(Collection<UUID> values) {
        this.excludedIds = values;
        return this;
    }

    public DynamicPageQuery excludedIds(UUID value) {
        this.excludedIds = List.of(value);
        return this;
    }

    public DynamicPageQuery excludedIds(UUID... value) {
        this.excludedIds = Arrays.asList(value);
        return this;
    }

    public DynamicPageQuery authorize(EnumSet<AuthorizationFlags> values) {
        return this;
    }

    public DynamicPageQuery() {
    }

    @Override
    protected Class<DynamicPageEntity> entityClass() {
        return DynamicPageEntity.class;
    }

    @Override
    protected Boolean isFalseQuery() {
        return this.isEmpty(this.ids) || this.isEmpty(this.isActives) || this.isEmpty(this.defaultLanguages) || this.isEmpty(this.excludedIds) || this.isEmpty(this.visibility) || this.isEmpty(this.type) || this.isEmpty(this.creatorIds);
    }

    @Override
    protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
        List<Predicate> predicates = new ArrayList<>();
        if (this.ids != null) {
            CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DynamicPageEntity._id));
            for (UUID item : this.ids) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.creatorIds != null) {
            CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DynamicPageEntity._creatorId));
            for (UUID item : this.creatorIds) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.defaultLanguages != null) {
            CriteriaBuilder.In<String> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DynamicPageEntity._defaultLanguage));
            for (String item : this.defaultLanguages) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.isActives != null) {
            CriteriaBuilder.In<IsActive> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DynamicPageEntity._isActive));
            for (IsActive item : this.isActives) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.visibility != null) {
            CriteriaBuilder.In<DynamicPageVisibility> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DynamicPageEntity._visibility));
            for (DynamicPageVisibility item : this.visibility) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.type != null) {
            CriteriaBuilder.In<DynamicPageType> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DynamicPageEntity._type));
            for (DynamicPageType item : this.type) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.excludedIds != null) {
            CriteriaBuilder.In<UUID> notInClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DynamicPageEntity._id));
            for (UUID item : this.excludedIds) notInClause.value(item);
            predicates.add(notInClause.not());
        }

        if (this.like != null && !this.like.isEmpty()) { //TODO move Sub query to base
            Subquery<DynamicPageContentEntity> subCriteria = queryContext.Query.subquery(DynamicPageContentEntity.class);
            Root<DynamicPageContentEntity> root = subCriteria.from(DynamicPageContentEntity.class);
            subCriteria.select(root.get(DynamicPageContentEntity._pageId)).distinct(true);
            subCriteria.where(queryContext.CriteriaBuilder.like(root.get(DynamicPageContentEntity._title), this.like));
            predicates.add(queryContext.CriteriaBuilder.in(queryContext.Root.get(DynamicPage._id)).value(subCriteria));
        }

        if (!predicates.isEmpty()) {
            Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
            return queryContext.CriteriaBuilder.and(predicatesArray);
        } else {
            return null;
        }
    }

    @Override
    protected DynamicPageEntity convert(Tuple tuple, Set<String> columns) {
        DynamicPageEntity item = new DynamicPageEntity();
        item.setId(QueryBase.convertSafe(tuple, columns, DynamicPageEntity._id, UUID.class));
        item.setCreatorId(QueryBase.convertSafe(tuple, columns, DynamicPageEntity._creatorId, UUID.class));
        item.setVisibility(QueryBase.convertSafe(tuple, columns, DynamicPageEntity._visibility, DynamicPageVisibility.class));
        item.setOrder(QueryBase.convertSafe(tuple, columns, DynamicPageEntity._order, Integer.class));
        item.setConfig(QueryBase.convertSafe(tuple, columns, DynamicPageEntity._config, String.class));
        item.setType(QueryBase.convertSafe(tuple, columns, DynamicPageEntity._type, DynamicPageType.class));
        item.setDefaultLanguage(QueryBase.convertSafe(tuple, columns, DynamicPageEntity._defaultLanguage, String.class));
        item.setCreatedAt(QueryBase.convertSafe(tuple, columns, DynamicPageEntity._createdAt, Instant.class));
        item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, DynamicPageEntity._updatedAt, Instant.class));
        item.setIsActive(QueryBase.convertSafe(tuple, columns, DynamicPageEntity._isActive, IsActive.class));
        return item;
    }

    @Override
    protected String fieldNameOf(FieldResolver item) {
        if (item.match(DynamicPage._id))
            return DynamicPageEntity._id;
        else if (item.prefix(DynamicPage._creator))
            return DynamicPageEntity._creatorId;
        else if (item.match(DynamicPage._visibility))
            return DynamicPageEntity._visibility;
        else if (item.match(DynamicPage._order))
            return DynamicPageEntity._order;
        else if (item.match(DynamicPage._type))
            return DynamicPageEntity._type;
        else if (item.match(DynamicPage._defaultLanguage))
            return DynamicPageEntity._defaultLanguage;
        else if (item.match(DynamicPage._config))
            return DynamicPageEntity._config;
        else if (item.prefix(DynamicPage._config))
            return DynamicPageEntity._config;
        else if (item.match(DynamicPage._createdAt))
            return DynamicPageEntity._createdAt;
        else if (item.match(DynamicPage._updatedAt))
            return DynamicPageEntity._updatedAt;
        else if (item.match(DynamicPage._hash))
            return DynamicPageEntity._updatedAt;
        else if (item.match(DynamicPage._isActive))
            return DynamicPageEntity._isActive;
        else
            return null;
    }

}
