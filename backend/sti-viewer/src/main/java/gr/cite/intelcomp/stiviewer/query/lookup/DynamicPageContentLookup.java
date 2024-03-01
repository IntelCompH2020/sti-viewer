package gr.cite.intelcomp.stiviewer.query.lookup;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.query.DynamicPageContentQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.util.List;
import java.util.UUID;

public class DynamicPageContentLookup extends Lookup {
    private String like;
    private List<UUID> ids;
    private List<UUID> pageIds;
    private List<String> languages;
    private List<IsActive> isActives;
    private List<UUID> excludedIds;

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public List<UUID> getIds() {
        return ids;
    }

    public void setIds(List<UUID> ids) {
        this.ids = ids;
    }

    public List<UUID> getPageIds() {
        return pageIds;
    }

    public void setPageIds(List<UUID> pageIds) {
        this.pageIds = pageIds;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<IsActive> getIsActives() {
        return isActives;
    }

    public void setIsActives(List<IsActive> isActives) {
        this.isActives = isActives;
    }

    public List<UUID> getExcludedIds() {
        return excludedIds;
    }

    public void setExcludedIds(List<UUID> excludedIds) {
        this.excludedIds = excludedIds;
    }

    public DynamicPageContentQuery enrich(QueryFactory queryFactory) {
        DynamicPageContentQuery query = queryFactory.query(DynamicPageContentQuery.class);
        if (this.like != null) query.like(this.like);
        if (this.ids != null) query.ids(this.ids);
        if (this.pageIds != null) query.pageIds(this.pageIds);
        if (this.languages != null) query.languages(this.languages);
        if (this.isActives != null) query.isActive(this.isActives);
        if (this.excludedIds != null) query.excludedIds(this.excludedIds);

        this.enrichCommon(query);

        return query;
    }

}
