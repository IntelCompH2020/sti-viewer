package gr.cite.notification.common.types.accessrequestconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessRequestConfig {

    private List<FilterColumnConfig> filterColumns;

    public List<FilterColumnConfig> getFilterColumns() {
        return filterColumns;
    }

    public void setFilterColumns(List<FilterColumnConfig> filterColumns) {
        this.filterColumns = filterColumns;
    }
}
