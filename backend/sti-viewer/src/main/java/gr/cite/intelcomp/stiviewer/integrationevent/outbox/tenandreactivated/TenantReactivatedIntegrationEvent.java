package gr.cite.intelcomp.stiviewer.integrationevent.outbox.tenandreactivated;

import gr.cite.intelcomp.stiviewer.integrationevent.TrackedEvent;

import java.util.UUID;

public class TenantReactivatedIntegrationEvent extends TrackedEvent {

	private UUID id;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
}
