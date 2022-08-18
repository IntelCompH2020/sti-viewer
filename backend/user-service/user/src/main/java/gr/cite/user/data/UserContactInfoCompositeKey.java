package gr.cite.user.data;

import gr.cite.user.common.enums.UserContactType;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class UserContactInfoCompositeKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID userId;

    private UserContactType type;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UserContactType getType() {
        return type;
    }

    public void setType(UserContactType type) {
        this.type = type;
    }

    public UserContactInfoCompositeKey() {
    }

    @Override
    public int hashCode() {
        return Objects.hash( userId, type );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(!(obj instanceof UserContactInfoCompositeKey )) return false;
        UserContactInfoCompositeKey key = (UserContactInfoCompositeKey) obj;
        return Objects.equals( type, key.type ) &&
                Objects.equals( userId, key.userId );
    }
}