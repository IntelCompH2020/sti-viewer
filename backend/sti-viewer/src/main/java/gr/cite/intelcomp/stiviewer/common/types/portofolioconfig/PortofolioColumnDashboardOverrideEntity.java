package gr.cite.intelcomp.stiviewer.common.types.portofolioconfig;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortofolioColumnDashboardOverrideEntity {
	private List<String> supportedDashboards;
	private List<PortofolioColumnDashboardOverrideFieldRequirementEntity> requirements;

	public List<String> getSupportedDashboards() {
		return supportedDashboards;
	}

	public void setSupportedDashboards(List<String> supportedDashboards) {
		this.supportedDashboards = supportedDashboards;
	}

	public List<PortofolioColumnDashboardOverrideFieldRequirementEntity> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<PortofolioColumnDashboardOverrideFieldRequirementEntity> requirements) {
		this.requirements = requirements;
	}

	public boolean applies(List<PortofolioColumnDashboardOverrideFieldRequirementEntity> item) {
		if (item == null || this.requirements == null || this.requirements.isEmpty() || item.size() < this.requirements.size()) return false;
		for (PortofolioColumnDashboardOverrideFieldRequirementEntity requirement : this.requirements) {
			if (!item.stream().anyMatch(x -> x.equals(requirement))) return false;
		}
		return true;
	}
}
