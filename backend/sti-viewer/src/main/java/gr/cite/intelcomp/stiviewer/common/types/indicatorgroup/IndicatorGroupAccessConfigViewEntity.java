package gr.cite.intelcomp.stiviewer.common.types.indicatorgroup;

import java.util.List;
import java.util.UUID;

public class IndicatorGroupAccessConfigViewEntity {
	private UUID id;
	private List<IndicatorGroupAccessColumnConfigViewEntity> filterColumns;

	public List<IndicatorGroupAccessColumnConfigViewEntity> getFilterColumns() {
		return filterColumns;
	}

	public void setFilterColumns(List<IndicatorGroupAccessColumnConfigViewEntity> filterColumns) {
		this.filterColumns = filterColumns;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
}
