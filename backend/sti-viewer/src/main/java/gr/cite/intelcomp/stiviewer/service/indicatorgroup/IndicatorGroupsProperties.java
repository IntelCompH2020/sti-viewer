package gr.cite.intelcomp.stiviewer.service.indicatorgroup;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.UUID;

@ConfigurationProperties(prefix = "indicator-group")
public class IndicatorGroupsProperties {
	private List<IndicatorGroup> groups;

	public List<IndicatorGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<IndicatorGroup> indicatorGroups) {
		this.groups = indicatorGroups;
	}
}



