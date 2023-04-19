package gr.cite.intelcomp.stiviewer.model;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.enums.DynamicPageType;
import gr.cite.intelcomp.stiviewer.common.enums.DynamicPageVisibility;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class DynamicPage {

	private UUID id;
	public final static String _id = "id";

	private User creator;
	public final static String _creator = "creator";

	private DynamicPageConfig config;
	public final static String _config = "config";

	private DynamicPageVisibility visibility;
	public final static String _visibility = "visibility";

	private int order;
	public final static String _order = "order";

	private DynamicPageType type;
	public final static String _type = "type";

	private String defaultLanguage;
	public final static String _defaultLanguage = "defaultLanguage";

	private Instant createdAt;
	public final static String _createdAt = "createdAt";

	private Instant updatedAt;
	public final static String _updatedAt = "updatedAt";

	private IsActive isActive;
	public final static String _isActive = "isActive";

	private String hash;
	public final static String _hash = "hash";

	private List<DynamicPageContent> dynamicPageContents;
	public final static String _pageContents = "pageContents";

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public DynamicPageConfig getConfig() {
		return config;
	}

	public void setConfig(DynamicPageConfig config) {
		this.config = config;
	}

	public DynamicPageVisibility getVisibility() {
		return visibility;
	}

	public void setVisibility(DynamicPageVisibility visibility) {
		this.visibility = visibility;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
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

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public List<DynamicPageContent> getPageContents() {
		return dynamicPageContents;
	}

	public void setPageContents(List<DynamicPageContent> dynamicPageContents) {
		this.dynamicPageContents = dynamicPageContents;
	}
}

