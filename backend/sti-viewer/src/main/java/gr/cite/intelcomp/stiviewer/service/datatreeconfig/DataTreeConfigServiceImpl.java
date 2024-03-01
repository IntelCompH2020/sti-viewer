package gr.cite.intelcomp.stiviewer.service.datatreeconfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationContentResolver;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.OwnedResource;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.IndicatorFieldBaseType;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.enums.UserSettingsEntityType;
import gr.cite.intelcomp.stiviewer.common.enums.UserSettingsType;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.common.types.datalastview.DataTreeLastViewConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.datatreeconfig.*;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.IndicatorAccessEntity;
import gr.cite.intelcomp.stiviewer.data.IndicatorEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.data.UserSettingsEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.FieldEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.IndicatorPointEntity;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpoint.IndicatorPointQuery;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter.*;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.IndicatorAccess;
import gr.cite.intelcomp.stiviewer.model.UserSettings;
import gr.cite.intelcomp.stiviewer.model.builder.datatreeconfig.BrowseDataTreeConfigBuilder;
import gr.cite.intelcomp.stiviewer.model.builder.datatreeconfig.BrowseDataTreeLevelBuilder;
import gr.cite.intelcomp.stiviewer.model.datatreeconfig.DataTreeConfig;
import gr.cite.intelcomp.stiviewer.model.datatreeconfig.DataTreeLevel;
import gr.cite.intelcomp.stiviewer.query.IndicatorAccessQuery;
import gr.cite.intelcomp.stiviewer.query.IndicatorQuery;
import gr.cite.intelcomp.stiviewer.query.UserSettingsQuery;
import gr.cite.intelcomp.stiviewer.query.lookup.IndicatorReportLevelLookup;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorConfigItem;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorConfigService;
import gr.cite.intelcomp.stiviewer.service.indicatorgroup.IndicatorGroupService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.query.Aggregation.*;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequestScope
public class DataTreeConfigServiceImpl implements DataTreeConfigService {
	private final TenantEntityManager entityManager;
	private final QueryFactory queryFactory;
	private final ElasticsearchRestTemplate elasticsearchTemplate;
	private final AuthorizationService authorizationService;
	private final AuthorizationContentResolver authorizationContentResolver;
	private final UserScope userScope;
	private final BuilderFactory builderFactory;
	private final ConventionService conventionService;
	private final MessageSource messageSource;
	private final IndicatorConfigService indicatorConfigService;
	private final JsonHandlingService jsonHandlingService;

	private final IndicatorGroupService indicatorGroupService;
	private final TenantScope tenantScope;
	public DataTreeConfigServiceImpl(
			TenantEntityManager entityManager,
			QueryFactory queryFactory,
			ElasticsearchRestTemplate elasticsearchTemplate,
			AuthorizationService authorizationService,
			AuthorizationContentResolver authorizationContentResolver,
			UserScope userScope,
			BuilderFactory builderFactory,
			ConventionService conventionService,
			MessageSource messageSource,
			IndicatorConfigService indicatorConfigService,
			JsonHandlingService jsonHandlingService,
			IndicatorGroupService indicatorGroupService, 
			TenantScope tenantScope) {
		this.entityManager = entityManager;
		this.queryFactory = queryFactory;
		this.elasticsearchTemplate = elasticsearchTemplate;
		this.authorizationService = authorizationService;
		this.authorizationContentResolver = authorizationContentResolver;
		this.userScope = userScope;
		this.builderFactory = builderFactory;
		this.conventionService = conventionService;
		this.messageSource = messageSource;
		this.indicatorConfigService = indicatorConfigService;
		this.jsonHandlingService = jsonHandlingService;
		this.indicatorGroupService = indicatorGroupService;
		this.tenantScope = tenantScope;
	}

