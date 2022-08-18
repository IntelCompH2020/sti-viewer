package gr.cite.intelcomp.stiviewer.data.tenant;

import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScoped;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.UUID;

@MappedSuperclass
//@Getter
//@Setter
//@NoArgsConstructor
@FilterDef(name = TenantScopedBaseEntity.tenantFilter, parameters = {@ParamDef(name = TenantScopedBaseEntity.tenantFilterTenantParam, type = "string")})
@Filter(name = "tenantFilter", condition = "tenant_id = (cast(:tenantId as uuid))")
@EntityListeners(TenantListener.class)
public abstract class TenantScopedBaseEntity implements TenantScoped, Serializable {
    private static final long serialVersionUID = 1L;
    public static final String tenantFilter = "tenantFilter";
    public static final String tenantFilterTenantParam = "tenantId";

    @Column(name = "tenant_id", columnDefinition = "uuid", nullable = false)
    private UUID tenantId;
    public static final String _tenantId = "tenantId";
    public UUID getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }
}
