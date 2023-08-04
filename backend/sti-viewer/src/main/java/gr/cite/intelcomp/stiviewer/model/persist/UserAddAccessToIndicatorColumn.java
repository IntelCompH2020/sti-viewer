package gr.cite.intelcomp.stiviewer.model.persist;

import gr.cite.intelcomp.stiviewer.model.persist.indicatoraccess.FilterColumnConfigPersist;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class UserAddAccessToIndicatorColumn {

    @NotNull(message = "{validation.empty}")
    private UUID userId;

    @NotNull(message = "{validation.empty}")
    private UUID tenantId;

    @NotNull(message = "{validation.empty}")
    private UUID indicatorId;

    @Valid
    private FilterColumnConfigPersist column;

    public FilterColumnConfigPersist getColumn() {
        return column;
    }

    public void setColumn(FilterColumnConfigPersist column) {
        this.column = column;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public UUID getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(UUID indicatorId) {
        this.indicatorId = indicatorId;
    }
}
