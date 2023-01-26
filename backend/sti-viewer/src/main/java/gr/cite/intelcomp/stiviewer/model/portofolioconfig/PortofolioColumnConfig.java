package gr.cite.intelcomp.stiviewer.model.portofolioconfig;


import java.util.List;

public class PortofolioColumnConfig {
	private DataField field;
	public static final String _field = "field";

	private List<String> defaultDashboards;
	public static final String _defaultDashboards = "defaultDashboards";

	private int order;
	public static final String _order = "order";

	private Boolean major;
	public static final String _major = "major";

	private List<PortofolioColumnDashboardOverride> dashboardOverrides;
	public static final String _dashboardOverrides = "dashboardOverrides";

	public DataField getField() {
		return field;
	}

	public void setField(DataField field) {
		this.field = field;
	}

	public List<String> getDefaultDashboards() {
		return defaultDashboards;
	}

	public void setDefaultDashboards(List<String> defaultDashboards) {
		this.defaultDashboards = defaultDashboards;
	}

	public List<PortofolioColumnDashboardOverride> getDashboardOverrides() {
		return dashboardOverrides;
	}

	public void setDashboardOverrides(List<PortofolioColumnDashboardOverride> dashboardOverrides) {
		this.dashboardOverrides = dashboardOverrides;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Boolean getMajor() {
		return major;
	}

	public void setMajor(Boolean major) {
		this.major = major;
	}
}

