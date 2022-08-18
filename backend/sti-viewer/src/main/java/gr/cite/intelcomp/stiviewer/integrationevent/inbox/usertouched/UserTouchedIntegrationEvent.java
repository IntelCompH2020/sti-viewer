package gr.cite.intelcomp.stiviewer.integrationevent.inbox.usertouched;

import gr.cite.intelcomp.stiviewer.integrationevent.TrackedEvent;

import java.util.UUID;

public class UserTouchedIntegrationEvent extends TrackedEvent {
	private UUID id;
	private UUID tenant;
	private String firstName;
	private String lastName;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getTenant() {
		return tenant;
	}

	public void setTenant(UUID tenant) {
		this.tenant = tenant;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
