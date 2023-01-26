package gr.cite.intelcomp.stiviewer.common.types.indicatorgroup;

import gr.cite.intelcomp.stiviewer.common.enums.DataAccessRequestStatus;

public class IndicatorGroupAccessColumnConfigItemViewEntity {
	private String value;
	private DataAccessRequestStatus status;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public DataAccessRequestStatus getStatus() {
		return status;
	}

	public void setStatus(DataAccessRequestStatus status) {
		this.status = status;
	}
}
