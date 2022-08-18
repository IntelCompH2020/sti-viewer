package gr.cite.intelcomp.stiviewer.model.elasticindicator;

import gr.cite.intelcomp.stiviewer.common.enums.IndicatorFieldBaseType;

import java.util.List;
import java.util.UUID;

public class Field {

	public final static String _id = "id";
	private UUID id;

	public final static String _code = "code";
	private String code;

	public final static String _name = "name";
	private String name;

	public final static String _label = "label";
	private String label;

	public final static String _description = "description";
	private String description;

	public final static String _baseType = "baseType";
	private IndicatorFieldBaseType baseType;

	public final static String _typeSemantics = "typeSemantics";
	private String typeSemantics;

	public final static String _typeId = "typeId";
	private String typeId;

	public final static String _altLabels = "altLabels";
	private List<AltText> altLabels;

	public final static String _altDescriptions = "altDescriptions";
	private List<AltText> altDescriptions;

	public final static String _valueRange = "valueRange";
	private ValueRange valueRange;

	public final static String _subfieldOf = "subfieldOf";
	private String subfieldOf;

	public final static String _valueField = "valueField";
	private String valueField;

	public final static String _useAs = "useAs";
	private String useAs;

	public final static String _operations = "operations";
	private List<Operator> operations;

	public final static String _validation = "validation";
	private String validation;

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

	public List<AltText> getAltLabels() {
		return altLabels;
	}

	public void setAltLabels(List<AltText> altLabels) {
		this.altLabels = altLabels;
	}

	public List<AltText> getAltDescriptions() {
		return altDescriptions;
	}

	public void setAltDescriptions(List<AltText> altDescriptions) {
		this.altDescriptions = altDescriptions;
	}

	public ValueRange getValueRange() {
		return valueRange;
	}

	public void setValueRange(ValueRange valueRange) {
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

	public List<Operator> getOperations() {
		return operations;
	}

	public void setOperations(List<Operator> operations) {
		this.operations = operations;
	}

	public String getValidation() {
		return validation;
	}

	public void setValidation(String validation) {
		this.validation = validation;
	}
}
