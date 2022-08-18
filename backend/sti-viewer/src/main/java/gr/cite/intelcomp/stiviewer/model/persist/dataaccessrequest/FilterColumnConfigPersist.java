package gr.cite.intelcomp.stiviewer.model.persist.dataaccessrequest;

import java.util.List;

public class FilterColumnConfigPersist {

    private String column;
    private List<String> values;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
