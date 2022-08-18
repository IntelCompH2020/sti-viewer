package gr.cite.intelcomp.stiviewer.service.indicator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IndicatorsConfig {
	private final Map<UUID, IndicatorConfigItem> indicators;

	public IndicatorsConfig() {
		this.indicators = new HashMap<>();
	}

	public Map<UUID, IndicatorConfigItem> getIndicators() {
		return indicators;
	}
}
