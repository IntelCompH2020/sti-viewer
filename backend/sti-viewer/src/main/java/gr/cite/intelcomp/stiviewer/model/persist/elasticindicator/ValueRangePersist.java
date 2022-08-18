package gr.cite.intelcomp.stiviewer.model.persist.elasticindicator;

import java.util.List;

public class ValueRangePersist {

    private Double min;

    private Double max;

    private List<ValueRangeValuePersist> values;

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public List<ValueRangeValuePersist> getValues() {
        return values;
    }

    public void setValues(List<ValueRangeValuePersist> values) {
        this.values = values;
    }
}
