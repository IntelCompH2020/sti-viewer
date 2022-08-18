package gr.cite.intelcomp.stiviewer.integrationevent.inbox.userremoval;

import gr.cite.intelcomp.stiviewer.integrationevent.TrackedEvent;

import java.util.UUID;

public class UserRemovalIntegrationEvent extends TrackedEvent {
	private UUID userId;
	private UUID tenant;

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public UUID getTenant() {
		return tenant;
	}

	public void setTenant(UUID tenant) {
		this.tenant = tenant;
	}
}
