package gr.cite.intelcomp.stiviewer.model.dataaccessrequest;

import gr.cite.intelcomp.stiviewer.model.Indicator;

import java.util.List;


public class DataAccessRequestIndicatorConfig {

	private Indicator indicator;
	public static final String _indicator = "indicator";

	private List<DataAccessRequestFilterColumnConfig> filterColumns;
	public static final String _filterColumns = "filterColumns";

	public Indicator getIndicator() {
		return indicator;
	}

	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}

	public List<DataAccessRequestFilterColumnConfig> getFilterColumns() {
		return filterColumns;
	}

	public void setFilterColumns(List<DataAccessRequestFilterColumnConfig> filterColumns) {
		this.filterColumns = filterColumns;
	}
}

