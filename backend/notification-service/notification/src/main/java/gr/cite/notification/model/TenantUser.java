package gr.cite.notification.model;

import gr.cite.notification.common.enums.IsActive;

import java.time.Instant;
import java.util.UUID;

public class TenantUser {
	public final static String _id = "id";
	private UUID id;

	public final static String _tenant = "tenant";
	private Tenant tenant;

	public final static String _user = "user";
	private User User;

	public final static String _isActive = "isActive";
	private IsActive isActive;

	public final static String _createdAt = "createdAt";
	private Instant createdAt;

	public final static String _updatedAt = "updatedAt";
	private Instant updatedAt;

	public final static String _hash = "hash";
	private String hash;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}


	public IsActive getIsActive() {
		return isActive;
	}

	public void setIsActive(IsActive isActive) {
		this.isActive = isActive;
	}

	public gr.cite.notification.model.User getUser() {
		return User;
	}

	public void setUser(gr.cite.notification.model.User user) {
		User = user;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
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

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}
