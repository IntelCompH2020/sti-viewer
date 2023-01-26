package gr.cite.intelcomp.stiviewer.common.types.portofolioconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortofolioColumnDashboardOverrideFieldRequirementEntity {
	private String value;

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

		PortofolioColumnDashboardOverrideFieldRequirementEntity that = (PortofolioColumnDashboardOverrideFieldRequirementEntity) o;

		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		int result = 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}
}
