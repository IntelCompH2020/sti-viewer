package gr.cite.intelcomp.stiviewer.query.lookup;

import gr.cite.intelcomp.stiviewer.common.enums.DataGroupRequestStatus;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.query.DataGroupRequestQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.util.List;
import java.util.UUID;

public class DataGroupRequestLookup extends Lookup {

	private List<UUID> ids;
	private List<IsActive> isActive;
	private List<DataGroupRequestStatus> status;

	public List<UUID> getIds() {
		return ids;
	}

	public void setIds(List<UUID> ids) {
		this.ids = ids;
	}

	public List<IsActive> getIsActive() {
		return isActive;
	}

	public void setIsActive(List<IsActive> isActive) {
		this.isActive = isActive;
	}

	public List<DataGroupRequestStatus> getStatus() {
		return status;
	}

	public void setStatus(List<DataGroupRequestStatus> status) {
		this.status = status;
	}

	public DataGroupRequestQuery enrich(QueryFactory queryFactory) {
		DataGroupRequestQuery query = queryFactory.query(DataGroupRequestQuery.class);
		if (this.ids != null) query.ids(this.ids);
		if (this.isActive != null) query.isActive(this.isActive);
		if (this.status != null) query.status(this.status);

		this.enrichCommon(query);

		return query;
	}

}
