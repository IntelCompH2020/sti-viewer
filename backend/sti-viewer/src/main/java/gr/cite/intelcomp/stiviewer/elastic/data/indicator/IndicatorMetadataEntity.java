package gr.cite.intelcomp.stiviewer.elastic.data.indicator;

import gr.cite.tools.elastic.ElasticConstants;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import java.time.Instant;
import java.util.List;

public class IndicatorMetadataEntity {
	public static final class Fields {
		public static final String label = "label";
		public static final String description = "description";
		public static final String url = "url";
		public static final String code = "code";
		public static final String semanticLabels = "semantic_labels";
		public static final String altLabels = "alt_labels";
		public static final String altDescriptions = "alt_descriptions";
		public static final String date = "date";
		public static final String coverage = "coverage";
	}

	@MultiField(mainField = @Field(value = Fields.label, type = FieldType.Text), otherFields = {@InnerField(suffix = ElasticConstants.SubFields.keyword, type = FieldType.Keyword)})
	private String label;

	@Field(value = Fields.description, type = FieldType.Text)
	private String description;

	@Field(value = Fields.url, type = FieldType.Keyword)
	private String url;

	@Field(value = Fields.code, type = FieldType.Keyword)
	private String code;

	@Field(value = Fields.semanticLabels, type = FieldType.Nested)
	private List<SemanticLabelEntity> semanticLabels;

	@Field(value = Fields.altLabels, type = FieldType.Nested)
	private List<AltTextEntity> altLabels;

	@Field(value = Fields.altDescriptions, type = FieldType.Nested)
	private List<AltTextEntity> altDescriptions;

	@Field(value = Fields.date, type = FieldType.Date)
	private Instant date;

	@Field(value = Fields.coverage, type = FieldType.Nested)
	private List<CoverageEntity> coverage;


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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<SemanticLabelEntity> getSemanticLabels() {
		return semanticLabels;
	}

	public void setSemanticLabels(List<SemanticLabelEntity> semanticLabels) {
		this.semanticLabels = semanticLabels;
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

	public Instant getDate() {
		return date;
	}

	public void setDate(Instant date) {
		this.date = date;
	}

	public List<CoverageEntity> getCoverage() {
		return coverage;
	}

	public void setCoverage(List<CoverageEntity> coverage) {
		this.coverage = coverage;
	}
}
