package gr.cite.intelcomp.stiviewer.eventscheduler.processing.datagroupbuild;

import gr.cite.intelcomp.stiviewer.integrationevent.inbox.ConsistencyPredicates;

import java.util.UUID;

public class CreateDataGroupConsistencyPredicates implements ConsistencyPredicates {
	public CreateDataGroupConsistencyPredicates(String groupHash, UUID requestId) {
		this.groupHash = groupHash;
		this.requestId = requestId;
	}

	private String groupHash;
	private UUID requestId;

	public String getGroupHash() {
		return groupHash;
	}

	public void setGroupHash(String groupHash) {
		this.groupHash = groupHash;
	}

	public UUID getRequestId() {
		return requestId;
	}

	public void setRequestId(UUID requestId) {
		this.requestId = requestId;
	}
}
