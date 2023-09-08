package gr.cite.intelcomp.stiviewer.query.lookup;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.query.IndicatorAccessQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.util.List;
import java.util.UUID;

public class IndicatorAccessLookup extends Lookup {

    private List<UUID> ids;
    private List<UUID> indicatorIds;
    private List<UUID> userIds;
    private List<IsActive> isActive;

    public List<UUID> getIds() {
        return ids;
    }

    public void setIds(List<UUID> ids) {
        this.ids = ids;
    }

    public List<UUID> getIndicatorIds() {
        return indicatorIds;
    }

    public void setIndicatorIds(List<UUID> indicatorIds) {
        this.indicatorIds = indicatorIds;
    }

    public List<UUID> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<UUID> userIds) {
        this.userIds = userIds;
    }

    public List<IsActive> getIsActive() {
        return isActive;
    }

    public void setIsActive(List<IsActive> isActive) {
        this.isActive = isActive;
    }

    public IndicatorAccessQuery enrich(QueryFactory queryFactory) {
        IndicatorAccessQuery query = queryFactory.query(IndicatorAccessQuery.class);
        if (this.ids != null)
            query.ids(this.ids);
        if (this.indicatorIds != null)
            query.indicatorIds(this.indicatorIds);
        if (this.userIds != null)
            query.userIds(this.userIds);
        if (this.isActive != null)
            query.isActive(this.isActive);

        this.enrichCommon(query);

        return query;
    }

}
