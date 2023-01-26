package gr.cite.intelcomp.stiviewer.service.indicatorpoint;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.UUID;

@ConfigurationProperties(prefix = "indicator-point")
public class IndicatorPointProperties {
	private int indicatorPointImportBatchSize;

	public int getIndicatorPointImportBatchSize() {
		return indicatorPointImportBatchSize;
	}

	public void setIndicatorPointImportBatchSize(int indicatorPointImportBatchSize) {
		this.indicatorPointImportBatchSize = indicatorPointImportBatchSize;
	}
}
