package gr.cite.intelcomp.stiviewer.model.dataaccessrequest;

import java.util.List;


public class DataAccessRequestConfig {
	private List<DataAccessRequestIndicatorConfig> indicators;
	public static final String _indicators = "indicators";

	private List<DataAccessRequestIndicatorGroupConfig> indicatorGroups;
	public static final String _indicatorGroups = "indicatorGroups";

	public List<DataAccessRequestIndicatorConfig> getIndicators() {
		return indicators;
	}

	public void setIndicators(List<DataAccessRequestIndicatorConfig> indicators) {
		this.indicators = indicators;
	}

	public List<DataAccessRequestIndicatorGroupConfig> getIndicatorGroups() {
		return indicatorGroups;
	}

	public void setIndicatorGroups(List<DataAccessRequestIndicatorGroupConfig> indicatorGroups) {
		this.indicatorGroups = indicatorGroups;
	}
}
