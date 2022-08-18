package gr.cite.intelcomp.stiviewer.elastic.data.indicator;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

public class ValueRangeEntity {
    public final static class Fields {
        public static final String min = "min";
        public static final String max = "max";
        public static final String values = "values";
    }

    @Field(value = Fields.min, type = FieldType.Double)
    private Double min;

    @Field(value = Fields.max, type = FieldType.Double)
    private Double max;

    @Field(value = Fields.values, type = FieldType.Nested)
    private List<ValueRangeValueEntity> values;

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

    public List<ValueRangeValueEntity> getValues() {
        return values;
    }

    public void setValues(List<ValueRangeValueEntity> values) {
        this.values = values;
    }
}
