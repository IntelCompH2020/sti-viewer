package gr.cite.intelcomp.stiviewer.model.persist;

import gr.cite.intelcomp.stiviewer.common.enums.DynamicPageType;
import gr.cite.intelcomp.stiviewer.common.enums.DynamicPageVisibility;
import gr.cite.intelcomp.stiviewer.common.validation.FieldNotNullIfOtherSet;
import gr.cite.intelcomp.stiviewer.common.validation.ValidEnum;
import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@FieldNotNullIfOtherSet(notNullField = "id", otherSetField = "hash", failOn = "hash", message = "{validation.hashempty}")
public class PagePersist {
	@ValidId(message = "{validation.invalidid}")
	private UUID id;

	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.empty}")
	@Size(max = 50, message = "{validation.largerthanmax}")
	private String defaultLanguage;

	@ValidEnum(message = "enum is null")
	private DynamicPageVisibility visibility;

	@ValidEnum(message = "enum is null")
	private DynamicPageType type;

	@NotNull(message = "{validation.empty}")
	private int order;
	
	private String hash;

	@Valid
	private List<PageContentPersist> pageContents;

	@Valid
	private PageConfigPersist config;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public DynamicPageVisibility getVisibility() {
		return visibility;
	}

	public void setVisibility(DynamicPageVisibility visibility) {
		this.visibility = visibility;
	}

	public DynamicPageType getType() {
		return type;
	}

	public void setType(DynamicPageType type) {
		this.type = type;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public List<PageContentPersist> getPageContents() {
		return pageContents;
	}

	public void setPageContents(List<PageContentPersist> pageContents) {
		this.pageContents = pageContents;
	}

	public PageConfigPersist getConfig() {
		return config;
	}

	public void setConfig(PageConfigPersist config) {
		this.config = config;
	}
}
