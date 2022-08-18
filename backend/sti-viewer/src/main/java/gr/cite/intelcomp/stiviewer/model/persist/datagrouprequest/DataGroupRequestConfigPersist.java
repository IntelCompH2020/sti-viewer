package gr.cite.intelcomp.stiviewer.model.persist.datagrouprequest;

import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;


public class DataGroupRequestConfigPersist {

	@ValidId(message = "{validation.invalidid}")
	private UUID indicatorGroupId;


	@NotNull(message = "{validation.empty}")
	@Valid
	private List<DataGroupColumnPersist> groupColumns;

	public UUID getIndicatorGroupId() {
		return indicatorGroupId;
	}

	public void setIndicatorGroupId(UUID indicatorGroupId) {
		this.indicatorGroupId = indicatorGroupId;
	}

	public List<DataGroupColumnPersist> getGroupColumns() {
		return groupColumns;
	}

	public void setGroupColumns(List<DataGroupColumnPersist> groupColumns) {
		this.groupColumns = groupColumns;
	}
}
