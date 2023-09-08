package gr.cite.intelcomp.stiviewer.query.lookup;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.query.UserContactInfoQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.util.List;
import java.util.UUID;

public class UserContactInfoLookup extends Lookup {

    private List<IsActive> isActive;
    private List<UUID> userIds;
    private List<UUID> tenantIds;
    private UserLookup userSubQuery;

    public List<IsActive> getIsActive() {
        return isActive;
    }

    public void setIsActive(List<IsActive> isActive) {
        this.isActive = isActive;
    }

    public List<UUID> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<UUID> userIds) {
        this.userIds = userIds;
    }

    public List<UUID> getTenantIds() {
        return tenantIds;
    }

    public void setTenantIds(List<UUID> tenantIds) {
        this.tenantIds = tenantIds;
    }

    public UserLookup getUserSubQuery() {
        return userSubQuery;
    }

    public void setUserSubQuery(UserLookup userSubQuery) {
        this.userSubQuery = userSubQuery;
    }

    public UserContactInfoQuery enrich(QueryFactory queryFactory) {
        UserContactInfoQuery query = queryFactory.query(UserContactInfoQuery.class);
        if (this.isActive != null)
            query.isActive(this.isActive);
        if (this.userIds != null)
            query.userIds(this.userIds);
        if (this.tenantIds != null)
            query.tenantIds(this.tenantIds);
        if (this.userSubQuery != null)
            query.userSubQuery(this.userSubQuery.enrich(queryFactory));

        this.enrichCommon(query);

        return query;
    }

}
