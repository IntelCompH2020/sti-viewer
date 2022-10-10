package gr.cite.intelcomp.stiviewer.elastic.data;

import gr.cite.intelcomp.stiviewer.elastic.data.indicator.IndicatorMetadataEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.IndicatorSchemaEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.UUID;

@Document(indexName = "ic-sti-indicator")
public class IndicatorElasticEntity {
	public static final class Fields {
		public static final String id = "id";
		public static final String metadata = "metadata";
		public static final String schema = "schema";
	}

	@Id
	@Field(value = Fields.id, type = FieldType.Keyword)
	private UUID id;

	@Field(value = Fields.metadata, type = FieldType.Object)
	private IndicatorMetadataEntity metadata;

	@Field(value = Fields.schema, type = FieldType.Object)
	private IndicatorSchemaEntity schema;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public IndicatorMetadataEntity getMetadata() {
		return metadata;
	}

	public void setMetadata(IndicatorMetadataEntity metadata) {
		this.metadata = metadata;
	}

	public IndicatorSchemaEntity getSchema() {
		return schema;
	}

	public void setSchema(IndicatorSchemaEntity schema) {
		this.schema = schema;
	}
}
