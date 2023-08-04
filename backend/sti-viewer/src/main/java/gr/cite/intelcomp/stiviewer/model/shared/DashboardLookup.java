package gr.cite.intelcomp.stiviewer.model.shared;

import gr.cite.intelcomp.stiviewer.model.elasticreport.IndicatorPointReportLookup;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class DashboardLookup  {
    
    @NotNull(message = "{validation.empty}")
    @NotEmpty(message = "{validation.invalidid}")
    private String dashboardId;
    
    @NotNull(message = "{validation.empty}")
    @NotEmpty(message = "{validation.empty}")
    private String token;

    public String getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(String dashboardId) {
        this.dashboardId = dashboardId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
