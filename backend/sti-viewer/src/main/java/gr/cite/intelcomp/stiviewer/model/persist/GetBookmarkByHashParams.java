package gr.cite.intelcomp.stiviewer.model.persist;

import gr.cite.intelcomp.stiviewer.common.enums.BookmarkType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class GetBookmarkByHashParams {
	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.empty}")
	private String value;

	@NotNull(message = "{validation.empty}")
	@Enumerated(EnumType.STRING)
	private BookmarkType type;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public BookmarkType getType() {
		return type;
	}

	public void setType(BookmarkType type) {
		this.type = type;
	}
}
