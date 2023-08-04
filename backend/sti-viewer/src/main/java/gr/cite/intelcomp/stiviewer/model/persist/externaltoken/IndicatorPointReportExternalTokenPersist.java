package gr.cite.intelcomp.stiviewer.model.persist.externaltoken;

import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IndicatorPointReportExternalTokenPersist {
	@NotNull(message = "{validation.empty}")
	private List<IndicatorPointChartExternalTokenPersist> lookups;

	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.empty}")
	@Size(max = 1024, message = "{validation.largerthanmax}")
	private String name;
	
	private Instant expiresAt;

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

	public List<IndicatorPointChartExternalTokenPersist> getLookups() {
		return lookups;
	}

	public void setLookups(List<IndicatorPointChartExternalTokenPersist> lookups) {
		this.lookups = lookups;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
