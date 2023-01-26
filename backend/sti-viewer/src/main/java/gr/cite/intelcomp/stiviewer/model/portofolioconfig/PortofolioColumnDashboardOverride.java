package gr.cite.intelcomp.stiviewer.model.portofolioconfig;


import java.util.List;

public class PortofolioColumnDashboardOverride {
	private List<PortofolioColumnDashboardOverrideFieldRequirement> requirements;
	public static final String _requirements = "requirements";
	private List<String> supportedDashboards;
	public static final String _supportedDashboards = "supportedDashboards";

	public List<PortofolioColumnDashboardOverrideFieldRequirement> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<PortofolioColumnDashboardOverrideFieldRequirement> requirements) {
		this.requirements = requirements;
	}

	public List<String> getSupportedDashboards() {
		return supportedDashboards;
	}

	public void setSupportedDashboards(List<String> supportedDashboards) {
		this.supportedDashboards = supportedDashboards;
	}
}


