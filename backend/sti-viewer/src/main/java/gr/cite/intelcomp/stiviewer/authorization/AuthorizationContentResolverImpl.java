package gr.cite.intelcomp.stiviewer.authorization;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractor;
import gr.cite.intelcomp.stiviewer.authorization.cache.AffiliatedIndicatorsCacheService;
import gr.cite.intelcomp.stiviewer.authorization.cache.IndicatorColumnAccessCacheService;
import gr.cite.intelcomp.stiviewer.authorization.cache.PrincipalIndicatorResourceCacheService;
import gr.cite.intelcomp.stiviewer.authorization.indicatorpoint.IndicatorColumnAccess;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.common.types.indicatoraccessconfig.IndicatorAccessConfigEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.IndicatorAccessEntity;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.IndicatorAccess;
import gr.cite.intelcomp.stiviewer.query.IndicatorAccessQuery;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.fieldset.BaseFieldSet;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequestScope
public class AuthorizationContentResolverImpl implements AuthorizationContentResolver {
	private final UserScope userScope;
	private final TenantScope tenantScope;
	private final QueryFactory queryFactory;
	protected final ConventionService conventionService;
	private final AffiliatedIndicatorsCacheService affiliatedIndicatorsCacheService;
	private final PrincipalIndicatorResourceCacheService principalIndicatorResourceCacheService;
	private final IndicatorColumnAccessCacheService indicatorColumnAccessCacheService;

	private final ClaimExtractor claimExtractor;
	private final CurrentPrincipalResolver currentPrincipalResolver;
	private final AuthorizationService authorizationService;
	private final JsonHandlingService jsonHandlingService;

	public AuthorizationContentResolverImpl(
			UserScope userScope,
			TenantScope tenantScope,
			QueryFactory queryFactory,
			ConventionService conventionService,
			AffiliatedIndicatorsCacheService affiliatedIndicatorsCacheService,
			PrincipalIndicatorResourceCacheService principalIndicatorResourceCacheService,
			IndicatorColumnAccessCacheService indicatorColumnAccessCacheService,
			ClaimExtractor claimExtractor,
			CurrentPrincipalResolver currentPrincipalResolver,
			AuthorizationService authorizationService,
			JsonHandlingService jsonHandlingService
	) {
		this.userScope = userScope;
		this.tenantScope = tenantScope;
		this.queryFactory = queryFactory;
		this.conventionService = conventionService;
		this.affiliatedIndicatorsCacheService = affiliatedIndicatorsCacheService;
		this.principalIndicatorResourceCacheService = principalIndicatorResourceCacheService;
		this.indicatorColumnAccessCacheService = indicatorColumnAccessCacheService;
		this.claimExtractor = claimExtractor;
		this.currentPrincipalResolver = currentPrincipalResolver;
		this.authorizationService = authorizationService;
		this.jsonHandlingService = jsonHandlingService;
	}

	public IndicatorRolesResource indicatorAffiliation(UUID indicatorId) {
		UUID userId = this.userScope.getUserIdSafe();
		IndicatorRolesResource resource = new IndicatorRolesResource(userId);
		if (userId == null) return resource;

		String entityType = "indicator";
		PrincipalIndicatorResourceCacheService.PrincipalIndicatorResourceCacheValue cacheValue = this.principalIndicatorResourceCacheService.lookup(this.principalIndicatorResourceCacheService.buildKey(userId, indicatorId, entityType));
		if (cacheValue == null) {
			resource = this.resolveIndicatorAffiliation(indicatorId);
			cacheValue = new PrincipalIndicatorResourceCacheService.PrincipalIndicatorResourceCacheValue(userId, indicatorId, entityType, resource);
			this.principalIndicatorResourceCacheService.put(cacheValue);
		}
		return cacheValue.getIndicatorRolesResource();
	}

	private IndicatorRolesResource resolveIndicatorAffiliation(UUID indicatorId) {
		UUID userId = this.userScope.getUserIdSafe();
		IndicatorRolesResource resource = new IndicatorRolesResource(userId);
		if (userId == null) return resource;
		List<IndicatorAccessEntity> indicatorAccesses = this.queryFactory.query(IndicatorAccessQuery.class).isActive(IsActive.ACTIVE).
				indicatorIds(indicatorId).collectAs(new BaseFieldSet().ensure(IndicatorAccess._id).ensure(this.conventionService.asIndexer(IndicatorAccess._indicator, Indicator._id)).ensure(IndicatorAccess._user));

		List<UUID> indicatorIds = new ArrayList<>();
		for (IndicatorAccessEntity indicatorAccessEntity : indicatorAccesses) {
			if (indicatorAccessEntity.getUserId() == null || indicatorAccessEntity.getUserId().equals(userId)) {
				indicatorIds.add(indicatorAccessEntity.getIndicatorId());
			}
		}
		if (indicatorIds.contains(indicatorId)) {
			List<String> roles = claimExtractor.roles(this.currentPrincipalResolver.currentPrincipal());
			resource.setIndicatorRoles(roles);
		}

		return resource;
	}

