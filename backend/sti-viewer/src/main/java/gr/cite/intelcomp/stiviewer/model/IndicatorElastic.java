package gr.cite.intelcomp.stiviewer.model;

import gr.cite.intelcomp.stiviewer.model.elasticindicator.Metadata;
import gr.cite.intelcomp.stiviewer.model.elasticindicator.Schema;

import java.util.UUID;

public class IndicatorElastic {


	public final static String _id = "id";
	private UUID id;


	public final static String _metadata = "metadata";
	private Metadata metadata;

	public final static String _schema = "schema";
	private Schema schema;


	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}
}
