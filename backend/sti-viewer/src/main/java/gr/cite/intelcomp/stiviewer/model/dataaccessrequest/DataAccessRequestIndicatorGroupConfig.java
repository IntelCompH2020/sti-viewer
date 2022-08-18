package gr.cite.intelcomp.stiviewer.model.dataaccessrequest;

import gr.cite.intelcomp.stiviewer.model.IndicatorGroup;

import java.util.List;

public class DataAccessRequestIndicatorGroupConfig {

	private IndicatorGroup indicatorGroup;
	public static final String _indicatorGroup = "indicatorGroup";

	private List<DataAccessRequestFilterColumnConfig> filterColumns;
	public static final String _filterColumns = "filterColumns";

	public IndicatorGroup getIndicatorGroup() {
		return indicatorGroup;
	}

	public void setIndicatorGroup(IndicatorGroup indicatorGroup) {
		this.indicatorGroup = indicatorGroup;
	}

	public List<DataAccessRequestFilterColumnConfig> getFilterColumns() {
		return filterColumns;
	}

	public void setFilterColumns(List<DataAccessRequestFilterColumnConfig> filterColumns) {
		this.filterColumns = filterColumns;
	}
}
