package gr.cite.intelcomp.stiviewer.integrationevent.outbox.tenanttouched;

import gr.cite.intelcomp.stiviewer.integrationevent.TrackedEvent;

import java.util.UUID;

public class TenantTouchedIntegrationEvent extends TrackedEvent {

	private UUID id;

	private String Code;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}
}
