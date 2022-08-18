package gr.cite.intelcomp.stiviewer.common.types.dataaccessrequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataAccessRequestConfigEntity implements Serializable {
	private List<DataAccessRequestIndicatorConfigEntity> indicators;
	private List<DataAccessRequestIndicatorGroupConfigEntity> indicatorGroups;

	public List<DataAccessRequestIndicatorConfigEntity> getIndicators() {
		return indicators;
	}

	public void setIndicators(List<DataAccessRequestIndicatorConfigEntity> indicators) {
		this.indicators = indicators;
	}

	public List<DataAccessRequestIndicatorGroupConfigEntity> getIndicatorGroups() {
		return indicatorGroups;
	}

	public void setIndicatorGroups(List<DataAccessRequestIndicatorGroupConfigEntity> indicatorGroups) {
		this.indicatorGroups = indicatorGroups;
	}
}
