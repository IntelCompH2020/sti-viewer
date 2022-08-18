package gr.cite.intelcomp.stiviewer.common.types.indicatorgroup;

import java.util.List;
import java.util.UUID;

public class IndicatorGroupEntity {
	private UUID id;
	private String name;
	private String dashboardKey;
	private List<UUID> indicatorIds;
	private List<String> filterColumns;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<UUID> getIndicatorIds() {
		return indicatorIds;
	}

	public void setIndicatorIds(List<UUID> indicatorIds) {
		this.indicatorIds = indicatorIds;
	}

	public List<String> getFilterColumns() {
		return filterColumns;
	}

	public void setFilterColumns(List<String> filterColumns) {
		this.filterColumns = filterColumns;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getDashboardKey() {
		return dashboardKey;
	}

	public void setDashboardKey(String dashboardKey) {
		this.dashboardKey = dashboardKey;
	}
}
