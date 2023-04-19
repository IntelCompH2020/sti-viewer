package gr.cite.notification.common.scope.tenant;

import gr.cite.notification.data.tenant.TenantScopedBaseEntity;
import gr.cite.tools.logging.LoggerService;
import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import javax.persistence.EntityManager;
import java.util.UUID;

@Component
@RequestScope
public class TenantScope {
	public static final String TenantReplaceParameter = "::TenantCode::";
	public static final String TenantCodesClaimName = "TenantCodes";
	public static final String TenantClaimName = "x-tenant";

	private MultitenancyProperties multitenancy;
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantScope.class));
	private UUID tenant = null;
	private String tenantCode = null;
	private UUID initialTenant = null;
	private String initialTenantCode = null;

	@Autowired
	public TenantScope(MultitenancyProperties multitenancy) {
		this.multitenancy = multitenancy;
	}

	public Boolean isMultitenant() {
		return multitenancy.isMultitenant();
	}

	public Boolean isSet() {
		if (!this.isMultitenant()) return true;
		return this.tenant != null;
	}

	public UUID getTenant() throws InvalidApplicationException {
		if (!this.isMultitenant()) return null;
		if (this.tenant == null) throw new InvalidApplicationException("tenant not set");
		return this.tenant;
	}

	public String getTenantCode() throws InvalidApplicationException {
		if (!this.isMultitenant()) return null;
		if (this.tenant == null) throw new InvalidApplicationException("tenant not set");
		return this.tenantCode;
	}

	public void setTempTenant(EntityManager entityManager, UUID tenant, String tenantCode) {
		this.tenant = tenant;

		if(this.tenant != null) {
			entityManager
					.unwrap(Session.class)
					.enableFilter(TenantScopedBaseEntity.tenantFilter).setParameter(TenantScopedBaseEntity.tenantFilterTenantParam, this.tenant.toString());
		}
	}

	public void removeTempTenant(EntityManager  entityManager) {
		this.tenant = this.initialTenant;
		this.tenantCode = this.initialTenantCode;
		if(this.initialTenant != null) {
			entityManager
					.unwrap(Session.class)
					.enableFilter(TenantScopedBaseEntity.tenantFilter).setParameter(TenantScopedBaseEntity.tenantFilterTenantParam, this.initialTenant.toString());
		}
	}

	public void setTenant(UUID tenant, String tenantCode) {
		if (this.isMultitenant()) {
			this.tenant = tenant;
			this.initialTenant = tenant;
			this.tenantCode = tenantCode;
			this.initialTenantCode = tenantCode;
		}
	}
}



