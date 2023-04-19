package gr.cite.intelcomp.stiviewer.web.scope.tenant;


import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractor;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.enums.UserContactType;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.TenantUserEntity;
import gr.cite.intelcomp.stiviewer.data.UserContactInfoEntity;
import gr.cite.intelcomp.stiviewer.data.UserEntity;
import gr.cite.intelcomp.stiviewer.data.tenant.TenantScopedBaseEntity;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.logging.LoggerService;
import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

import javax.management.InvalidApplicationException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
public class TenantInterceptor implements WebRequestInterceptor {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantInterceptor.class));
	private final TenantScope tenantScope;
	private final UserScope userScope;
	private final CurrentPrincipalResolver currentPrincipalResolver;
	private final ClaimExtractor claimExtractor;
	private final ApplicationContext applicationContext;
	private final ErrorThesaurusProperties errorThesaurusProperties;
	private final TenantScopeProperties tenantScopeProperties;
	private final UserAllowedTenantCacheService userAllowedTenantCacheService;
	private final PlatformTransactionManager transactionManager;
	private final ErrorThesaurusProperties errors;
	private final ConventionService conventionService;

	@PersistenceContext
	public EntityManager entityManager;

	@Autowired
	public TenantInterceptor(
			TenantScope tenantScope,
			UserScope userScope,
			CurrentPrincipalResolver currentPrincipalResolver,
			ClaimExtractor claimExtractor,
			ApplicationContext applicationContext,
			ErrorThesaurusProperties errorThesaurusProperties,
			TenantScopeProperties tenantScopeProperties,
			UserAllowedTenantCacheService userAllowedTenantCacheService,
			PlatformTransactionManager transactionManager,
			ErrorThesaurusProperties errors, 
			ConventionService conventionService) {
		this.tenantScope = tenantScope;
		this.userScope = userScope;
		this.currentPrincipalResolver = currentPrincipalResolver;
		this.claimExtractor = claimExtractor;
		this.applicationContext = applicationContext;
		this.errorThesaurusProperties = errorThesaurusProperties;
		this.tenantScopeProperties = tenantScopeProperties;
		this.userAllowedTenantCacheService = userAllowedTenantCacheService;
		this.transactionManager = transactionManager;
		this.errors = errors;
		this.conventionService = conventionService;
	}

	@Override
	public void preHandle(WebRequest request) throws InvalidApplicationException {
		if (!this.currentPrincipalResolver.currentPrincipal().isAuthenticated()) return;
		if (!this.tenantScope.isMultitenant()) return;

		boolean isAllowedNoTenant = this.applicationContext.getBean(AuthorizationService.class).authorize(Permission.AllowNoTenant);
		if (tenantScope.isSet() && this.entityManager != null) {
			List<String> currentPrincipalTenantCodes = this.claimExtractor.asStrings(this.currentPrincipalResolver.currentPrincipal(), TenantScope.TenantCodesClaimName);
			if ((currentPrincipalTenantCodes == null || !currentPrincipalTenantCodes.contains(tenantScope.getTenantCode())) && !isAllowedNoTenant) {
				logger.warn("tenant not allowed {}", this.tenantScope.getTenant());
				throw new MyForbiddenException(this.errors.getTenantNotAllowed().getCode(), this.errors.getTenantNotAllowed().getMessage());
			}

			boolean isUserAllowedTenant = false;
			UserAllowedTenantCacheService.UserAllowedTenantCacheValue cacheValue = this.userAllowedTenantCacheService.lookup(this.userAllowedTenantCacheService.buildKey(this.userScope.getUserId(), this.tenantScope.getTenant()));
			if (cacheValue != null) {
				isUserAllowedTenant = cacheValue.isAllowed();
			} else {
				isUserAllowedTenant = this.isUserAllowedTenant();
				this.userAllowedTenantCacheService.put(new UserAllowedTenantCacheService.UserAllowedTenantCacheValue(this.userScope.getUserId(), this.tenantScope.getTenant(), isUserAllowedTenant));
			}

			if (isUserAllowedTenant) {
				this.entityManager
						.unwrap(Session.class)
						.enableFilter(TenantScopedBaseEntity.tenantFilter).setParameter(TenantScopedBaseEntity.tenantFilterTenantParam, tenantScope.getTenant().toString());
			} else {
				if (isAllowedNoTenant || this.isWhiteListedEndpoint(request)) {
					tenantScope.setTenant(null, null);
				} else {
					logger.warn("tenant not allowed {}", this.tenantScope.getTenant());
					throw new MyForbiddenException(this.errors.getTenantNotAllowed().getCode(), this.errors.getTenantNotAllowed().getMessage());
				}
			}
		} else {
			if (!isAllowedNoTenant) {
				if (!this.isWhiteListedEndpoint(request)) {
					logger.warn("tenant scope not provided");
					throw new MyForbiddenException(this.errorThesaurusProperties.getMissingTenant().getCode(), this.errorThesaurusProperties.getMissingTenant().getMessage());
				}
			}
		}
	}

	private boolean isWhiteListedEndpoint(WebRequest request) {
		String servletPath = ((ServletWebRequest) request).getRequest().getServletPath();
		if (this.tenantScopeProperties.getWhiteListedEndpoints() != null) {
			for (String whiteListedEndpoint : this.tenantScopeProperties.getWhiteListedEndpoints()) {
				if (servletPath.toLowerCase(Locale.ROOT).startsWith(whiteListedEndpoint.toLowerCase(Locale.ROOT))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isUserAllowedTenant() throws InvalidApplicationException {
		if (userScope.isSet()) {
			CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = criteriaBuilder.createQuery(Tuple.class);
			Root<UserEntity> root = query.from(UserEntity.class);
			Subquery<TenantUserEntity> subQuery = query.subquery(TenantUserEntity.class);
			Root<TenantUserEntity> subQueryRoot = subQuery.from(TenantUserEntity.class);
			query.where(criteriaBuilder.and(
					criteriaBuilder.equal(root.get(UserEntity._isActive), IsActive.ACTIVE),
					criteriaBuilder.in(root.get(UserEntity._id)).value(subQuery.where(
							criteriaBuilder.and(
									criteriaBuilder.equal(subQueryRoot.get(TenantUserEntity._tenantId), this.tenantScope.getTenant()),
									criteriaBuilder.equal(subQueryRoot.get(TenantUserEntity._userId), this.userScope.getUserId()),
									criteriaBuilder.equal(subQueryRoot.get(TenantUserEntity._isActive), IsActive.ACTIVE)
							)).select(subQueryRoot.get(TenantUserEntity._userId)).distinct(true)
					)
			));
			query.multiselect(root.get(UserEntity._id).alias(UserEntity._id));
			List<Tuple> results = this.entityManager.createQuery(query).getResultList();
			if (results.size() == 0 && this.tenantScopeProperties.getAutoCreateTenantUser()) {
				return this.createTenantUser();
			} else {
				return results.size() > 0;
			}
		}

		return false;
	}

	private boolean createTenantUser() throws InvalidApplicationException {
		TenantUserEntity user = new TenantUserEntity();
		user.setId(UUID.randomUUID());
		user.setCreatedAt(Instant.now());
		user.setUpdatedAt(Instant.now());
		user.setIsActive(IsActive.ACTIVE);
		user.setTenantId(this.tenantScope.getTenant());
		user.setUserId(userScope.getUserId());

		String email = this.claimExtractor.email(this.currentPrincipalResolver.currentPrincipal());
		UserContactInfoEntity userContactInfoEntity = null;
		if (this.conventionService.isValidEmail(email)) {
			userContactInfoEntity = new UserContactInfoEntity();
			userContactInfoEntity.setUserId(userScope.getUserId());
			userContactInfoEntity.setCreatedAt(Instant.now());
			userContactInfoEntity.setUpdatedAt(Instant.now());
			userContactInfoEntity.setIsActive(IsActive.ACTIVE);
			userContactInfoEntity.setType(UserContactType.Email);
			userContactInfoEntity.setTenantId(this.tenantScope.getTenant());
			userContactInfoEntity.setValue(email);
		}

		DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
		definition.setName(UUID.randomUUID().toString());
		definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus status = null;
		try {
			status = transactionManager.getTransaction(definition);
			this.entityManager.persist(user);
			if (userContactInfoEntity != null) this.entityManager.persist(userContactInfoEntity);
			this.entityManager.flush();
			transactionManager.commit(status);
		} catch (Exception ex) {
			if (status != null) transactionManager.rollback(status);
			throw ex;
		}
		return true;
	}

	@Override
	public void postHandle(@NonNull WebRequest request, ModelMap model) {
		this.tenantScope.setTenant(null, null);
	}

	@Override
	public void afterCompletion(@NonNull WebRequest request, Exception ex) {
	}
}
