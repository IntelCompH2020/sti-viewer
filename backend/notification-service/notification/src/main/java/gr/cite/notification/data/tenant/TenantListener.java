package gr.cite.notification.data.tenant;

import gr.cite.notification.common.scope.tenant.TenantScope;
import gr.cite.notification.common.scope.tenant.TenantScoped;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.management.InvalidApplicationException;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import java.util.UUID;

public class TenantListener {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantListener.class));
	private final TenantScope tenantScope;

	@Autowired
	public TenantListener(
			TenantScope tenantScope
	) {
		this.tenantScope = tenantScope;
	}

	@PrePersist
	public void setTenantOnCreate(TenantScoped entity) throws InvalidApplicationException {
		if (tenantScope.isMultitenant()) {
			final UUID tenantId = tenantScope.getTenant();
			entity.setTenantId(tenantId);
		} else {
			entity.setTenantId(null);
		}
	}

	@PreUpdate
	@PreRemove
	public void setTenantOnUpdate(TenantScoped entity) throws InvalidApplicationException {
		if (tenantScope.isMultitenant()) {
			if (entity.getTenantId() == null) {
				logger.error("somebody tried to set null tenant");
				throw new MyForbiddenException("tenant tampering");
			}
			if (entity.getTenantId().compareTo(tenantScope.getTenant()) != 0) {
				logger.error("somebody tried to change an entries tenant");
				throw new MyForbiddenException("tenant tampering");
			}

			final UUID tenantId = tenantScope.getTenant();
			entity.setTenantId(tenantId);
		} else {
			if (entity.getTenantId() != null) {
				logger.error("somebody tried to set non null tenant");
				throw new MyForbiddenException("tenant tampering");
			}
		}
	}
}
