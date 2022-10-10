package gr.cite.intelcomp.stiviewer.query.lookup;

import gr.cite.intelcomp.stiviewer.common.enums.UserSettingsType;
import gr.cite.intelcomp.stiviewer.query.UserSettingsQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.util.List;
import java.util.UUID;

public class UserSettingsLookup extends Lookup {

    private String like;
    private List<UUID> ids;
    private List<UserSettingsType> userSettingsTypes;

    public UserSettingsQuery enrich(QueryFactory queryFactory) {
        UserSettingsQuery query = queryFactory.query(UserSettingsQuery.class);
        if (this.like != null) query.like(this.like);
        if (this.ids != null) query.ids(this.ids);
        if (this.userSettingsTypes != null) query.userSettingsTypes(this.userSettingsTypes);

        this.enrichCommon(query);

        return query;
    }

}
