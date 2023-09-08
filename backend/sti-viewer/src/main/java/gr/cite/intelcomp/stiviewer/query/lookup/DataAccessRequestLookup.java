package gr.cite.intelcomp.stiviewer.query.lookup;

import gr.cite.intelcomp.stiviewer.query.DataAccessRequestQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.util.List;
import java.util.UUID;

public class DataAccessRequestLookup extends Lookup {

    private List<UUID> ids;

    public List<UUID> getIds() {
        return ids;
    }

    public void setIds(List<UUID> ids) {
        this.ids = ids;
    }

    public DataAccessRequestQuery enrich(QueryFactory queryFactory) {
        DataAccessRequestQuery query = queryFactory.query(DataAccessRequestQuery.class);
        if (this.ids != null)
            query.ids(this.ids);

        this.enrichCommon(query);

        return query;
    }

}
