package gr.cite.intelcomp.stiviewer.model.persist;

import gr.cite.intelcomp.stiviewer.common.validation.ValidId;
import gr.cite.intelcomp.stiviewer.model.persist.indicatoraccess.IndicatorAccessConfigPersist;

import javax.validation.constraints.NotNull;
import java.util.UUID;


public class IndicatorAccessPersist {

	@ValidId(message = "{validation.invalidid}")
	private UUID id;

	private UUID userId;

	@NotNull(message = "{validation.empty}")
	private UUID indicatorId;

	private IndicatorAccessConfigPersist config;

	private String hash;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public UUID getIndicatorId() {
		return indicatorId;
	}

	public void setIndicatorId(UUID indicatorId) {
		this.indicatorId = indicatorId;
	}

	public IndicatorAccessConfigPersist getConfig() {
		return config;
	}

	public void setConfig(IndicatorAccessConfigPersist config) {
		this.config = config;
	}
}
