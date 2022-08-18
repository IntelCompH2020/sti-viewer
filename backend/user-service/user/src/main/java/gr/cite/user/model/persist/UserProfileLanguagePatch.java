package gr.cite.user.model.persist;

import gr.cite.user.common.validation.FieldNotNullIfOtherSet;
import gr.cite.user.common.validation.ValidId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

public class UserProfileLanguagePatch {

    @NotNull(message = "{validation.empty}")
    @ValidId(message = "{validation.invalidid}")
    private UUID id;

    @NotNull(message = "{validation.empty}")
    @NotEmpty(message = "{validation.empty}")
    @Size(max = 200, message = "{validation.largerthanmax}")
    private String language;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}