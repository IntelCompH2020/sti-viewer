package gr.cite.intelcomp.stiviewer.model.persist;

import gr.cite.intelcomp.stiviewer.common.validation.FieldNotNullIfOtherSet;
import gr.cite.intelcomp.stiviewer.common.validation.ValidId;
import gr.cite.intelcomp.stiviewer.model.persist.accessrequestconfig.AccessRequestConfigPersist;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@FieldNotNullIfOtherSet(message = "{validation.hashempty}")
public class IndicatorPersist {

    @ValidId(message = "{validation.invalidid}")
    private UUID id;

    @NotNull(message = "{validation.empty}")
    @NotEmpty(message = "{validation.empty}")
    @Size(max = 200, message = "{validation.largerthanmax}")
    private String code;

    @NotNull(message = "{validation.empty}")
    @NotEmpty(message = "{validation.empty}")
    @Size(max = 500, message = "{validation.largerthanmax}")
    private String name;

    private String description;

    private AccessRequestConfigPersist config;

    private String hash;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public AccessRequestConfigPersist getConfig() {
        return config;
    }

    public void setAccessRequestConfigPersist(AccessRequestConfigPersist config) {
        this.config = config;
    }
}
