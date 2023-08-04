package gr.cite.intelcomp.stiviewer.authorization;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractor;
import gr.cite.intelcomp.stiviewer.authorization.cache.AffiliatedIndicatorsCacheService;
import gr.cite.intelcomp.stiviewer.authorization.cache.HierarchyIndicatorColumnAccessCacheService;
import gr.cite.intelcomp.stiviewer.authorization.cache.PrincipalIndicatorResourceCacheService;
import gr.cite.intelcomp.stiviewer.authorization.indicatorpoint.IndicatorColumnAccess;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.common.types.indicator.FilterColumnConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicator.IndicatorConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatoraccess.IndicatorAccessConfigEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.IndicatorAccessEntity;
import gr.cite.intelcomp.stiviewer.data.IndicatorEntity;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpoint.IndicatorPointQuery;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter.IndicatorPointKeywordFilter;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.IndicatorAccess;
import gr.cite.intelcomp.stiviewer.query.IndicatorAccessQuery;
import gr.cite.intelcomp.stiviewer.query.IndicatorQuery;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.query.DistinctValuesResponse;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.fieldset.BaseFieldSet;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
	private final HierarchyIndicatorColumnAccessCacheService hierarchyIndicatorColumnAccessCacheService;

	private final ClaimExtractor claimExtractor;
	private final CurrentPrincipalResolver currentPrincipalResolver;
	private final AuthorizationService authorizationService;
	private final JsonHandlingService jsonHandlingService;
	private final MessageSource messageSource;

	public AuthorizationContentResolverImpl(
			UserScope userScope,
			TenantScope tenantScope,
			QueryFactory queryFactory,
			ConventionService conventionService,
			AffiliatedIndicatorsCacheService affiliatedIndicatorsCacheService,
			PrincipalIndicatorResourceCacheService principalIndicatorResourceCacheService,
			HierarchyIndicatorColumnAccessCacheService hierarchyIndicatorColumnAccessCacheService,
			ClaimExtractor claimExtractor,
			CurrentPrincipalResolver currentPrincipalResolver,
			AuthorizationService authorizationService,
			JsonHandlingService jsonHandlingService,
			MessageSource messageSource) {
		this.userScope = userScope;
		this.tenantScope = tenantScope;
		this.queryFactory = queryFactory;
		this.conventionService = conventionService;
		this.affiliatedIndicatorsCacheService = affiliatedIndicatorsCacheService;
		this.principalIndicatorResourceCacheService = principalIndicatorResourceCacheService;
		this.hierarchyIndicatorColumnAccessCacheService = hierarchyIndicatorColumnAccessCacheService;
		this.claimExtractor = claimExtractor;
		this.currentPrincipalResolver = currentPrincipalResolver;
		this.authorizationService = authorizationService;
		this.jsonHandlingService = jsonHandlingService;
		this.messageSource = messageSource;
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
				indicatorIds(indicatorId).hasUser(false).collectAs(new BaseFieldSet().ensure(IndicatorAccess._id).ensure(this.conventionService.asIndexer(IndicatorAccess._indicator, Indicator._id)).ensure(IndicatorAccess._user));
		try {
			indicatorAccesses.addAll(this.queryFactory.query(IndicatorAccessQuery.class).isActive(IsActive.ACTIVE).
					indicatorIds(indicatorId).userIds(this.userScope.getUserId()).collectAs(new BaseFieldSet().ensure(IndicatorAccess._id).ensure(this.conventionService.asIndexer(IndicatorAccess._indicator, Indicator._id)).ensure(IndicatorAccess._user)));
		} catch (InvalidApplicationException e) {
			throw new RuntimeException(e);
		}
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

	public List<HierarchyIndicatorColumnAccess> indicatorAllowedKeywords(UUID... indicatorIds) {
		if (indicatorIds == null || indicatorIds.length == 0) return new ArrayList<>();
		UUID userId = this.userScope.getUserIdSafe();
		if (userId == null) return new ArrayList<>();

		HierarchyIndicatorColumnAccessCacheService.HierarchyIndicatorColumnAccessCacheValue cacheValue = this.hierarchyIndicatorColumnAccessCacheService.lookup(this.hierarchyIndicatorColumnAccessCacheService.buildKey(userId, List.of(indicatorIds)));
		if (cacheValue == null) {
			List<IndicatorAccessEntity> indicatorAccesses = null;
			List<IndicatorEntity> indicators = null;
			try {
				indicatorAccesses = this.queryFactory.query(IndicatorAccessQuery.class).indicatorIds(indicatorIds).hasUser(false).isActive(IsActive.ACTIVE).tenantIds(this.tenantScope.getTenant()).collectAs(new BaseFieldSet().ensure(IndicatorAccess._config).ensure(IndicatorAccess._indicator));
				indicatorAccesses.addAll(this.queryFactory.query(IndicatorAccessQuery.class).indicatorIds(indicatorIds).userIds(this.userScope.getUserId()).isActive(IsActive.ACTIVE).tenantIds(this.tenantScope.getTenant()).collectAs(new BaseFieldSet().ensure(IndicatorAccess._config).ensure(IndicatorAccess._indicator)));
				indicators = this.queryFactory.query(IndicatorQuery.class).ids(indicatorIds).isActive(IsActive.ACTIVE).collectAs(new BaseFieldSet().ensure(Indicator._id).ensure(Indicator._config));
			} catch (InvalidApplicationException e) {
				throw new RuntimeException(e);
			}
			List<HierarchyIndicatorColumnAccess> items = new ArrayList<>();
			for (IndicatorAccessEntity indicatorAccess : indicatorAccesses) {
				items = this.buildHierarchyIndicatorColumnAccesses(items, indicators, indicatorAccess);
			}
			cacheValue = new HierarchyIndicatorColumnAccessCacheService.HierarchyIndicatorColumnAccessCacheValue(userId, List.of(indicatorIds), items);
			this.hierarchyIndicatorColumnAccessCacheService.put(cacheValue);
		}
		return cacheValue.getHierarchyIndicatorColumnAccesses();
	}
	
	private List<HierarchyIndicatorColumnAccess> buildHierarchyIndicatorColumnAccesses(List<HierarchyIndicatorColumnAccess> allhierarchyIndicatorColumnAccesses, List<IndicatorEntity> indicators, IndicatorAccessEntity indicatorAccess){
		IndicatorAccessConfigEntity indicatorAccessConfig = this.jsonHandlingService.fromJsonSafe(IndicatorAccessConfigEntity.class, indicatorAccess.getConfig());
		if (indicatorAccessConfig == null || indicatorAccessConfig.getAllFilterColumns() == null || indicatorAccessConfig.getAllFilterColumns().isEmpty()) return allhierarchyIndicatorColumnAccesses;

		List<IndicatorColumnAccess> tempFilterColumns = new ArrayList<>();
		tempFilterColumns.addAll(indicatorAccessConfig.getAllFilterColumns().stream().map(x -> {
			IndicatorColumnAccess indicatorColumnAccess = new IndicatorColumnAccess();
			indicatorColumnAccess.setField(x.getColumn());
			indicatorColumnAccess.setValues(x.getValues());
			return indicatorColumnAccess;
		}).collect(Collectors.toList()));

		IndicatorEntity indicator =  indicators.stream().filter(x-> x.getId().equals(indicatorAccess.getIndicatorId())).findFirst().orElse(null);
		if (indicator == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{indicatorAccess.getIndicatorId(), Indicator.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		IndicatorConfigEntity indicatorConfig = this.jsonHandlingService.fromJsonSafe(IndicatorConfigEntity.class, indicator.getConfig());
		
		if (indicatorConfig != null && indicatorConfig.getAccessRequestConfig() != null && indicatorConfig.getAccessRequestConfig().getFilterColumns() != null && !indicatorConfig.getAccessRequestConfig().getFilterColumns().isEmpty() ){
			for (IndicatorColumnAccess tempFilterColumn : tempFilterColumns) {
				String code = tempFilterColumn.getField();
				FilterColumnConfigEntity indicatorFilterColumnConfig = indicatorConfig.getAccessRequestConfig().getFilterColumns().stream().filter(x -> x.getCode().equals(code)).findFirst().orElse(null);
				if (indicatorFilterColumnConfig != null) {
					if (tempFilterColumn.getValues() != null && !tempFilterColumn.getValues().isEmpty()) {
						List<String> fieldsHierarchy = this.getDependsOnFieldsHierarchy(indicatorConfig.getAccessRequestConfig().getFilterColumns(), code);
						for (String value : tempFilterColumn.getValues()) {
							HierarchyIndicatorColumnAccess indicatorColumnAccess = this.buildHierarchyIndicatorColumnAccess(indicator, fieldsHierarchy, 0, code, value);
							boolean hasMerge = false;
							for (HierarchyIndicatorColumnAccess hierarchyIndicatorColumnAccess: allhierarchyIndicatorColumnAccesses) {
								if(hierarchyIndicatorColumnAccess.tryMerge(indicatorColumnAccess)){
									hasMerge = true;
									break;
								}
							}
							if (!hasMerge){
								allhierarchyIndicatorColumnAccesses.add(indicatorColumnAccess);
							}
						}
					}
				}
			}
		}
		
		return allhierarchyIndicatorColumnAccesses;
	}
	
	private HierarchyIndicatorColumnAccess buildHierarchyIndicatorColumnAccess(IndicatorEntity indicator, List<String> fieldsHierarchy, int level, String code, String value){
		String levelCode = fieldsHierarchy.get(level);
		HierarchyIndicatorColumnAccess indicatorColumnAccess = new HierarchyIndicatorColumnAccess();
		if (levelCode.equals(code)) {
			indicatorColumnAccess.setCode(code);
			indicatorColumnAccess.setValue(value);
			return indicatorColumnAccess;
		} else {
			level++;
			
			DistinctValuesResponse<String> distinctValuesResponse = this.queryFactory.query(IndicatorPointQuery.class).indicatorIds(indicator.getId()).keywordFilters(new IndicatorPointKeywordFilter(code, List.of(value))).collectDistinct(levelCode, SortOrder.ASC, x-> x, null, 10);
			if (distinctValuesResponse.getItems() == null || distinctValuesResponse.getItems().isEmpty())throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{code, FilterColumnConfigEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
			String fieldHierarchyValue = distinctValuesResponse.getItems().stream().filter(x-> !this.conventionService.isNullOrEmpty(x)).findFirst().orElse(null);
			if (this.conventionService.isNullOrEmpty(fieldHierarchyValue))throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{code, FilterColumnConfigEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));

			indicatorColumnAccess.setCode(levelCode);
			indicatorColumnAccess.setValue(fieldHierarchyValue);
			indicatorColumnAccess.setChildItems(List.of(this.buildHierarchyIndicatorColumnAccess(indicator, fieldsHierarchy, level, code, value)));
		} 

		return  indicatorColumnAccess;
	}
	
	private List<String> getDependsOnFieldsHierarchy(List<FilterColumnConfigEntity> allFilterColumnConfigs, String code){
		List<String> items = new ArrayList<>();
		FilterColumnConfigEntity indicatorFilterColumnConfig = allFilterColumnConfigs.stream().filter(x-> x.getCode().contains(code)).findFirst().orElse(null);
		if (indicatorFilterColumnConfig == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{code, FilterColumnConfigEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		
		if (!this.conventionService.isNullOrEmpty(indicatorFilterColumnConfig.getDependsOnCode())){
			items = this.getDependsOnFieldsHierarchy(allFilterColumnConfigs, indicatorFilterColumnConfig.getDependsOnCode());
		}
		items.add(indicatorFilterColumnConfig.getCode());
		return  items;
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

		List<IndicatorAccessEntity> indicatorAccesses = this.queryFactory.query(IndicatorAccessQuery.class).isActive(IsActive.ACTIVE).hasUser(false).collectAs(new BaseFieldSet().ensure(IndicatorAccess._id).ensure(this.conventionService.asIndexer(IndicatorAccess._indicator, Indicator._id)).ensure(IndicatorAccess._user));
		try {
			indicatorAccesses.addAll(this.queryFactory.query(IndicatorAccessQuery.class).isActive(IsActive.ACTIVE).userIds(this.userScope.getUserId()).collectAs(new BaseFieldSet().ensure(IndicatorAccess._id).ensure(this.conventionService.asIndexer(IndicatorAccess._indicator, Indicator._id)).ensure(IndicatorAccess._user)));
		} catch (InvalidApplicationException e) {
			throw new RuntimeException(e);
		}

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

