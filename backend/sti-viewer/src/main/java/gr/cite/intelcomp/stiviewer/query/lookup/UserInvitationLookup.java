package gr.cite.intelcomp.stiviewer.query.lookup;

import gr.cite.intelcomp.stiviewer.query.UserInvitationQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.util.List;
import java.util.UUID;

public class UserInvitationLookup extends Lookup {

    private String like;
    private List<UUID> ids;
    private List<UUID> tenantIds;

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

    public List<UUID> getTenantIds() {
        return tenantIds;
    }

    public void setTenantIds(List<UUID> tenantIds) {
        this.tenantIds = tenantIds;
    }

    public UserInvitationQuery enrich(QueryFactory queryFactory) {
        UserInvitationQuery query = queryFactory.query(UserInvitationQuery.class);
        if (this.like != null) query.like(this.like);
        if (this.ids != null) query.ids(this.ids);
        if (this.tenantIds != null) query.tenantIds(this.tenantIds);

        this.enrichCommon(query);

        return query;
    }

}
