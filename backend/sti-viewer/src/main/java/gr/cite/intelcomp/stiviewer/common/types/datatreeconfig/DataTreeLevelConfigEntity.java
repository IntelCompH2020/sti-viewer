package gr.cite.intelcomp.stiviewer.common.types.datatreeconfig;

import java.util.List;

public class DataTreeLevelConfigEntity {
	private DataFieldEntity field;
	private int order;
	private Boolean supportSubLevel;
	private List<String> defaultDashboards;
	private List<DataTreeLevelDashboardOverrideEntity> dashboardOverrides;

	public DataFieldEntity getField() {
		return field;
	}

	public void setField(DataFieldEntity field) {
		this.field = field;
	}

	public Boolean getSupportSubLevel() {
		return supportSubLevel;
	}

	public void setSupportSubLevel(Boolean supportSubLevel) {
		this.supportSubLevel = supportSubLevel;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public List<String> getDefaultDashboards() {
		return defaultDashboards;
	}

	public void setDefaultDashboards(List<String> defaultDashboards) {
		this.defaultDashboards = defaultDashboards;
	}

	public List<DataTreeLevelDashboardOverrideEntity> getDashboardOverrides() {
		return dashboardOverrides;
	}

	public void setDashboardOverrides(List<DataTreeLevelDashboardOverrideEntity> dashboardOverrides) {
		this.dashboardOverrides = dashboardOverrides;
	}
}


