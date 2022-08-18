package gr.cite.intelcomp.stiviewer.model.datagrouprequest;

import gr.cite.intelcomp.stiviewer.common.enums.DataGroupRequestStatus;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.model.Tenant;
import gr.cite.intelcomp.stiviewer.model.User;

import java.time.Instant;
import java.util.UUID;

public class DataGroupRequest {

	private UUID id;
	public final static String _id = "id";

	private Tenant tenant;
	public final static String _tenant = "tenant";

	private String groupHash;
	public final static String _groupHash = "groupHash";

	private String name;
	public final static String _name = "name";

	private DataGroupRequestStatus status;
	public final static String _status = "status";

	private User user;
	public final static String _user = "user";


	public final static String _hash = "hash";
	private String hash;

	public final static String _isActive = "isActive";
	private IsActive isActive;

	private Instant createdAt;
	public final static String _createdAt = "createdAt";

	private Instant updatedAt;
	public final static String _updatedAt = "updatedAt";

	private DataGroupRequestConfig config;
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

	public DataGroupRequestStatus getStatus() {
		return status;
	}

	public void setStatus(DataGroupRequestStatus status) {
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

	public DataGroupRequestConfig getConfig() {
		return config;
	}

	public void setConfig(DataGroupRequestConfig config) {
		this.config = config;
	}

	public String getGroupHash() {
		return groupHash;
	}

	public void setGroupHash(String groupHash) {
		this.groupHash = groupHash;
	}

	public IsActive getIsActive() {
		return isActive;
	}

	public void setIsActive(IsActive isActive) {
		this.isActive = isActive;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
