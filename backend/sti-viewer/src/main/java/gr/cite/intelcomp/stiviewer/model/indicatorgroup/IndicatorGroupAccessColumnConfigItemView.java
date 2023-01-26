package gr.cite.intelcomp.stiviewer.model.indicatorgroup;

import gr.cite.intelcomp.stiviewer.common.enums.DataAccessRequestStatus;

public class IndicatorGroupAccessColumnConfigItemView {

    public static final String _value = "value";
    private String value;
    
    public static final String _status = "status";
    private DataAccessRequestStatus status;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DataAccessRequestStatus getStatus() {
        return status;
    }

    public void setStatus(DataAccessRequestStatus status) {
        this.status = status;
    }
}
