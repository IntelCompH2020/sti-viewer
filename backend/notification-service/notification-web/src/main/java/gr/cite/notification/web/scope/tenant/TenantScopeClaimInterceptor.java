package gr.cite.notification.web.scope.tenant;


import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.MyPrincipal;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractor;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractorContext;
import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.scope.tenant.TenantScope;
import gr.cite.notification.convention.ConventionService;
import gr.cite.notification.data.TenantEntity;
import gr.cite.notification.errorcode.ErrorThesaurusProperties;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.logging.LoggerService;
import org.jetbrains.annotations.NotNull;
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
public class TenantScopeClaimInterceptor implements WebRequestInterceptor {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantScopeClaimInterceptor.class));
	private final TenantScope tenantScope;
	private final ConventionService conventionService;
	private final TenantScopeProperties tenantScopeProperties;
	private final ErrorThesaurusProperties errorThesaurusProperties;
	private final ClaimExtractor claimExtractor;
	private final CurrentPrincipalResolver currentPrincipalResolver;
	private final String clientTenantClaimName;
	private final ClaimExtractorContext claimExtractorContext;
	private final TenantByCodeCacheService tenantByCodeCacheService;
	private final TenantByIdCacheService tenantByIdCacheService;
	@PersistenceContext
	public EntityManager entityManager;

	@Autowired
	public TenantScopeClaimInterceptor(
			TenantScope tenantScope,
			ConventionService conventionService,
			ClaimExtractor claimExtractor,
			CurrentPrincipalResolver currentPrincipalResolver,
			ErrorThesaurusProperties errorThesaurusProperties,
			TenantScopeProperties tenantScopeProperties,
			ClaimExtractorContext claimExtractorContext,
			TenantByCodeCacheService tenantByCodeCacheService,
			TenantByIdCacheService tenantByIdCacheService
	) {
		this.tenantScope = tenantScope;
		this.conventionService = conventionService;
		this.currentPrincipalResolver = currentPrincipalResolver;
		this.claimExtractor = claimExtractor;
		this.errorThesaurusProperties = errorThesaurusProperties;
		this.tenantScopeProperties = tenantScopeProperties;
		this.claimExtractorContext = claimExtractorContext;
		this.tenantByCodeCacheService = tenantByCodeCacheService;
		this.tenantByIdCacheService = tenantByIdCacheService;
		this.clientTenantClaimName = this.tenantScopeProperties.getClientClaimsPrefix() + TenantScope.TenantClaimName;
	}

	@Override
	public void preHandle(@NotNull WebRequest request) throws InvalidApplicationException {
		if (!this.currentPrincipalResolver.currentPrincipal().isAuthenticated()) return;
		if (!this.tenantScope.isMultitenant()) return;

		MyPrincipal principal = this.currentPrincipalResolver.currentPrincipal();
		if (principal != null && principal.isAuthenticated() /* principal.Claims.Any() */) {
			Boolean scoped = this.scopeByPrincipal(this.tenantScope, principal);
			if (!scoped) scoped = this.scopeByClient(this.tenantScope, principal);
			if (!scoped && this.tenantScope.isSet() && this.tenantScopeProperties.getEnforceTrustedTenant()) throw new MyForbiddenException(this.errorThesaurusProperties.getMissingTenant().getCode(), this.errorThesaurusProperties.getMissingTenant().getMessage());
		}
	}

	private Boolean scopeByPrincipal(TenantScope scope, MyPrincipal principal) {
		String tenantCode = this.claimExtractor.tenantString(principal);
		if (tenantCode == null || tenantCode.isBlank()) tenantCode = this.claimExtractor.asString(principal, this.clientTenantClaimName);

		UUID tenantId = this.conventionService.parseUUIDSafe(tenantCode);
		if (tenantId == null && tenantCode == null) return false;
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
			logger.debug("tenant claim was set to {}", tenantId);
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
			logger.debug("parsed tenant header and set tenant id to {}", tenantId);
			this.tenantScope.setTenant(tenantId, tenantCode);
			this.claimExtractorContext.putReplaceParameter(TenantScope.TenantReplaceParameter, tenantCode);
		}
		return tenantId != null;
	}

	private Boolean scopeByClient(TenantScope scope, MyPrincipal principal) throws InvalidApplicationException {
		String client = this.claimExtractor.client(principal);

		Boolean isWhiteListed = this.tenantScopeProperties.getWhiteListedClients() != null && !this.conventionService.isNullOrEmpty(client) && this.tenantScopeProperties.getWhiteListedClients().contains(client);
		logger.debug("client is whitelisted : {}, scope is set: {}, with value {}", isWhiteListed, scope.isSet(), (scope.isSet() ? scope.getTenant() : null));

		return isWhiteListed && scope.isSet();
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
				return (UUID) o;
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
				return (String) o;
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
