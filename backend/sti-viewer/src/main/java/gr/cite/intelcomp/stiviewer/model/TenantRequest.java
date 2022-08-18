package gr.cite.intelcomp.stiviewer.model;


import gr.cite.intelcomp.stiviewer.common.enums.TenantRequestStatus;

import java.time.Instant;
import java.util.UUID;

public class TenantRequest {

	private UUID id;
	public final static String _id = "id";

	private String message;
	public final static String _message = "message";

	private TenantRequestStatus status;
	public final static String _status = "status";

	private User forUser;
	public final static String _forUser = "forUser";

	private String email;
	public final static String _email = "email";
	private Tenant assignedTenant;
	public final static String _assignedTenant = "assignedTenant";

	private Instant createdAt;
	public final static String _createdAt = "createdAt";

	private Instant updatedAt;
	public final static String _updatedAt = "updatedAt";

	public final static String _hash = "hash";
	private String hash;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public TenantRequestStatus getStatus() {
		return status;
	}

	public void setStatus(TenantRequestStatus tenantRequestStatus) {
		this.status = tenantRequestStatus;
	}

	public User getForUser() {
		return forUser;
	}

	public void setForUser(User forUser) {
		this.forUser = forUser;
	}

	public Tenant getAssignedTenant() {
		return assignedTenant;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setAssignedTenant(Tenant assignedTenant) {
		this.assignedTenant = assignedTenant;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}
