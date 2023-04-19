package gr.cite.intelcomp.stiviewer.model.persist;

import gr.cite.intelcomp.stiviewer.common.validation.ValidId;
import gr.cite.intelcomp.stiviewer.model.persist.elasticindicator.MetadataPersist;
import gr.cite.intelcomp.stiviewer.model.persist.elasticindicator.SchemaPersist;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ResetIndicatorElasticPersist {

	@ValidId(message = "{validation.invalidid}")
	@NotNull(message = "{validation.empty}")
	private UUID id;

	private MetadataPersist metadata;

	@Valid
	private SchemaPersist schema;


	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public MetadataPersist getMetadata() {
		return metadata;
	}

	public void setMetadata(MetadataPersist metadata) {
		this.metadata = metadata;
	}

	public SchemaPersist getSchema() {
		return schema;
	}

	public void setSchema(SchemaPersist schema) {
		this.schema = schema;
	}
}
