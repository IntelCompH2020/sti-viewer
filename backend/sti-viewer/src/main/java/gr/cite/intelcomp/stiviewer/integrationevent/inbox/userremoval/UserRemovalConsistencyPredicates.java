package gr.cite.intelcomp.stiviewer.integrationevent.inbox.userremoval;

import gr.cite.intelcomp.stiviewer.integrationevent.inbox.ConsistencyPredicates;

import java.util.UUID;

public class UserRemovalConsistencyPredicates implements ConsistencyPredicates {
	public UserRemovalConsistencyPredicates(UUID userId) {
		this.userId = userId;
	}

	private UUID userId;

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}
}
