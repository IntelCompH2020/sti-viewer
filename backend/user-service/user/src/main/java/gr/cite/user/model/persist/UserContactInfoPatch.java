package gr.cite.user.model.persist;

import gr.cite.user.common.validation.FieldNotNullIfOtherSet;
import gr.cite.user.common.validation.ValidId;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@FieldNotNullIfOtherSet(notNullField = "id", otherSetField = "hash", failOn = "hash", message = "{validation.hashempty}")
public class UserContactInfoPatch {

    @NotNull(message = "{validation.empty}")
    @ValidId(message = "{validation.invalidid}")
    private UUID id;

    @Valid
    private List<UserContactInfoPersist> contacts;

    private String hash;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<UserContactInfoPersist> getContacts() {
        return contacts;
    }

    public void setContacts(List<UserContactInfoPersist> contacts) {
        this.contacts = contacts;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

}
