package gr.cite.user.query.lookup;

import gr.cite.user.common.enums.IsActive;
import gr.cite.user.query.TenantQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.util.List;
import java.util.UUID;

public class TenantLookup extends Lookup {
    private String like;
    private List<IsActive> isActive;
    private List<UUID> ids;

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public List<IsActive> getIsActive() {
        return isActive;
    }

    public void setIsActive(List<IsActive> isActive) {
        this.isActive = isActive;
    }

    public List<UUID> getIds() {
        return ids;
    }

    public void setIds(List<UUID> ids) {
        this.ids = ids;
    }

    public TenantQuery enrich(QueryFactory queryFactory) {
        TenantQuery query = queryFactory.query(TenantQuery.class);
        if (this.like != null) query.like(this.like);
        if (this.isActive != null) query.isActive(this.isActive);
        if (this.ids != null) query.ids(this.ids);

        this.enrichCommon(query);

        return query;
    }

}
