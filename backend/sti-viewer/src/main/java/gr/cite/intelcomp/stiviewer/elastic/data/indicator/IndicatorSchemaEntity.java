package gr.cite.intelcomp.stiviewer.elastic.data.indicator;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
import java.util.UUID;

public class IndicatorSchemaEntity {
    public static final class Fields {
        public static final String id = "id";
        public static final String fields ="fields";
    }

    @Field(value = Fields.id, type = FieldType.Keyword)
    private UUID id;

    @Field(value = Fields.fields, type = FieldType.Nested)
    private List<FieldEntity> fields;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<FieldEntity> getFields() {
        return fields;
    }

    public void setFields(List<FieldEntity> fields) {
        this.fields = fields;
    }
}
