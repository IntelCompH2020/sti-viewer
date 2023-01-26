package gr.cite.intelcomp.stiviewer.common.types.portofolioconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortofolioColumnConfigEntity {
	private DataFieldEntity field;
	private int order;
	private Boolean major;
	private List<String> defaultDashboards;
	private List<PortofolioColumnDashboardOverrideEntity> dashboardOverrides;

	public DataFieldEntity getField() {
		return field;
	}

	public void setField(DataFieldEntity field) {
		this.field = field;
	}

	public List<String> getDefaultDashboards() {
		return defaultDashboards;
	}

	public void setDefaultDashboards(List<String> defaultDashboards) {
		this.defaultDashboards = defaultDashboards;
	}

	public List<PortofolioColumnDashboardOverrideEntity> getDashboardOverrides() {
		return dashboardOverrides;
	}

	public void setDashboardOverrides(List<PortofolioColumnDashboardOverrideEntity> dashboardOverrides) {
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


