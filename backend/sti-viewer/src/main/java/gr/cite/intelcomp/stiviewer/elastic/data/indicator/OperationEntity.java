package gr.cite.intelcomp.stiviewer.elastic.data.indicator;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class OperationEntity {
    public static final class Fields {
        public static final String op = "op";
    }

    @Field(value = Fields.op, type = FieldType.Keyword) //Enum?
    private String op;


    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }
}
