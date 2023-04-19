package gr.cite.intelcomp.stiviewer.model;

import gr.cite.intelcomp.stiviewer.common.enums.DynamicPageType;

import java.util.UUID;

public class DynamicPageMenuItem {

	private UUID id;
	public final static String _id = "id";

	private int order;
	public final static String _order = "order";

	private DynamicPageType type;
	public final static String _type = "type";

	private String title;
	public final static String _title = "title";

	private String externalUrl;
	public final static String _externalUrl = "externalUrl";
	
	private String matIcon;
	public final static String _matIcon = "matIcon";

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getExternalUrl() {
		return externalUrl;
	}

	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}

	public String getMatIcon() {
		return matIcon;
	}

	public void setMatIcon(String matIcon) {
		this.matIcon = matIcon;
	}
}

