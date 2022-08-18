package gr.cite.intelcomp.stiviewer.model.persist.elasticindicator;

import com.fasterxml.jackson.annotation.*;
import gr.cite.intelcomp.stiviewer.common.enums.IndicatorPointValidationType;


@JsonTypeName("required")
public class ValidationRequiredPersist extends BaseValidationPersist {
    @JsonCreator
    public ValidationRequiredPersist(@JsonProperty("type") IndicatorPointValidationType type) {
        super(type);
    }
}
