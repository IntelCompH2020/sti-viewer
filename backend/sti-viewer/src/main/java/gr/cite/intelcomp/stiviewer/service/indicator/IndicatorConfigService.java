package gr.cite.intelcomp.stiviewer.service.indicator;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface IndicatorConfigService {
	IndicatorConfigItem getConfig(UUID indicatorId);

	String ensurePropertyName(@NotNull String prop);
}
