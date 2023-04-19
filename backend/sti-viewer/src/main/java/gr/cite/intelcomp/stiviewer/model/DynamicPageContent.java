package gr.cite.intelcomp.stiviewer.model;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;

import java.time.Instant;
import java.util.UUID;

public class DynamicPageContent {
	private UUID id;
	public final static String _id = "id";

	private DynamicPage dynamicPage;
	public final static String _page = "page";

	private String language;
	public final static String _language = "language";

	private String title;
	public final static String _title = "title";

	private String content;
	public final static String _content = "content";

	private Instant createdAt;
	public final static String _createdAt = "createdAt";

	private Instant updatedAt;
	public final static String _updatedAt = "updatedAt";

	private IsActive isActive;
	public final static String _isActive = "isActive";

	private String hash;
	public final static String _hash = "hash";

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public DynamicPage getPage() {
		return dynamicPage;
	}

	public void setPage(DynamicPage dynamicPage) {
		this.dynamicPage = dynamicPage;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
}
