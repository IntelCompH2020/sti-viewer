package gr.cite.intelcomp.stiviewer.elastic.query.lookup;

import gr.cite.intelcomp.stiviewer.elastic.query.indicator.IndicatorElasticQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.util.List;
import java.util.UUID;

public class IndicatorElasticLookup extends Lookup {

	private List<UUID> ids;

	public List<UUID> getIds() {
		return ids;
	}

	public void setIds(List<UUID> ids) {
		this.ids = ids;
	}

	public IndicatorElasticQuery enrich(QueryFactory queryFactory) {
		IndicatorElasticQuery query = queryFactory.query(IndicatorElasticQuery.class);
		if (this.ids != null) query.ids(this.ids);

		this.enrichCommon(query);

		return query;
	}
}
