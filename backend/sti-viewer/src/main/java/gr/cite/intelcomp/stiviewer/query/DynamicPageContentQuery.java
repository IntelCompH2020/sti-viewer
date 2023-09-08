package gr.cite.intelcomp.stiviewer.query;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.data.DynamicPageContentEntity;
import gr.cite.intelcomp.stiviewer.model.DynamicPageContent;
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
public class DynamicPageContentQuery extends QueryBase<DynamicPageContentEntity> {

    private String like;
    private Collection<UUID> ids;
    private Collection<UUID> pageIds;
    private Collection<String> languages;
    private Collection<IsActive> isActives;
    private Collection<UUID> excludedIds;

    public DynamicPageContentQuery like(String value) {
        this.like = value;
        return this;
    }

    public DynamicPageContentQuery ids(UUID value) {
        this.ids = List.of(value);
        return this;
    }

    public DynamicPageContentQuery ids(UUID... value) {
        this.ids = Arrays.asList(value);
        return this;
    }

    public DynamicPageContentQuery ids(Collection<UUID> values) {
        this.ids = values;
        return this;
    }

    public DynamicPageContentQuery pageIds(UUID value) {
        this.pageIds = List.of(value);
        return this;
    }

    public DynamicPageContentQuery pageIds(UUID... value) {
        this.pageIds = Arrays.asList(value);
        return this;
    }

    public DynamicPageContentQuery pageIds(Collection<UUID> values) {
        this.pageIds = values;
        return this;
    }

    public DynamicPageContentQuery languages(String value) {
        this.languages = List.of(value);
        return this;
    }

    public DynamicPageContentQuery languages(String... value) {
        this.languages = Arrays.asList(value);
        return this;
    }

    public DynamicPageContentQuery languages(Collection<String> values) {
        this.languages = values;
        return this;
    }

    public DynamicPageContentQuery isActive(IsActive value) {
        this.isActives = List.of(value);
        return this;
    }

    public DynamicPageContentQuery isActive(IsActive... value) {
        this.isActives = Arrays.asList(value);
        return this;
    }

    public DynamicPageContentQuery isActive(Collection<IsActive> values) {
        this.isActives = values;
        return this;
    }

    public DynamicPageContentQuery excludedIds(Collection<UUID> values) {
        this.excludedIds = values;
        return this;
    }

    public DynamicPageContentQuery excludedIds(UUID value) {
        this.excludedIds = List.of(value);
        return this;
    }

    public DynamicPageContentQuery excludedIds(UUID... value) {
        this.excludedIds = Arrays.asList(value);
        return this;
    }

    public DynamicPageContentQuery authorize(EnumSet<AuthorizationFlags> values) {
        return this;
    }

    public DynamicPageContentQuery() {
    }

    @Override
    protected Class<DynamicPageContentEntity> entityClass() {
        return DynamicPageContentEntity.class;
    }

    @Override
    protected Boolean isFalseQuery() {
        return this.isEmpty(this.ids) || this.isEmpty(this.isActives) || this.isEmpty(this.languages) || this.isEmpty(this.excludedIds) || this.isEmpty(this.pageIds);
    }

    @Override
    protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
        List<Predicate> predicates = new ArrayList<>();
        if (this.like != null && !this.like.isEmpty()) {
            predicates.add(queryContext.CriteriaBuilder.like(queryContext.Root.get(DynamicPageContentEntity._title), this.like));
        }

        if (this.ids != null) {
            CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DynamicPageContentEntity._id));
            for (UUID item : this.ids) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.pageIds != null) {
            CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DynamicPageContentEntity._pageId));
            for (UUID item : this.pageIds) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.languages != null) {
            CriteriaBuilder.In<String> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DynamicPageContentEntity._language));
            for (String item : this.languages) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.isActives != null) {
            CriteriaBuilder.In<IsActive> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DynamicPageContentEntity._isActive));
            for (IsActive item : this.isActives) inClause.value(item);
            predicates.add(inClause);
        }

        if (this.excludedIds != null) {
            CriteriaBuilder.In<UUID> notInClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(DynamicPageContentEntity._id));
            for (UUID item : this.excludedIds) notInClause.value(item);
            predicates.add(notInClause.not());
        }

        if (!predicates.isEmpty()) {
            Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
            return queryContext.CriteriaBuilder.and(predicatesArray);
        } else {
            return null;
        }
    }

    @Override
    protected DynamicPageContentEntity convert(Tuple tuple, Set<String> columns) {
        DynamicPageContentEntity item = new DynamicPageContentEntity();
        item.setId(QueryBase.convertSafe(tuple, columns, DynamicPageContentEntity._id, UUID.class));
        item.setPageId(QueryBase.convertSafe(tuple, columns, DynamicPageContentEntity._pageId, UUID.class));
        item.setTitle(QueryBase.convertSafe(tuple, columns, DynamicPageContentEntity._title, String.class));
        item.setContent(QueryBase.convertSafe(tuple, columns, DynamicPageContentEntity._content, String.class));
        item.setLanguage(QueryBase.convertSafe(tuple, columns, DynamicPageContentEntity._language, String.class));
        item.setCreatedAt(QueryBase.convertSafe(tuple, columns, DynamicPageContentEntity._createdAt, Instant.class));
        item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, DynamicPageContentEntity._updatedAt, Instant.class));
        item.setIsActive(QueryBase.convertSafe(tuple, columns, DynamicPageContentEntity._isActive, IsActive.class));
        return item;
    }

    @Override
    protected String fieldNameOf(FieldResolver item) {
        if (item.match(DynamicPageContent._id))
            return DynamicPageContentEntity._id;
        else if (item.prefix(DynamicPageContent._page))
            return DynamicPageContentEntity._pageId;
        else if (item.match(DynamicPageContent._page))
            return DynamicPageContentEntity._pageId;
        else if (item.match(DynamicPageContent._title))
            return DynamicPageContentEntity._title;
        else if (item.match(DynamicPageContent._content))
            return DynamicPageContentEntity._content;
        else if (item.match(DynamicPageContent._language))
            return DynamicPageContentEntity._language;
        else if (item.match(DynamicPageContent._createdAt))
            return DynamicPageContentEntity._createdAt;
        else if (item.match(DynamicPageContent._updatedAt))
            return DynamicPageContentEntity._updatedAt;
        else if (item.match(DynamicPageContent._hash))
            return DynamicPageContent._updatedAt;
        else if (item.match(DynamicPageContent._isActive))
            return DynamicPageContentEntity._isActive;
        else
            return null;
    }

}
