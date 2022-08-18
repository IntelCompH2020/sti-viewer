package gr.cite.notification.query.lookup;

import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.enums.TenantConfigurationType;
import gr.cite.notification.query.TenantConfigurationQuery;
import gr.cite.notification.query.TenantQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.util.List;
import java.util.UUID;

public class TenantConfigurationLookup extends Lookup {
    private List<UUID> ids;
    private List<IsActive> isActives;
    private List<TenantConfigurationType> type;

    public List<IsActive> getIsActives() {
        return isActives;
    }

    public void setIsActives(List<IsActive> isActive) {
        this.isActives = isActive;
    }

    public List<UUID> getIds() {
        return ids;
    }

    public void setIds(List<UUID> ids) {
        this.ids = ids;
    }

    public List<TenantConfigurationType> getType() {
        return type;
    }

    public void setType(List<TenantConfigurationType> type) {
        this.type = type;
    }

    public TenantConfigurationQuery enrich(QueryFactory queryFactory) {
        TenantConfigurationQuery query = queryFactory.query(TenantConfigurationQuery.class);
        if (this.isActives != null) query.isActive(this.isActives);
        if (this.ids != null) query.ids(this.ids);
        if (this.type != null) query.type(this.type);

        this.enrichCommon(query);

        return query;
    }

}
