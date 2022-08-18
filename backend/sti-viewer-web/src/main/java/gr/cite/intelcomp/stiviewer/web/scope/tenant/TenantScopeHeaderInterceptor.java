package gr.cite.intelcomp.stiviewer.web.scope.tenant;


import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractorContext;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.TenantEntity;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

import javax.management.InvalidApplicationException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;

@Component
public class TenantScopeHeaderInterceptor implements WebRequestInterceptor {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantScopeHeaderInterceptor.class));
	private final TenantScope tenantScope;
	private final ConventionService conventionService;
	private final TenantByCodeCacheService tenantByCodeCacheService;
	private final TenantByIdCacheService tenantByIdCacheService;
	private final ClaimExtractorContext claimExtractorContext;
	private final CurrentPrincipalResolver currentPrincipalResolver;
	@PersistenceContext
	public EntityManager entityManager;

	@Autowired
	public TenantScopeHeaderInterceptor(
			TenantScope tenantScope,
			ConventionService conventionService,
			TenantByCodeCacheService tenantByCodeCacheService,
			TenantByIdCacheService tenantByIdCacheService,
			ClaimExtractorContext claimExtractorContext,
			CurrentPrincipalResolver currentPrincipalResolver
	) {
		this.tenantScope = tenantScope;
		this.conventionService = conventionService;
		this.tenantByCodeCacheService = tenantByCodeCacheService;
		this.tenantByIdCacheService = tenantByIdCacheService;
		this.claimExtractorContext = claimExtractorContext;
		this.currentPrincipalResolver = currentPrincipalResolver;
	}

	@Override
	public void preHandle(WebRequest request) throws InvalidApplicationException {
		if (!this.currentPrincipalResolver.currentPrincipal().isAuthenticated()) return;
		if (!this.tenantScope.isMultitenant()) return;

		String tenantCode = request.getHeader(TenantScope.TenantClaimName);
		logger.debug("retrieved request tenant header is: {header}", tenantCode);
		if (this.conventionService.isNullOrEmpty(tenantCode)) return;

		UUID tenantId = this.conventionService.parseUUIDSafe(tenantCode);
		if (tenantId == null && tenantCode == null) return;
		if (tenantId == null) {
			TenantByCodeCacheService.TenantByCodeCacheValue cacheValue = this.tenantByCodeCacheService.lookup(this.tenantByCodeCacheService.buildKey(tenantCode));
			if (cacheValue != null) {
				tenantId = cacheValue.getTenantId();
			} else {
				tenantId = this.getTenantIdFromDatabase(tenantCode);
				this.tenantByCodeCacheService.put(new TenantByCodeCacheService.TenantByCodeCacheValue(tenantCode, tenantId));
				this.tenantByIdCacheService.put(new TenantByIdCacheService.TenantByIdCacheValue(tenantCode, tenantId));
			}
		} else {
			TenantByIdCacheService.TenantByIdCacheValue cacheValue = this.tenantByIdCacheService.lookup(this.tenantByIdCacheService.buildKey(tenantId));
			if (cacheValue != null) {
				tenantCode = cacheValue.getTenantCode();
			} else {
				tenantCode = this.getTenantCodeFromDatabase(tenantId);
				this.tenantByCodeCacheService.put(new TenantByCodeCacheService.TenantByCodeCacheValue(tenantCode, tenantId));
				this.tenantByIdCacheService.put(new TenantByIdCacheService.TenantByIdCacheValue(tenantCode, tenantId));
			}
		}

		if (tenantId != null && tenantCode != null && !tenantCode.isBlank()) {
			logger.debug("parsed tenant header and set tenant id to {tenant}", tenantId);
			this.tenantScope.setTenant(tenantId, tenantCode);
			this.claimExtractorContext.putReplaceParameter(TenantScope.TenantReplaceParameter, tenantCode);
		}
	}

	private UUID getTenantIdFromDatabase(String tenantCode) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> query = criteriaBuilder.createQuery(Tuple.class);
		Root<TenantEntity> root = query.from(TenantEntity.class);
		query = query.where(
				criteriaBuilder.and(
						criteriaBuilder.equal(root.get(TenantEntity._code), tenantCode),
						criteriaBuilder.equal(root.get(TenantEntity._isActive), IsActive.ACTIVE)
				)
		).multiselect(root.get(TenantEntity._id).alias(TenantEntity._id));
		List<Tuple> results = this.entityManager.createQuery(query).getResultList();
		if (results.size() == 1) {
			Object o;
			try {
				o = results.get(0).get(TenantEntity._id);
			} catch (IllegalArgumentException e) {
				return null;
			}
			if (o == null) return null;
			try {
				return UUID.class.cast(o);
			} catch (ClassCastException e) {
				return null;
			}
		}
		return null;
	}

	private String getTenantCodeFromDatabase(UUID tenantId) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> query = criteriaBuilder.createQuery(Tuple.class);
		Root<TenantEntity> root = query.from(TenantEntity.class);
		query = query.where(
				criteriaBuilder.and(
						criteriaBuilder.equal(root.get(TenantEntity._id), tenantId),
						criteriaBuilder.equal(root.get(TenantEntity._isActive), IsActive.ACTIVE)
				)
		).multiselect(root.get(TenantEntity._code).alias(TenantEntity._code));
		List<Tuple> results = this.entityManager.createQuery(query).getResultList();
		if (results.size() == 1) {
			Object o;
			try {
				o = results.get(0).get(TenantEntity._code);
			} catch (IllegalArgumentException e) {
				return null;
			}
			if (o == null) return null;
			try {
				return String.class.cast(o);
			} catch (ClassCastException e) {
				return null;
			}
		}
		return null;
	}

	@Override
	public void postHandle(@NonNull WebRequest request, ModelMap model) {

		this.tenantScope.setTenant(null, null);
		this.claimExtractorContext.removeReplaceParameter(TenantScope.TenantReplaceParameter);
	}

	@Override
	public void afterCompletion(@NonNull WebRequest request, Exception ex) {
	}
}