	public List<IndicatorColumnAccess> indicatorAllowedKeywords(UUID... indicatorIds) {
		if (indicatorIds == null || indicatorIds.length == 0) return new ArrayList<>();
		UUID userId = this.userScope.getUserIdSafe();
		if (userId == null) return new ArrayList<>();

		IndicatorColumnAccessCacheService.IndicatorColumnAccessCacheValue cacheValue = this.indicatorColumnAccessCacheService.lookup(this.indicatorColumnAccessCacheService.buildKey(userId, List.of(indicatorIds)));
		if (cacheValue == null) {
			List<IndicatorAccessEntity> indicatorAccesses = null;
			try {
				indicatorAccesses = this.queryFactory.query(IndicatorAccessQuery.class).indicatorIds(indicatorIds).isActive(IsActive.ACTIVE).tenantIds(this.tenantScope.getTenant()).collectAs(new BaseFieldSet().ensure(IndicatorAccess._config));
			} catch (InvalidApplicationException e) {
				throw new RuntimeException(e);
			}
			List<IndicatorColumnAccess> items = null;
			for (IndicatorAccessEntity indicatorAccess : indicatorAccesses) {
				items = this.mergeIndicatorColumnAccess(items, indicatorAccess);
			}
			cacheValue = new IndicatorColumnAccessCacheService.IndicatorColumnAccessCacheValue(userId, List.of(indicatorIds), items);
			this.indicatorColumnAccessCacheService.put(cacheValue);
		}
		return cacheValue.getIndicatorColumnAccesses();
	}

	private List<IndicatorColumnAccess> mergeIndicatorColumnAccess(List<IndicatorColumnAccess> filterColumns, IndicatorAccessEntity entity) {
		IndicatorAccessConfigEntity config = this.jsonHandlingService.fromJsonSafe(IndicatorAccessConfigEntity.class, entity.getConfig());
		if (config == null || config.getFilterColumns() == null || config.getFilterColumns().isEmpty()) return filterColumns;

		List<IndicatorColumnAccess> tempFilterColumns = new ArrayList<>();
		tempFilterColumns.addAll(config.getFilterColumns().stream().map(x -> {
			IndicatorColumnAccess indicatorColumnAccess = new IndicatorColumnAccess();
			indicatorColumnAccess.setField(x.getColumn());
			indicatorColumnAccess.setValues(x.getValues());
			return indicatorColumnAccess;
		}).collect(Collectors.toList()));

		if (tempFilterColumns.isEmpty()) return filterColumns;

		if (filterColumns == null) {
			filterColumns = new ArrayList<>();
			for (IndicatorColumnAccess tempFilterColumn : tempFilterColumns) {
				IndicatorColumnAccess indicatorColumnAccess = new IndicatorColumnAccess();
				indicatorColumnAccess.setField(tempFilterColumn.getField());
				indicatorColumnAccess.setValues(tempFilterColumn.getValues());
				filterColumns.add(indicatorColumnAccess);
			}
		} else {
			for (IndicatorColumnAccess tempFilterColumn : tempFilterColumns) {
				IndicatorColumnAccess indicatorColumnAccess = filterColumns.stream().filter(x -> x.getField().equals(tempFilterColumn.getField())).findFirst().orElse(null);
				if (indicatorColumnAccess != null) {
					if (tempFilterColumn.getValues() != null && !tempFilterColumn.getValues().isEmpty()) {
						indicatorColumnAccess.getValues().retainAll(tempFilterColumn.getValues());
					}
				}
			}
			List<String> toDelete = new ArrayList<>();
			for (IndicatorColumnAccess filterColumn : filterColumns) {
				IndicatorColumnAccess indicatorColumnAccess = tempFilterColumns.stream().filter(x -> x.getField().equals(filterColumn.getField())).findFirst().orElse(null);
				if (indicatorColumnAccess == null) {
					toDelete.add(filterColumn.getField());
				}
			}
			for (String toDeleteItem : toDelete) filterColumns.removeIf(x -> x.getField().equals(toDeleteItem));
		}

		filterColumns = filterColumns.stream().filter(x -> x.getValues() != null && !x.getValues().isEmpty()).collect(Collectors.toList());
		return filterColumns;
	}

	public List<UUID> affiliatedIndicators(String... permissions) {
		UUID userId = this.userScope.getUserIdSafe();
		if (userId == null) return new ArrayList<>();

		AffiliatedIndicatorsCacheService.AffiliatedIndicatorsCacheValue cacheValue = this.affiliatedIndicatorsCacheService.lookup(this.affiliatedIndicatorsCacheService.buildKey(userId, permissions != null ? List.of(permissions) : null));
		if (cacheValue == null) {
			List<UUID> indicatorIds = this.resolveAffiliatedIndicators(permissions);

			cacheValue = new AffiliatedIndicatorsCacheService.AffiliatedIndicatorsCacheValue(userId, indicatorIds, permissions != null ? List.of(permissions) : null);
			this.affiliatedIndicatorsCacheService.put(cacheValue);
		}
		return cacheValue.getIndicatorIds();
	}

	private List<UUID> resolveAffiliatedIndicators(String... permissions) {
		UUID userId = this.userScope.getUserIdSafe();
		List<UUID> indicatorIds = new ArrayList<>();

		if (userId == null) return indicatorIds;
		List<String> roles = claimExtractor.roles(this.currentPrincipalResolver.currentPrincipal());

		List<IndicatorAccessEntity> indicatorAccesses = this.queryFactory.query(IndicatorAccessQuery.class).isActive(IsActive.ACTIVE).collectAs(new BaseFieldSet().ensure(IndicatorAccess._id).ensure(this.conventionService.asIndexer(IndicatorAccess._indicator, Indicator._id)).ensure(IndicatorAccess._user));

		for (IndicatorAccessEntity indicatorAccessEntity : indicatorAccesses) {
			if (indicatorAccessEntity.getUserId() == null || indicatorAccessEntity.getUserId().equals(userId)) {
				IndicatorRolesResource resource = new IndicatorRolesResource(userId);
				resource.setIndicatorRoles(roles);
				Boolean isPermitted = this.authorizationService.authorizeAtLeastOne(List.of(resource), true, permissions);
				if (isPermitted) indicatorIds.add(indicatorAccessEntity.getIndicatorId());
			}
		}

		return indicatorIds.stream().distinct().collect(Collectors.toList());
	}
}
