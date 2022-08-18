package gr.cite.intelcomp.stiviewer.elastic.data.indicator;

import gr.cite.intelcomp.stiviewer.common.enums.IndicatorFieldBaseType;
import gr.cite.tools.elastic.ElasticConstants;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import java.util.List;
import java.util.UUID;

public class FieldEntity {
	public static final class Fields {
		public static final String id = "id";
		public static final String code = "code";
		public static final String name = "name";
		public static final String label = "label";
		public static final String description = "description";
		public static final String baseType = "base_type";
		public static final String typeSemantics = "type_semantics";
		public static final String validation = "validation";
		public static final String typeId = "type_id";
		public static final String altLabels = "alt_labels";
		public static final String altDescriptions = "alt_descriptions";
		public static final String valueRange = "value_range";
		public static final String subfieldOf = "sub_field_of";
		public static final String valueField = "value_field";
		public static final String useAs = "use_as";
		public static final String operations = "operations";
	}

	@Field(value = Fields.id, type = FieldType.Keyword)
	private UUID id;

	@Field(value = Fields.code, type = FieldType.Keyword)
	private String code;

	@MultiField(mainField = @Field(value = Fields.name, type = FieldType.Text), otherFields = {
			@InnerField(suffix = ElasticConstants.SubFields.keyword, type = FieldType.Keyword)
	})
	private String name;

	@Field(value = Fields.label, type = FieldType.Text)
	private String label;

	@Field(value = Fields.description, type = FieldType.Text)
	private String description;

	@Field(value = Fields.baseType, type = FieldType.Keyword)
	private IndicatorFieldBaseType baseType;

	@Field(value = Fields.typeSemantics, type = FieldType.Keyword) //Enum?
	private String typeSemantics;

	@Field(value = Fields.typeId, type = FieldType.Keyword)
	private String typeId;

	@Field(value = Fields.altLabels, type = FieldType.Nested)
	private List<AltTextEntity> altLabels;

	@Field(value = Fields.altDescriptions, type = FieldType.Nested)
	private List<AltTextEntity> altDescriptions;

	@Field(value = Fields.valueRange, type = FieldType.Object)
	private ValueRangeEntity valueRange;

	@Field(value = Fields.subfieldOf, type = FieldType.Keyword)
	private String subfieldOf;

	@Field(value = Fields.valueField, type = FieldType.Keyword)
	private String valueField;

	@Field(value = Fields.useAs, type = FieldType.Keyword) //Enum?
	private String useAs;

	@Field(value = Fields.operations, type = FieldType.Nested)
	private List<OperationEntity> operations;

	@Field(value = Fields.validation, type = FieldType.Nested,includeInParent = true)
	private List<ValidationEntity> validation;

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

	public List<AltTextEntity> getAltLabels() {
		return altLabels;
	}

	public void setAltLabels(List<AltTextEntity> altLabels) {
		this.altLabels = altLabels;
	}

	public List<AltTextEntity> getAltDescriptions() {
		return altDescriptions;
	}

	public void setAltDescriptions(List<AltTextEntity> altDescriptions) {
		this.altDescriptions = altDescriptions;
	}

	public ValueRangeEntity getValueRange() {
		return valueRange;
	}

	public void setValueRange(ValueRangeEntity valueRange) {
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

	public List<OperationEntity> getOperations() {
		return operations;
	}

	public void setOperations(List<OperationEntity> operations) {
		this.operations = operations;
	}

	public  List<ValidationEntity> getValidation() {
		return validation;
	}

	public void setValidation( List<ValidationEntity> validation) {
		this.validation = validation;
	}
}
