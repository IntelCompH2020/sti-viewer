package gr.cite.intelcomp.stiviewer.model;

import java.util.List;
import java.util.UUID;

public class IndicatorGroup {
	private UUID id;
	public static final String _id = "id";
	private String name;
	public static final String _name = "name";
	private String dashboardKey;
	public static final String _dashboardKey = "dashboardKey";

	private List<Indicator> indicators;
	public static final String _indicators = "indicators";

	private List<String> filterColumns;
	public static final String _filterColumns = "filterColumns";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Indicator> getIndicators() {
		return indicators;
	}

	public void setIndicators(List<Indicator> indicators) {
		this.indicators = indicators;
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
