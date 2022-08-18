package gr.cite.intelcomp.stiviewer.common.types.datagrouprequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataGroupRequestConfigEntity implements Serializable {
	private UUID indicatorGroupId;

	private List<DataGroupColumnEntity> groupColumns;

	public UUID getIndicatorGroupId() {
		return indicatorGroupId;
	}

	public void setIndicatorGroupId(UUID indicatorGroupId) {
		this.indicatorGroupId = indicatorGroupId;
	}

	public List<DataGroupColumnEntity> getGroupColumns() {
		return groupColumns;
	}

	public void setGroupColumns(List<DataGroupColumnEntity> groupColumns) {
		this.groupColumns = groupColumns;
	}
}
