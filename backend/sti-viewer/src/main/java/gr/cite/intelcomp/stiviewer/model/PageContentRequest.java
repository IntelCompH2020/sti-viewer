package gr.cite.intelcomp.stiviewer.model;

import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

public class PageContentRequest {
	@ValidId(message = "{validation.invalidid}")
	@NotNull(message = "{validation.empty}")
	private UUID id;

	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.empty}")
	@Size(max = 50, message = "{validation.largerthanmax}")
	private String language;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
