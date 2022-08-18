package gr.cite.intelcomp.stiviewer.query.lookup;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.query.DetailItemQuery;
import gr.cite.tools.data.query.Lookup;
import gr.cite.tools.data.query.QueryFactory;

import java.util.List;
import java.util.UUID;

public class DetailItemLookup extends Lookup {
	private String like;
	private List<IsActive> isActive;
	private List<UUID> ids;
	private List<UUID> masterItemIds;

	private MasterItemLookup masterItemSubQuery;

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

	public List<UUID> getMasterItemIds() { return masterItemIds; }

	public void setMasterItemIds(List<UUID> masterItemIds) { this.masterItemIds = masterItemIds; }

	public MasterItemLookup getMasterItemSubQuery() {
		return masterItemSubQuery;
	}

	public void setMasterItemSubQuery(MasterItemLookup masterItemSubQuery) {
		this.masterItemSubQuery = masterItemSubQuery;
	}

	public DetailItemQuery enrich(QueryFactory queryFactory) {
		DetailItemQuery query = queryFactory.query(DetailItemQuery.class);
		if (this.like != null) query.like(this.like);
		if (this.isActive != null) query.isActive(this.isActive);
		if (this.ids != null) query.ids(this.ids);
		if (this.masterItemIds != null) query.masterItemIds(this.masterItemIds);
		if (this.masterItemSubQuery != null) query.masterItemSubQuery(this.masterItemSubQuery.enrich(queryFactory));

		this.enrichCommon(query);

		return query;
	}
}
