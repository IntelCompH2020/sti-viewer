package gr.cite.intelcomp.stiviewer.model;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;

import java.time.Instant;
import java.util.UUID;

public class IndicatorAccess {

	private UUID id;
	public final static String _id = "id";

	private Tenant tenant;
	public final static String _tenant = "tenant";

	private User user;
	public final static String _user = "user";

	private Indicator indicator;
	public final static String _indicator = "indicator";

	private IsActive isActive;
	public final static String _isActive = "isActive";

	private String hash;
	public final static String _hash = "hash";

	private Instant createdAt;
	public final static String _createdAt = "createdAt";

	private Instant updatedAt;
	public final static String _updatedAt = "updatedAt";

	//private String updatedAt;
	public final static String _config = "config";


	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Indicator getIndicator() {
		return indicator;
	}

	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}

	public IsActive getIsActive() {
		return isActive;
	}

	public void setIsActive(IsActive isActive) {
		this.isActive = isActive;
	}
}
