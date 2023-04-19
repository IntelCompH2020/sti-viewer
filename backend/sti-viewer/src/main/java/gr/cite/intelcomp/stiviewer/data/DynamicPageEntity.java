package gr.cite.intelcomp.stiviewer.data;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.enums.DynamicPageType;
import gr.cite.intelcomp.stiviewer.common.enums.DynamicPageVisibility;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dynamic_page")
public class DynamicPageEntity {

	@Id
	@Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;
	public final static String _id = "id";

	@Column(name = "creator_id", nullable = true)
	private UUID creatorId;
	public final static String _creatorId = "creatorId";

	@Column(name = "visibility", length = 200, nullable = false)
	@Enumerated(EnumType.STRING)
	private DynamicPageVisibility visibility;
	public final static String _visibility = "visibility";

	@Column(name = "\"order\"", nullable = false)
	private Integer order;
	public final static String _order = "order";

	@Column(name = "config", nullable = true)
	private String config;
	public final static String _config = "config";

	@Column(name = "type", length = 200, nullable = false)
	@Enumerated(EnumType.STRING)
	private DynamicPageType type;
	public final static String _type = "type";

	@Column(name = "default_language", length = 50, nullable = false)
	private String defaultLanguage;
	public final static String _defaultLanguage = "defaultLanguage";

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;
	public final static String _createdAt = "createdAt";

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;
	public final static String _updatedAt = "updatedAt";

	@Column(name = "is_active", length = 100, nullable = false)
	@Enumerated(EnumType.STRING)
	private IsActive isActive;
	public final static String _isActive = "isActive";

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(UUID creatorId) {
		this.creatorId = creatorId;
	}

	public DynamicPageVisibility getVisibility() {
		return visibility;
	}

	public void setVisibility(DynamicPageVisibility visibility) {
		this.visibility = visibility;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public DynamicPageType getType() {
		return type;
	}

	public void setType(DynamicPageType type) {
		this.type = type;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
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
}

