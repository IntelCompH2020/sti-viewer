package gr.cite.intelcomp.stiviewer.eventscheduler.processing.datagroupbuild;

import java.util.UUID;

public class CreateDataGroupScheduledEventData {
	private UUID dataGroupRequestId;

	public UUID getDataGroupRequestId() {
		return dataGroupRequestId;
	}

	public void setDataGroupRequestId(UUID dataGroupRequestId) {
		this.dataGroupRequestId = dataGroupRequestId;
	}
}
