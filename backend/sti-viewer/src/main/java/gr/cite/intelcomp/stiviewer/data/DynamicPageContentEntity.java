package gr.cite.intelcomp.stiviewer.data;

import gr.cite.intelcomp.stiviewer.common.enums.IsActive;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dynamic_page_content")
public class DynamicPageContentEntity {

	@Id
	@Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;
	public final static String _id = "id";

	@Column(name = "page_id", nullable = false)
	private UUID pageId;
	public final static String _pageId = "pageId";

	@Column(name = "language", length = 50, nullable = false)
	private String language;
	public final static String _language = "language";

	@Column(name = "title", length = 500, nullable = false)
	private String title;
	public final static String _title = "title";

	@Column(name = "content", nullable = true)
	private String content;
	public final static String _content = "content";

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

	public UUID getPageId() {
		return pageId;
	}

	public void setPageId(UUID pageId) {
		this.pageId = pageId;
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
}
