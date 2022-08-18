package gr.cite.intelcomp.stiviewer.integrationevent.outbox.tenantremoved;

import gr.cite.intelcomp.stiviewer.integrationevent.TrackedEvent;

import java.util.UUID;

public class TenantRemovedIntegrationEvent extends TrackedEvent {

	private UUID id;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
}
