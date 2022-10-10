package gr.cite.intelcomp.stiviewer.common.types.datatreeconfig;

import java.util.Objects;

public class DataTreeLevelDashboardOverrideFieldRequirementEntity {
	private String field;
	private String value;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
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

		DataTreeLevelDashboardOverrideFieldRequirementEntity that = (DataTreeLevelDashboardOverrideFieldRequirementEntity) o;

		if (!Objects.equals(field, that.field)) return false;
		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		int result = field != null ? field.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}
}
