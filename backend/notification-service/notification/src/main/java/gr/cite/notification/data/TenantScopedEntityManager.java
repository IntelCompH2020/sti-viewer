package gr.cite.notification.data;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.notification.authorization.Permission;
import gr.cite.notification.common.scope.tenant.TenantScope;
import gr.cite.notification.common.scope.tenant.TenantScoped;
import gr.cite.tools.exception.MyForbiddenException;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import java.util.UUID;

@Service
@Scope
public class TenantScopedEntityManager {
	@PersistenceContext
	private EntityManager entityManager;
	private final AuthorizationService authorizationService;

	private final TenantScope tenantScope;

	public TenantScopedEntityManager(AuthorizationService authorizationService, TenantScope tenantScope) {
		this.authorizationService = authorizationService;
		this.tenantScope = tenantScope;
	}

	public int getBulkSize() {
		Session session = this.entityManager.unwrap(Session.class);
		return session.getJdbcBatchSize();
	}

	public void setBulkSize(int size) {
		Session session = this.entityManager.unwrap(Session.class);
		session.setJdbcBatchSize(size);
	}
	public void persist(Object entity) {
		this.entityManager.persist(entity);
	}

	public <T> T merge(T entity) throws InvalidApplicationException {
		if (tenantScope.isMultitenant() && (entity instanceof TenantScoped)) {
			boolean isAllowedNoTenant = authorizationService.authorize(Permission.AllowNoTenant);

			final UUID tenantId = !isAllowedNoTenant ? tenantScope.getTenant() : null;
			if (!isAllowedNoTenant && !tenantId.equals(((TenantScoped) entity).getTenantId())) throw new MyForbiddenException("tenant tampering");
		}
		return this.entityManager.merge(entity);
	}

	public void remove(Object entity) throws InvalidApplicationException {
		if (tenantScope.isMultitenant() && (entity instanceof TenantScoped)) {
			final UUID tenantId = tenantScope.getTenant();
			if (!tenantId.equals(((TenantScoped) entity).getTenantId())) throw new MyForbiddenException("tenant tampering");
		}
		this.entityManager.remove(entity);
	}

	public <T> T find(Class<T> entityClass, Object primaryKey) throws InvalidApplicationException {
		T entity = this.entityManager.find(entityClass, primaryKey);

		if (tenantScope.isMultitenant() && (entity instanceof TenantScoped)) {
			boolean isAllowedNoTenant = authorizationService.authorize(Permission.AllowNoTenant);

			final UUID tenantId = !isAllowedNoTenant ? tenantScope.getTenant() : null;
			if (!isAllowedNoTenant && !tenantId.equals(((TenantScoped) entity).getTenantId())) return null;
		}
		return entity;
	}

	public void flush() {
		this.entityManager.flush();
	}


	public void setFlushMode(FlushModeType flushMode) {
		this.entityManager.setFlushMode(flushMode);

	}

	public FlushModeType getFlushMode() {
		return this.entityManager.getFlushMode();
	}

	public void clear() {
		this.entityManager.clear();
	}

}
