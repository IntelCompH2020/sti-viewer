package gr.cite.intelcomp.stiviewer.model.dataaccessrequest;

import gr.cite.intelcomp.stiviewer.common.enums.DataAccessRequestStatus;
import gr.cite.intelcomp.stiviewer.model.Tenant;
import gr.cite.intelcomp.stiviewer.model.User;

import java.time.Instant;
import java.util.UUID;

public class DataAccessRequest {

	private UUID id;
	public final static String _id = "id";

	private Tenant tenant;
	public final static String _tenant = "tenant";

	private DataAccessRequestStatus status;
	public final static String _status = "status";

	private User user;
	public final static String _user = "user";

	public final static String _hash = "hash";
	private String hash;

	private Instant createdAt;
	public final static String _createdAt = "createdAt";

	private Instant updatedAt;
	public final static String _updatedAt = "updatedAt";

	private DataAccessRequestConfig config;
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

	public DataAccessRequestStatus getStatus() {
		return status;
	}

	public void setStatus(DataAccessRequestStatus status) {
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public DataAccessRequestConfig getConfig() {
		return config;
	}

	public void setConfig(DataAccessRequestConfig config) {
		this.config = config;
	}
}
