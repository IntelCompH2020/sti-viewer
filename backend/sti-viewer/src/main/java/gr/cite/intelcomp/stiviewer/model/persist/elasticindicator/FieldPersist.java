package gr.cite.intelcomp.stiviewer.model.persist.elasticindicator;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.cite.intelcomp.stiviewer.common.enums.IndicatorFieldBaseType;
import gr.cite.intelcomp.stiviewer.common.validation.ValidEnum;

import java.util.List;
import java.util.UUID;

public class FieldPersist {

    private UUID id;

    private String code;

    private String name;

    private String label;

    private String description;


    @ValidEnum(message = "enum is null")
    @JsonProperty(value="basetype")
    private IndicatorFieldBaseType baseType;

    @JsonProperty(value="typesemantics")
    private String typeSemantics;

    @JsonProperty("typeid")
    private String typeId;

    @JsonProperty("alt_labels")
    private List<AltTextPersist> altLabels;

    @JsonProperty("alt_descriptions")
    private List<AltTextPersist> altDescriptions;

    @JsonProperty("valuerange")
    private ValueRangePersist valueRange;

    @JsonProperty("subfieldof")
    private String subfieldOf;

    @JsonProperty("valuefield")
    private String valueField;

    @JsonProperty("useas")
    private String useAs;

    private List<OperatorPersist> operations;

    private List<BaseValidationPersist> validation;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IndicatorFieldBaseType getBaseType() {
        return baseType;
    }

    public void setBaseType(IndicatorFieldBaseType baseType) {
        this.baseType = baseType;
    }

    public String getTypeSemantics() {
        return typeSemantics;
    }

    public void setTypeSemantics(String typeSemantics) {
        this.typeSemantics = typeSemantics;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public List<AltTextPersist> getAltLabels() {
        return altLabels;
    }

    public void setAltLabels(List<AltTextPersist> altLabels) {
        this.altLabels = altLabels;
    }

    public List<AltTextPersist> getAltDescriptions() {
        return altDescriptions;
    }

    public void setAltDescriptions(List<AltTextPersist> altDescriptions) {
        this.altDescriptions = altDescriptions;
    }

    public ValueRangePersist getValueRange() {
        return valueRange;
    }

    public void setValueRange(ValueRangePersist valueRange) {
        this.valueRange = valueRange;
    }

    public String getSubfieldOf() {
        return subfieldOf;
    }

    public void setSubfieldOf(String subfieldOf) {
        this.subfieldOf = subfieldOf;
    }

    public String getValueField() {
        return valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }

    public String getUseAs() {
        return useAs;
    }

    public void setUseAs(String useAs) {
        this.useAs = useAs;
    }

    public List<OperatorPersist> getOperations() {
        return operations;
    }

    public void setOperations(List<OperatorPersist> operations) {
        this.operations = operations;
    }

    public List<BaseValidationPersist> getValidation() {
        return validation;
    }

    public void setValidation(List<BaseValidationPersist> validation) {
        this.validation = validation;
    }
}
