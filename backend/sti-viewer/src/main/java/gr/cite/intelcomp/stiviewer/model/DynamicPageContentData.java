package gr.cite.intelcomp.stiviewer.model;

import gr.cite.intelcomp.stiviewer.common.enums.DynamicPageType;

import java.util.UUID;

public class DynamicPageContentData {

	private UUID id;
	public final static String _id = "id";

	private String title;
	public final static String _title = "title";

	private String content;
	public final static String _content = "content";

	private DynamicPageType type;
	public final static String _type = "type";

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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

	public DynamicPageType getType() {
		return type;
	}

	public void setType(DynamicPageType type) {
		this.type = type;
	}
}
