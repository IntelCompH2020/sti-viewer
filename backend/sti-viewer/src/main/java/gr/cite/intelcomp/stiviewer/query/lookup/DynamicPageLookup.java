package gr.cite.intelcomp.stiviewer.query.lookup;

import gr.cite.intelcomp.stiviewer.common.enums.DynamicPageType;
import gr.cite.intelcomp.stiviewer.common.enums.DynamicPageVisibility;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.query.DynamicPageQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.util.List;
import java.util.UUID;

public class DynamicPageLookup extends Lookup {
    private String like;
    private List<UUID> ids;
    private List<UUID> creatorIds;
    private List<String> defaultLanguages;
    private List<IsActive> isActives;
    private List<DynamicPageVisibility> visibility;
    private List<DynamicPageType> type;
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

    public List<UUID> getCreatorIds() {
        return creatorIds;
    }

    public void setCreatorIds(List<UUID> creatorIds) {
        this.creatorIds = creatorIds;
    }

    public List<String> getDefaultLanguages() {
        return defaultLanguages;
    }

    public void setDefaultLanguages(List<String> defaultLanguages) {
        this.defaultLanguages = defaultLanguages;
    }

    public List<IsActive> getIsActives() {
        return isActives;
    }

    public void setIsActives(List<IsActive> isActives) {
        this.isActives = isActives;
    }

    public List<DynamicPageVisibility> getVisibility() {
        return visibility;
    }

    public void setVisibility(List<DynamicPageVisibility> visibility) {
        this.visibility = visibility;
    }

    public List<DynamicPageType> getType() {
        return type;
    }

    public void setType(List<DynamicPageType> type) {
        this.type = type;
    }

    public List<UUID> getExcludedIds() {
        return excludedIds;
    }

    public void setExcludedIds(List<UUID> excludedIds) {
        this.excludedIds = excludedIds;
    }

    public DynamicPageQuery enrich(QueryFactory queryFactory) {
        DynamicPageQuery query = queryFactory.query(DynamicPageQuery.class);
        if (this.ids != null)
            query.ids(this.ids);
        if (this.like != null)
            query.like(this.like);
        if (this.creatorIds != null)
            query.creatorIds(this.creatorIds);
        if (this.defaultLanguages != null)
            query.defaultLanguages(this.defaultLanguages);
        if (this.isActives != null)
            query.isActive(this.isActives);
        if (this.visibility != null)
            query.visibility(this.visibility);
        if (this.type != null)
            query.type(this.type);
        if (this.excludedIds != null)
            query.excludedIds(this.excludedIds);

        this.enrichCommon(query);

        return query;
    }

}
