package gr.cite.intelcomp.stiviewer.model;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;

import java.time.Instant;
import java.util.UUID;

public class DetailItem {
	public final static String _id = "id";
	private UUID id;

	public final static String _name = "name";
	private String name;

	public final static String _isActive = "isActive";
	private IsActive isActive;

	public final static String _createdAt = "createdAt";
	private Instant createdAt;

	public final static String _updatedAt = "updatedAt";
	private Instant updatedAt;

	public final static String _hash = "hash";
	private String hash;

	public final static String _master = "master";
	private MasterItem master;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public MasterItem getMaster() {
		return master;
	}

	public void setMaster(MasterItem master) {
		this.master = master;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IsActive getIsActive() {
		return isActive;
	}

	public void setIsActive(IsActive isActive) {
		this.isActive = isActive;
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
