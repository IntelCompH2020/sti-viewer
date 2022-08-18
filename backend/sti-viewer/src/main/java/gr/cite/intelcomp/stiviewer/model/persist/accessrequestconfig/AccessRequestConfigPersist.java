package gr.cite.intelcomp.stiviewer.model.persist.accessrequestconfig;

import java.util.List;

public class AccessRequestConfigPersist {

    private List<FilterColumnConfigPersist> filterColumns;
    public static final String _filterColumns ="filterColumns";

    public List<FilterColumnConfigPersist> getFilterColumns() {
        return filterColumns;
    }

    public void setFilterColumns(List<FilterColumnConfigPersist> filterColumns) {
        this.filterColumns = filterColumns;
    }
}
