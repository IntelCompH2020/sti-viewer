package gr.cite.intelcomp.stiviewer.model.persist.externaltoken;

import gr.cite.intelcomp.stiviewer.common.validation.ValidId;
import gr.cite.intelcomp.stiviewer.model.elasticreport.IndicatorPointReportLookup;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class IndicatorPointChartExternalTokenPersist {
	@NotNull(message = "{validation.empty}")
	private IndicatorPointReportLookup lookup;
	@NotNull(message = "{validation.empty}")
	@ValidId(message = "{validation.invalidid}")
	private UUID indicatorId;

	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.empty}")
	private String chartId;

	@NotNull(message = "{validation.empty}")
	@NotEmpty(message = "{validation.invalidid}")
	private String dashboardId;


	public IndicatorPointReportLookup getLookup() {
		return lookup;
	}

	public void setLookup(IndicatorPointReportLookup lookup) {
		this.lookup = lookup;
	}

	public UUID getIndicatorId() {
		return indicatorId;
	}

	public void setIndicatorId(UUID indicatorId) {
		this.indicatorId = indicatorId;
	}

	public String getChartId() {
		return chartId;
	}

	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	public String getDashboardId() {
		return dashboardId;
	}

	public void setDashboardId(String dashboardId) {
		this.dashboardId = dashboardId;
	}
}
