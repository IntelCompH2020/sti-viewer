package gr.cite.intelcomp.stiviewer.query.lookup;

import gr.cite.intelcomp.stiviewer.elastic.query.lookup.IndicatorPointLookup;
import gr.cite.tools.data.query.Lookup;

import java.util.List;

public class IndicatorReportLevelLookup extends Lookup {

    private String configId;
    private String parentConfigId;
    private IndicatorPointLookup filters;
    private List<String> selectedLevels;

    public List<String> getSelectedLevels() {
        return selectedLevels;
    }

    public void setSelectedLevels(List<String> selectedLevels) {
        this.selectedLevels = selectedLevels;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public IndicatorPointLookup getFilters() {
        return filters;
    }

    public void setFilters(IndicatorPointLookup filters) {
        this.filters = filters;
    }

    public String getParentConfigId() {
        return parentConfigId;
    }

    public void setParentConfigId(String parentConfigId) {
        this.parentConfigId = parentConfigId;
    }
}
