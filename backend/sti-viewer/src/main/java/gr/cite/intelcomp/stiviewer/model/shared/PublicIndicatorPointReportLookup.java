package gr.cite.intelcomp.stiviewer.model.shared;

import gr.cite.intelcomp.stiviewer.common.validation.ValidId;
import gr.cite.intelcomp.stiviewer.model.elasticreport.IndicatorPointReportLookup;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class PublicIndicatorPointReportLookup extends IndicatorPointReportLookup {
    
    @NotNull(message = "{validation.empty}")
    @NotEmpty(message = "{validation.invalidid}")
    private String chartId;
    
    @NotNull(message = "{validation.empty}")
    @NotEmpty(message = "{validation.invalidid}")
    private String dashboardId;

    @NotNull(message = "{validation.empty}")
    @NotEmpty(message = "{validation.empty}")
    private String token;

    @NotNull(message = "{validation.empty}")
    @ValidId(message = "{validation.invalidid}")
    private UUID indicatorId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
