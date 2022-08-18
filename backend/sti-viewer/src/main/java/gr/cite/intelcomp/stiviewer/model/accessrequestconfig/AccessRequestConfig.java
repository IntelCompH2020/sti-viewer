package gr.cite.intelcomp.stiviewer.model.accessrequestconfig;

import java.util.List;

public class AccessRequestConfig {

    private List<FilterColumnConfig> filterColumns;
    public static final String _filterColumns ="filterColumns";

    public List<FilterColumnConfig> getFilterColumns() {
        return filterColumns;
    }

    public void setFilterColumns(List<FilterColumnConfig> filterColumns) {
        this.filterColumns = filterColumns;
    }
}
