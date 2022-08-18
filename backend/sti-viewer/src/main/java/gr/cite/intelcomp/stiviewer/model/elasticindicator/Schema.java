package gr.cite.intelcomp.stiviewer.model.elasticindicator;

import java.util.List;
import java.util.UUID;

public class Schema {

	public final static String _id = "id";
	private UUID id;

	public final static String _fields = "fields";
	private List<Field> fields;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
}
