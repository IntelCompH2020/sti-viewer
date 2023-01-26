package gr.cite.intelcomp.stiviewer.model.persist.indicator;

import javax.validation.Valid;
import java.util.List;

public class AccessRequestConfigPersist {
    @Valid
    private List<FilterColumnConfigPersist> filterColumns;

    public List<FilterColumnConfigPersist> getFilterColumns() {
        return filterColumns;
    }

    public void setFilterColumns(List<FilterColumnConfigPersist> filterColumns) {
        this.filterColumns = filterColumns;
    }
}
