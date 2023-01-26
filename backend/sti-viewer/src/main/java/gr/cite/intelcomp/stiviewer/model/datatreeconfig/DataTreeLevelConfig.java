package gr.cite.intelcomp.stiviewer.model.datatreeconfig;

import java.util.List;

public class DataTreeLevelConfig {
	private DataTreeDataField field;
	public static final String _field = "field";
	private Integer order;
	public static final String _order = "order";

	private Boolean supportSubLevel;
	public static final String _supportSubLevel = "supportSubLevel";

	private List<String> defaultDashboards;
	public static final String _defaultDashboards = "defaultDashboards";

	private List<DataTreeLevelDashboardOverride> dashboardOverrides;
	public static final String _dashboardOverrides = "dashboardOverrides";

	public DataTreeDataField getField() {
		return field;
	}

	public void setField(DataTreeDataField field) {
		this.field = field;
	}

	public Boolean getSupportSubLevel() {
		return supportSubLevel;
	}

	public void setSupportSubLevel(Boolean supportSubLevel) {
		this.supportSubLevel = supportSubLevel;
	}

	public List<String> getDefaultDashboards() {
		return defaultDashboards;
	}

	public void setDefaultDashboards(List<String> defaultDashboards) {
		this.defaultDashboards = defaultDashboards;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public List<DataTreeLevelDashboardOverride> getDashboardOverrides() {
		return dashboardOverrides;
	}

	public void setDashboardOverrides(List<DataTreeLevelDashboardOverride> dashboardOverrides) {
		this.dashboardOverrides = dashboardOverrides;
	}
}

