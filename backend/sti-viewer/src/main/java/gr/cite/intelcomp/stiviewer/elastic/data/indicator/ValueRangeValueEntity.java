package gr.cite.intelcomp.stiviewer.elastic.data.indicator;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class ValueRangeValueEntity {
    public static final class Fields {
        public static final String value = "value";
        public static final String label = "label";
    }

    @Field(value = Fields.value, type = FieldType.Text)
    private String value;

    @Field(value = Fields.label, type = FieldType.Text)
    private String label;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
