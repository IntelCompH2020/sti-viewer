package gr.cite.intelcomp.stiviewer.common.scope.tenant;

import gr.cite.intelcomp.stiviewer.data.tenant.TenantScopedBaseEntity;
import gr.cite.tools.logging.LoggerService;
import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import javax.persistence.EntityManager;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequestScope
public class TenantScope {
    public static final String TenantReplaceParameter = "::TenantCode::";
    public static final String TenantCodesClaimName = "TenantCodes";
    public static final String TenantClaimName = "x-tenant";

    private final MultitenancyProperties multitenancy;
    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantScope.class));
    private final AtomicReference<UUID> tenant = new AtomicReference<>();
    private final AtomicReference<String> tenantCode = new AtomicReference<>();
    private final AtomicReference<UUID> initialTenant = new AtomicReference<>();
    private final AtomicReference<String> initialTenantCode = new AtomicReference<>();

    @Autowired
    public TenantScope(MultitenancyProperties multitenancy) {
        this.multitenancy = multitenancy;
    }

    public Boolean isMultitenant() {
        return multitenancy.isMultitenant();
    }

    public Boolean isSet() {
        if (!this.isMultitenant())
            return Boolean.TRUE;
        return this.tenant.get() != null;
    }

    public UUID getTenant() throws InvalidApplicationException {
        if (!this.isMultitenant())
            return null;
        if (this.tenant.get() == null)
            throw new InvalidApplicationException("tenant not set");
        return this.tenant.get();
    }

    public String getTenantCode() throws InvalidApplicationException {
        if (!this.isMultitenant())
            return null;
        if (this.tenant.get() == null)
            throw new InvalidApplicationException("tenant not set");
        return this.tenantCode.get();
    }

    public void setTempTenant(EntityManager entityManager, UUID tenant) {
        this.tenant.set(tenant);

        if (this.tenant.get() != null) {
            entityManager
                    .unwrap(Session.class)
                    .enableFilter(TenantScopedBaseEntity.tenantFilter).setParameter(TenantScopedBaseEntity.tenantFilterTenantParam, this.tenant.get().toString());
        }
    }

    public void removeTempTenant(EntityManager entityManager) {
        this.tenant.set(this.initialTenant.get());
        this.tenantCode.set(this.initialTenantCode.get());
        if (this.initialTenant.get() != null) {
            entityManager
                    .unwrap(Session.class)
                    .enableFilter(TenantScopedBaseEntity.tenantFilter).setParameter(TenantScopedBaseEntity.tenantFilterTenantParam, this.initialTenant.get().toString());
        }
    }

    public void setTenant(UUID tenant, String tenantCode) {
        if (this.isMultitenant()) {
            this.tenant.set(tenant);
            this.initialTenant.set(tenant);
            this.tenantCode.set(tenantCode);
            this.initialTenantCode.set(tenantCode);
        }
    }
}

