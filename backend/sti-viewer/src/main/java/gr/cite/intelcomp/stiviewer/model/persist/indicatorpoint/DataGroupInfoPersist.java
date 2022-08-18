package gr.cite.intelcomp.stiviewer.model.persist.indicatorpoint;

import javax.validation.Valid;
import java.util.List;

public class DataGroupInfoPersist {

	@Valid
	private List<DataGroupInfoColumnPersist> columns;

	public List<DataGroupInfoColumnPersist> getColumns() {
		return columns;
	}

	public void setColumns(List<DataGroupInfoColumnPersist> columns) {
		this.columns = columns;
	}
}
