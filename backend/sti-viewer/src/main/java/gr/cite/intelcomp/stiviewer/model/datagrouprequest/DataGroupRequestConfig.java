package gr.cite.intelcomp.stiviewer.model.datagrouprequest;

import gr.cite.intelcomp.stiviewer.model.IndicatorGroup;

import java.util.List;


public class DataGroupRequestConfig {
	private IndicatorGroup indicatorGroup;
	public static final String _indicatorGroup = "indicatorGroup";

	private List<DataGroupColumn> groupColumns;
	public static final String _groupColumns = "groupColumns";

	public IndicatorGroup getIndicatorGroup() {
		return indicatorGroup;
	}

	public void setIndicatorGroup(IndicatorGroup indicatorGroup) {
		this.indicatorGroup = indicatorGroup;
	}

	public List<DataGroupColumn> getGroupColumns() {
		return groupColumns;
	}

	public void setGroupColumns(List<DataGroupColumn> groupColumns) {
		this.groupColumns = groupColumns;
	}
}
