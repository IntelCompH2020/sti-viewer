package gr.cite.intelcomp.stiviewer.model.indicatorpoint;

import java.util.List;

public class DataGroupInfo {

	public final static String _columns = "columns";
	private List<DataGroupInfoColumn> columns;

	public List<DataGroupInfoColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<DataGroupInfoColumn> columns) {
		this.columns = columns;
	}
}
