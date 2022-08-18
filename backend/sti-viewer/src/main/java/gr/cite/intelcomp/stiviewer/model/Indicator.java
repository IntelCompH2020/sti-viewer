package gr.cite.intelcomp.stiviewer.model;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.model.accessrequestconfig.AccessRequestConfig;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Indicator {

	public final static String _id = "id";
	private UUID id;

	public final static String _code = "code";
	private String code;

	public final static String _name = "name";
	private String name;

	public final static String _description = "description";
	private String description;

	public final static String _createdAt = "createdAt";
	private Instant createdAt;

	public final static String _updatedAt = "updatedAt";
	private Instant updatedAt;

	public final static String _isActive = "isActive";
	private IsActive isActive;

	public final static String _hash = "hash";
	private String hash;

	public final static String _indicatorAccesses = "indicatorAccesses";
	private List<IndicatorAccess> indicatorAccesses;

	public final static String _config = "config";
	private AccessRequestConfig config;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public IsActive getIsActive() {
		return isActive;
	}

	public void setIsActive(IsActive isActive) {
		this.isActive = isActive;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public List<IndicatorAccess> getIndicatorAccesses() {
		return indicatorAccesses;
	}

	public void setIndicatorAccesses(List<IndicatorAccess> indicatorAccesses) {
		this.indicatorAccesses = indicatorAccesses;
	}

	public AccessRequestConfig getConfig() {
		return config;
	}

	public void setConfig(AccessRequestConfig config) {
		this.config = config;
	}
}
