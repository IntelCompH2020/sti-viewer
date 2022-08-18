package gr.cite.intelcomp.stiviewer.model.persist.dataaccessrequest;

import java.util.List;


public class DataAccessRequestConfigPersist {
	private List<DataAccessRequestIndicatorConfigPersist> indicators;
	private List<DataAccessRequestIndicatorGroupConfigPersist> indicatorGroups;

	public List<DataAccessRequestIndicatorConfigPersist> getIndicators() {
		return indicators;
	}

	public void setIndicators(List<DataAccessRequestIndicatorConfigPersist> indicators) {
		this.indicators = indicators;
	}

	public List<DataAccessRequestIndicatorGroupConfigPersist> getIndicatorGroups() {
		return indicatorGroups;
	}

	public void setIndicatorGroups(List<DataAccessRequestIndicatorGroupConfigPersist> indicatorGroups) {
		this.indicatorGroups = indicatorGroups;
	}
}