	@Override
	public List<DataTreeConfig> getMyConfigs(FieldSet fields) throws InvalidApplicationException {
		List<DataTreeConfigEntity> browseDataTreeConfigEntities = this.getMyDataTreeConfigs();
		return this.builderFactory.builder(BrowseDataTreeConfigBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, DataTreeConfig._id), browseDataTreeConfigEntities);
	}

	@Override
	public List<DataTreeConfig> getMyConfigByKey(String key, FieldSet fields) throws InvalidApplicationException {
		if (this.conventionService.isNullOrEmpty(key)) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{key, UserSettingsEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		
		UserSettingsEntity userSettingsEntity = this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.BrowseDataTree).entityTypes(UserSettingsEntityType.User).entityIds(this.userScope.getUserId()).keys(key).firstAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		if (userSettingsEntity == null && this.tenantScope.isSet()) userSettingsEntity = this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.BrowseDataTree).entityTypes(UserSettingsEntityType.Tenant).entityIds(this.tenantScope.getTenant()).keys(key).firstAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		if (userSettingsEntity == null) userSettingsEntity =this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.BrowseDataTree).entityTypes(UserSettingsEntityType.Application).keys(key).firstAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		if (userSettingsEntity == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{key, UserSettingsEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		
		DataTreeConfigEntity[] dataTreeConfigs = this.jsonHandlingService.fromJsonSafe(DataTreeConfigEntity[].class, userSettingsEntity.getValue());
		if (dataTreeConfigs == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{key, UserSettingsEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		return this.builderFactory.builder(BrowseDataTreeConfigBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, DataTreeConfig._id), List.of(dataTreeConfigs));
	}

	private List<DataTreeConfigEntity> getMyDataTreeConfigs() throws InvalidApplicationException {
		List<UserSettingsEntity> userSettingsEntities = this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.BrowseDataTree).entityTypes(UserSettingsEntityType.User).entityIds(this.userScope.getUserId()).collectAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		if ((userSettingsEntities == null || userSettingsEntities.isEmpty()) && this.tenantScope.isSet()) userSettingsEntities = this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.BrowseDataTree).entityTypes(UserSettingsEntityType.Tenant).entityIds(this.tenantScope.getTenant()).collectAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		if (userSettingsEntities == null || userSettingsEntities.isEmpty()) userSettingsEntities = this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.BrowseDataTree).entityTypes(UserSettingsEntityType.Application).collectAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		
		List<DataTreeConfigEntity> browseDataTreeConfigEntities = new ArrayList<>();
		if (userSettingsEntities != null) {
			for (UserSettingsEntity userSettingsEntity : userSettingsEntities) {
				DataTreeConfigEntity[] dataTreeConfigs = this.jsonHandlingService.fromJsonSafe(DataTreeConfigEntity[].class, userSettingsEntity.getValue());
				if (dataTreeConfigs != null) {
					for (DataTreeConfigEntity dataTreeConfig : dataTreeConfigs) {
						browseDataTreeConfigEntities.add(dataTreeConfig);
					}
				}
			}
		}
		return browseDataTreeConfigEntities;
	}

	public void updateLastAccess(String configId) throws InvalidApplicationException, JsonProcessingException {
		UUID userId = userScope.getUserIdSafe();
		this.authorizationService.authorizeAtLeastOneForce(List.of(new OwnedResource(userId)));
		UserSettingsEntity data = this.queryFactory.query(UserSettingsQuery.class).keys(configId).types(UserSettingsType.DataTreeLastView).entityTypes(UserSettingsEntityType.User).entityIds(userScope.getUserId()).first();
		boolean isUpdate = data != null;
		if (!isUpdate) {
			data = new UserSettingsEntity();
			data.setId(UUID.randomUUID());
			data.setKey(configId);
			data.setEntityId(userId);
			data.setEntityType(UserSettingsEntityType.User);
			data.setCreatedAt(Instant.now());
			data.setType(UserSettingsType.DataTreeLastView);
			data.setName(configId);
		}
		DataTreeLastViewConfigEntity dataTreeLastViewConfigEntity = new DataTreeLastViewConfigEntity();
		dataTreeLastViewConfigEntity.setLastAccess(Instant.now());
		data.setValue(this.jsonHandlingService.toJson(dataTreeLastViewConfigEntity));
		data.setUpdatedAt(Instant.now());

		if (isUpdate) this.entityManager.merge(data);
		else this.entityManager.persist(data);

		this.entityManager.flush();
	}

	private DataTreeLastViewConfigEntity getDataTreeLastViewConfig(String configId) {
		List<UserSettingsEntity> userSettingsEntities = null;
		try {
			userSettingsEntities = this.queryFactory.query(UserSettingsQuery.class).keys(configId).types(UserSettingsType.DataTreeLastView).entityTypes(UserSettingsEntityType.User).entityIds(userScope.getUserId())
					.collectAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		} catch (InvalidApplicationException e) {
			userSettingsEntities = null;
		}
		if (userSettingsEntities != null) {
			for (UserSettingsEntity userSettingsEntity : userSettingsEntities) {
				DataTreeLastViewConfigEntity lastViewConfigEntity = this.jsonHandlingService.fromJsonSafe(DataTreeLastViewConfigEntity.class, userSettingsEntity.getValue());
				return lastViewConfigEntity;
			}
		}
		return null;
	}

	@Override
	public DataTreeLevel getIndicatorReportLevel(IndicatorReportLevelLookup lookup, FieldSet fields) throws InvalidApplicationException {
		List<DataTreeConfigEntity> browseDataTreeConfigEntities = this.getMyDataTreeConfigs();
		DataTreeConfigEntity viewConfig = browseDataTreeConfigEntities.stream().filter(x -> x.getId().equals(lookup.getConfigId())).findFirst().orElse(null);
		if (viewConfig == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{lookup.getConfigId(), DataTreeConfigEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		DataTreeLevelConfigEntity nextLevelConfig = null;
		for (DataTreeLevelConfigEntity dataTreeLevelConfigEntity : viewConfig.getLevelConfigs().stream().sorted(Comparator.comparingInt(DataTreeLevelConfigEntity::getOrder)).collect(Collectors.toList())) {
			if (lookup.getSelectedLevels() == null || !lookup.getSelectedLevels().stream().anyMatch(x -> x.equals(dataTreeLevelConfigEntity.getField().getCode()))) {
				nextLevelConfig = dataTreeLevelConfigEntity;
				break;
			}
		}
		if (nextLevelConfig == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{lookup.getConfigId(), DataTreeLevelConfigEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		DataTreeLastViewConfigEntity dataTreeLastViewConfig = this.getDataTreeLastViewConfig(lookup.getConfigId());
		if (dataTreeLastViewConfig == null && !this.conventionService.isNullOrEmpty(lookup.getParentConfigId())) dataTreeLastViewConfig = this.getDataTreeLastViewConfig(lookup.getParentConfigId());
		if (dataTreeLastViewConfig == null){
			dataTreeLastViewConfig = new DataTreeLastViewConfigEntity();
			dataTreeLastViewConfig.setLastAccess(Instant.MIN);
		}
		DataTreeLevelEntity browseDataTreeLevelType = new DataTreeLevelEntity();
		browseDataTreeLevelType.setField(nextLevelConfig.getField());
		browseDataTreeLevelType.setOrder(nextLevelConfig.getOrder());
		browseDataTreeLevelType.setSupportedDashboards(nextLevelConfig.getDefaultDashboards());
		browseDataTreeLevelType.setSupportSubLevel(nextLevelConfig.getSupportSubLevel());

		List<UUID> indicatorIds = null;
		IndicatorGroupEntity indicatorGroupEntity = this.conventionService.isNullOrEmpty(viewConfig.getIndicatorGroupCode()) ? null : this.indicatorGroupService.getIndicatorGroupByCode(viewConfig.getIndicatorGroupCode());
		if (this.authorizationService.authorize(Permission.BrowseIndicatorPoint)) {
			List<IndicatorEntity> indicators = this.queryFactory.query(IndicatorQuery.class).isActive(IsActive.ACTIVE).ids(indicatorGroupEntity == null ? null :indicatorGroupEntity.getIndicatorIds()).collectAs(new BaseFieldSet().ensure(Indicator._id));
			indicatorIds = indicators.stream().map(x -> x.getId()).distinct().collect(Collectors.toList());
		} else {
			List<IndicatorAccessEntity> indicatorAccesses = this.queryFactory.query(IndicatorAccessQuery.class).isActive(IsActive.ACTIVE).hasUser(false).indicatorIds(indicatorGroupEntity == null ? null :indicatorGroupEntity.getIndicatorIds()).collectAs(new BaseFieldSet().ensure(this.conventionService.asIndexer(IndicatorAccess._indicator, Indicator._id), IndicatorAccess._id));
			indicatorAccesses.addAll(this.queryFactory.query(IndicatorAccessQuery.class).isActive(IsActive.ACTIVE).userIds(userScope.getUserId()).indicatorIds(indicatorGroupEntity == null ? null :indicatorGroupEntity.getIndicatorIds()).collectAs(new BaseFieldSet().ensure(this.conventionService.asIndexer(IndicatorAccess._indicator, Indicator._id), IndicatorAccess._id)));
			indicatorIds = indicatorAccesses.stream().map(x -> x.getIndicatorId()).distinct().collect(Collectors.toList());
		}

		List<DataTreeLevelItemEntity> indicatorReportConfigs = new ArrayList<>();
		for (UUID indicatorId : indicatorIds) {
			List<DataTreeLevelItemEntity> itemEntities = this.collectBrowseDataTreeLevelItems(lookup, indicatorId, nextLevelConfig, dataTreeLastViewConfig);
			for (DataTreeLevelItemEntity itemEntity : itemEntities) {
				DataTreeLevelItemEntity dataTreeLevelItemEntity = indicatorReportConfigs.stream().filter(x -> Objects.equals(x.getValue(), itemEntity.getValue())).findFirst().orElse(null);
				if (dataTreeLevelItemEntity == null) indicatorReportConfigs.add(itemEntity);
				else if (itemEntity.getHasNewData()) dataTreeLevelItemEntity.setHasNewData(true);
			}
		}
		browseDataTreeLevelType.setItems(indicatorReportConfigs.stream().sorted(Comparator.comparing(DataTreeLevelItemEntity::getValue)).collect(Collectors.toList()));

		return this.builderFactory.builder(BrowseDataTreeLevelBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fields, browseDataTreeLevelType);
	}

	private List<DataTreeLevelItemEntity> collectBrowseDataTreeLevelItems(IndicatorReportLevelLookup lookup, UUID indicatorId, DataTreeLevelConfigEntity nextLevelConfig, DataTreeLastViewConfigEntity lastViewItemConfig) {
		List<DataTreeLevelItemEntity> indicatorReportConfigs = new ArrayList<>();

		IndicatorConfigItem indicatorConfigItem = this.indicatorConfigService.getConfig(indicatorId);
		if (indicatorConfigItem == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{indicatorId, IndicatorConfigItem.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		FieldEntity nextLevelField = indicatorConfigItem.getExtraProps().get(nextLevelConfig.getField().getCode());
		if (nextLevelField == null) return indicatorReportConfigs;

		List<DataTreeLevelDashboardOverrideFieldRequirementEntity> dashboardOverrideFieldRequirementEntities = new ArrayList<>();
		if (lookup.getSelectedLevels().size() > 0) {
			List<String> notFoundFilters = null;
			if (lookup.getFilters() == null || lookup.getFilters().getKeywordFilters() == null) {
				notFoundFilters = lookup.getSelectedLevels();
			} else {
				List<String> keywordFiltersFields = lookup.getFilters().getKeywordFilters().stream().map(y -> y.getField()).collect(Collectors.toList());
				notFoundFilters = lookup.getSelectedLevels().stream().filter(x -> !keywordFiltersFields.contains(x)).collect(Collectors.toList());
			}
			if (notFoundFilters != null && notFoundFilters.size() > 0) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{String.join(",", notFoundFilters), "SelectedLevels"}, LocaleContextHolder.getLocale()));
		}

		if (lookup.getFilters() != null) {
			if (lookup.getFilters().getKeywordFilters() != null) {
				for (IndicatorPointKeywordFilter keywordFilter : lookup.getFilters().getKeywordFilters()) {
					FieldEntity filterField = indicatorConfigItem.getExtraProps().get(keywordFilter.getField());
					if (filterField == null) return indicatorReportConfigs;
					if (keywordFilter.getValues() != null && !keywordFilter.getValues().isEmpty()) { //TODO
						DataTreeLevelDashboardOverrideFieldRequirementEntity dashboardOverrideFieldRequirement = new DataTreeLevelDashboardOverrideFieldRequirementEntity();
						dashboardOverrideFieldRequirement.setField(keywordFilter.getField());
						dashboardOverrideFieldRequirement.setValue(keywordFilter.getValues().get(0)); //TODO
						dashboardOverrideFieldRequirementEntities.add(dashboardOverrideFieldRequirement);
					}
				}
			}

			if (lookup.getFilters().getDateFilters() != null) {
				for (IndicatorPointDateFilter f : lookup.getFilters().getDateFilters()) {
					FieldEntity filterField = indicatorConfigItem.getExtraProps().get(f.getField());
					if (filterField == null) return indicatorReportConfigs;
				}
			}

			if (lookup.getFilters().getIntegerFilters() != null) {
				for (IndicatorPointIntegerFilter f : lookup.getFilters().getIntegerFilters()) {
					FieldEntity filterField = indicatorConfigItem.getExtraProps().get(f.getField());
					if (filterField == null) return indicatorReportConfigs;
				}
			}

			if (lookup.getFilters().getDoubleFilters() != null) {
				for (IndicatorPointDoubleFilter f : lookup.getFilters().getDoubleFilters()) {
					FieldEntity filterField = indicatorConfigItem.getExtraProps().get(f.getField());
					if (filterField == null) return indicatorReportConfigs;
				}
			}

			if (lookup.getFilters().getDateRangeFilters() != null) {
				for (IndicatorPointDateRangeFilter f : lookup.getFilters().getDateRangeFilters()) {
					FieldEntity filterField = indicatorConfigItem.getExtraProps().get(f.getField());
					if (filterField == null) return indicatorReportConfigs;
				}
			}

			if (lookup.getFilters().getDoubleRangeFilters() != null) {
				for (IndicatorPointDoubleRangeFilter f : lookup.getFilters().getDoubleRangeFilters()) {
					FieldEntity filterField = indicatorConfigItem.getExtraProps().get(f.getField());
					if (filterField == null) return indicatorReportConfigs;
				}
			}

			if (lookup.getFilters().getIntegerRangeFilters() != null) {
				for (IndicatorPointIntegerRangeFilter f : lookup.getFilters().getIntegerRangeFilters()) {
					FieldEntity filterField = indicatorConfigItem.getExtraProps().get(f.getField());
					if (filterField == null) return indicatorReportConfigs;
				}
			}

			if (lookup.getFilters().getFieldLikeFilter() != null && lookup.getFilters().getFieldLikeFilter().getFields() != null) {
				for (String f : lookup.getFilters().getFieldLikeFilter().getFields()) {
					FieldEntity filterField = indicatorConfigItem.getExtraProps().get(f);
					if (filterField == null) return indicatorReportConfigs;
				}
			}
		}

		AggregateResponse aggregateResponse = null;
		Map<String, Object> afterKey = null;
		DataTreeLevelDashboardOverrideFieldRequirementEntity nextLevelDashboardOverrideFieldRequirementEntity = new DataTreeLevelDashboardOverrideFieldRequirementEntity();
		nextLevelDashboardOverrideFieldRequirementEntity.setField(nextLevelConfig.getField().getCode());
		dashboardOverrideFieldRequirementEntities.add(nextLevelDashboardOverrideFieldRequirementEntity);
		do {
			String distinctKey = nextLevelConfig.getField().getCode();
			if (nextLevelField.getBaseType() == IndicatorFieldBaseType.DoubleMap || nextLevelField.getBaseType() == IndicatorFieldBaseType.IntegerMap) {
				distinctKey = nextLevelConfig.getField().getCode() + ".key";
			}

			IndicatorPointQuery query = lookup.getFilters() == null ? this.queryFactory.query(IndicatorPointQuery.class) : lookup.getFilters().enrich(this.queryFactory);
			query.setPage(null);
			query.setOrder(null);
			AggregationQuery aggregationQuery = new AggregationQuery();
			aggregationQuery.setBucketAggregate(new Terms(distinctKey));
			aggregationQuery.getBucketAggregate().setMetrics(new ArrayList<>());
			aggregationQuery.getBucketAggregate().getMetrics().add(new Metric(IndicatorPointEntity.Fields.timestamp, MetricAggregateType.Max));
			aggregateResponse = query.indicatorIds(indicatorId).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicatorOrIndicatorAccess).collectAggregate(aggregationQuery);
			
			for (AggregateResponseItem item : aggregateResponse.getItems()) {
				if (item.getGroup() == null || item.getGroup().getItems() == null || !item.getGroup().getItems().containsKey(distinctKey)) continue;
				DataTreeLevelItemEntity levelConfigItem = new DataTreeLevelItemEntity();
				levelConfigItem.setValue(item.getGroup().getItems().getOrDefault(distinctKey, null));
				AggregateResponseValue maxTimestampValue = item.getValues() != null ? item.getValues().stream().filter(x-> x.getAggregateType().equals(MetricAggregateType.Max) && IndicatorPointEntity.Fields.timestamp.equals(x.getField())).findFirst().orElse(null) : null;
				Instant maxTimestamp = Instant.now();
				if (maxTimestampValue != null) maxTimestamp = Instant.ofEpochMilli(maxTimestampValue.getValue().longValue());
				levelConfigItem.setHasNewData(lastViewItemConfig.getLastAccess().isBefore(maxTimestamp));
				if (nextLevelConfig.getDashboardOverrides() != null) {
					nextLevelDashboardOverrideFieldRequirementEntity.setValue(item.getGroup().getItems().get(distinctKey));
					List<DataTreeLevelDashboardOverrideEntity> dashboardOverrideEntities = nextLevelConfig.getDashboardOverrides().stream().filter(x -> x.applies(dashboardOverrideFieldRequirementEntities)).collect(Collectors.toList());
					if (!dashboardOverrideEntities.isEmpty()) {
						levelConfigItem.setSupportedDashboards(dashboardOverrideEntities.stream().map(x -> x.getSupportedDashboards()).flatMap(List::stream).distinct().collect(Collectors.toList()));

						if (dashboardOverrideEntities.stream().anyMatch(x -> x.getSupportSubLevel() != null)) {
							levelConfigItem.setSupportSubLevel(dashboardOverrideEntities.stream().filter(x -> x.getSupportSubLevel() != null).map(x -> x.getSupportSubLevel()).findFirst().orElse(null));
						}
					}
				}
				indicatorReportConfigs.add(levelConfigItem);
			}
			afterKey = aggregateResponse.getAfterKey();
		} while (aggregateResponse.getItems() != null && !aggregateResponse.getItems().isEmpty() && afterKey != null /*&& indicatorReportConfigs.size() < aggregateResponse.sl()*/);
		return indicatorReportConfigs;
	}
}
