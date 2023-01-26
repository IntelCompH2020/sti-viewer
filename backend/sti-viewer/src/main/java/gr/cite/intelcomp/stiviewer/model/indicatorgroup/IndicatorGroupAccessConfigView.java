package gr.cite.intelcomp.stiviewer.model.indicatorgroup;


import gr.cite.intelcomp.stiviewer.model.IndicatorGroup;

import java.util.List;

public class IndicatorGroupAccessConfigView {

    public static final String _group = "group";
    private IndicatorGroup group;

    public static final String _filterColumns = "filterColumns";
    private List<IndicatorGroupAccessColumnConfigView> filterColumns;

    public IndicatorGroup getGroup() {
        return group;
    }

    public void setGroup(IndicatorGroup group) {
        this.group = group;
    }

    public List<IndicatorGroupAccessColumnConfigView> getFilterColumns() {
        return filterColumns;
    }

    public void setFilterColumns(List<IndicatorGroupAccessColumnConfigView> filterColumns) {
        this.filterColumns = filterColumns;
    }
}
