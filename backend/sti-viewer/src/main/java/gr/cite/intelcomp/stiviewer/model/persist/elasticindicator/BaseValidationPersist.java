package gr.cite.intelcomp.stiviewer.model.persist.elasticindicator;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gr.cite.intelcomp.stiviewer.common.enums.IndicatorPointValidationType;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true) @JsonSubTypes({
        @JsonSubTypes.Type(value = ValidationRequiredPersist.class, name = "required"),
        @JsonSubTypes.Type(value = ValidationAllowedValuesPersist.class, name = "allowed_values"),
        @JsonSubTypes.Type(value = ValidationValueRangePersist.class, name = "value_range")
})
public  abstract class BaseValidationPersist {
    private IndicatorPointValidationType type;

    public BaseValidationPersist(IndicatorPointValidationType type) {
        this.type = type;
    }

    public IndicatorPointValidationType getType() {
        return type;
    }

    public void setType(IndicatorPointValidationType type) {
        this.type = type;
    }
}
