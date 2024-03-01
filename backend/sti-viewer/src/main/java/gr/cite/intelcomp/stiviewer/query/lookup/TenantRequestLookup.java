package gr.cite.intelcomp.stiviewer.query.lookup;

import gr.cite.intelcomp.stiviewer.common.enums.TenantRequestStatus;
import gr.cite.intelcomp.stiviewer.query.TenantRequestQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.util.List;
import java.util.UUID;

public class TenantRequestLookup extends Lookup {
	private String like;
	private List<TenantRequestStatus> tenantRequestStatuses;
	private List<UUID> ids;

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}

	public List<TenantRequestStatus> getStatuses() {
		return tenantRequestStatuses;
	}

	public void setStatuses(List<TenantRequestStatus> tenantRequestStatuses) {
		this.tenantRequestStatuses = tenantRequestStatuses;
	}

	public List<UUID> getIds() {
		return ids;
	}

	public void setIds(List<UUID> ids) {
		this.ids = ids;
	}

	public TenantRequestQuery enrich(QueryFactory queryFactory) {
		TenantRequestQuery query = queryFactory.query(TenantRequestQuery.class);
		if (this.like != null) query.like(this.like);
		if (this.tenantRequestStatuses != null) query.statuses(this.tenantRequestStatuses);
		if (this.ids != null) query.ids(this.ids);

		this.enrichCommon(query);

		return query;
	}

}
