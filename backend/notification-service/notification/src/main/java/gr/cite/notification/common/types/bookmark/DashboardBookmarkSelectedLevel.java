package gr.cite.notification.common.types.bookmark;

import java.util.Objects;

public class DashboardBookmarkSelectedLevel {

	private String code;

	private String value;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DashboardBookmarkSelectedLevel that = (DashboardBookmarkSelectedLevel) o;

		if (!Objects.equals(code, that.code) ||
				!Objects.equals(value, that.value)) return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = code != null ? code.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}
}
