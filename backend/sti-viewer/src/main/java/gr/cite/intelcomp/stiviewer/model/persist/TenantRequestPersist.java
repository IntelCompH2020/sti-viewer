package gr.cite.intelcomp.stiviewer.model.persist;


import gr.cite.intelcomp.stiviewer.common.enums.TenantRequestStatus;
import gr.cite.intelcomp.stiviewer.common.validation.FieldNotNullIfOtherSet;
import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@FieldNotNullIfOtherSet(notNullField = TenantRequestPersist._id, otherSetField = TenantRequestPersist._hash, failOn = TenantRequestPersist._hash, message = "{validation.hashempty}")
public class TenantRequestPersist {

    @ValidId(message = "{validation.invalidid}")
    private UUID id;
    public final static String _id = "id";

    private String message;
    private final String _message = "message";

    @NotNull(message = "{validation.empty}")
    @NotEmpty(message = "{validation.empty}")
    @Size(max = 200, message = "{validation.largerthanmax}")
    private String email;
    private final String _email = "email";


    @NotNull(message = "{validation.empty}")
    private TenantRequestStatus tenantRequestStatus;

    private String hash;
    public final static String _hash = "hash";

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TenantRequestStatus getStatus() {
        return tenantRequestStatus;
    }

    public void setStatus(TenantRequestStatus tenantRequestStatus) {
        this.tenantRequestStatus = tenantRequestStatus;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
