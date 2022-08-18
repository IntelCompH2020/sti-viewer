package gr.cite.intelcomp.stiviewer.model.persist.elasticindicator;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

public class SchemaPersist {

    private UUID id;
    @Valid
    private List<FieldPersist> fields;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<FieldPersist> getFields() {
        return fields;
    }

    public void setFields(List<FieldPersist> fields) {
        this.fields = fields;
    }
}
