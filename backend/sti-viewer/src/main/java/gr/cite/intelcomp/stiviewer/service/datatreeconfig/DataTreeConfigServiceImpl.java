package gr.cite.intelcomp.stiviewer.service.datatreeconfig;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationContentResolver;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.IndicatorFieldBaseType;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.enums.UserSettingsEntityType;
import gr.cite.intelcomp.stiviewer.common.enums.UserSettingsType;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.common.types.datatreeconfig.*;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.IndicatorAccessEntity;
import gr.cite.intelcomp.stiviewer.data.IndicatorEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.data.UserSettingsEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.FieldEntity;
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
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.query.DistinctValuesResponse;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

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
			JsonHandlingService jsonHandlingService
	) {
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
	}

	@Override
	public List<DataTreeConfig> getMyConfigs(FieldSet fields) {
		List<DataTreeConfigEntity> browseDataTreeConfigEntities = this.getMyConfigs();
		return this.builderFactory.builder(BrowseDataTreeConfigBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, DataTreeConfig._id), browseDataTreeConfigEntities);
	}

	private List<DataTreeConfigEntity> getMyConfigs() {
		List<UserSettingsEntity> userSettingsEntities = this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.BrowseDataTree).entityTypes(UserSettingsEntityType.Application).collectAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		List<DataTreeConfigEntity> browseDataTreeConfigEntities = new ArrayList<>();
		if (userSettingsEntities != null) {
			for (UserSettingsEntity userSettingsEntity : userSettingsEntities) {
				DataTreeConfigEntity[] dataTreeConfigs = this.jsonHandlingService.fromJsonSafe(DataTreeConfigEntity[].class, userSettingsEntity.getValue());
				if (browseDataTreeConfigEntities != null) {
					for (DataTreeConfigEntity dataTreeConfig : dataTreeConfigs) {
						browseDataTreeConfigEntities.add(dataTreeConfig);
					}
				}
			}
		}
		return browseDataTreeConfigEntities;
	}

	@Override
	public DataTreeLevel getIndicatorReportLevel(IndicatorReportLevelLookup lookup, FieldSet fields) {
		List<DataTreeConfigEntity> browseDataTreeConfigEntities = this.getMyConfigs();
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

		DataTreeLevelEntity browseDataTreeLevelType = new DataTreeLevelEntity();
		browseDataTreeLevelType.setField(nextLevelConfig.getField());
		browseDataTreeLevelType.setOrder(nextLevelConfig.getOrder());
		browseDataTreeLevelType.setSupportedDashboards(nextLevelConfig.getDefaultDashboards());
		browseDataTreeLevelType.setSupportSubLevel(nextLevelConfig.getSupportSubLevel());

		List<UUID> indicatorIds = null;
		if (this.authorizationService.authorize(Permission.BrowseIndicatorPoint)) {
			List<IndicatorEntity> indicators = this.queryFactory.query(IndicatorQuery.class).isActive(IsActive.ACTIVE).collectAs(new BaseFieldSet().ensure(Indicator._id));
			indicatorIds = indicators.stream().map(x -> x.getId()).distinct().collect(Collectors.toList());
		} else {
			List<IndicatorAccessEntity> indicatorAccesses = this.queryFactory.query(IndicatorAccessQuery.class).isActive(IsActive.ACTIVE).collectAs(new BaseFieldSet().ensure(this.conventionService.asIndexer(IndicatorAccess._indicator, Indicator._id), IndicatorAccess._id));
			indicatorIds = indicatorAccesses.stream().map(x -> x.getIndicatorId()).distinct().collect(Collectors.toList());
		}

		List<DataTreeLevelItemEntity> indicatorReportConfigs = new ArrayList<>();
		for (UUID indicatorId : indicatorIds) {
			List<DataTreeLevelItemEntity> itemEntities = this.collectBrowseDataTreeLevelItems(lookup, indicatorId, nextLevelConfig);
			for (DataTreeLevelItemEntity itemEntity : itemEntities) {
				if (indicatorReportConfigs.stream().filter(x -> Objects.equals(x.getValue(), itemEntity.getValue())).count() == 0) indicatorReportConfigs.add(itemEntity);
			}
		}
		browseDataTreeLevelType.setItems(indicatorReportConfigs.stream().sorted(Comparator.comparing(DataTreeLevelItemEntity::getValue)).collect(Collectors.toList()));

		return this.builderFactory.builder(BrowseDataTreeLevelBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fields, browseDataTreeLevelType);
	}

	private List<DataTreeLevelItemEntity> collectBrowseDataTreeLevelItems(IndicatorReportLevelLookup lookup, UUID indicatorId, DataTreeLevelConfigEntity nextLevelConfig) {
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

		DistinctValuesResponse<String> items = null;
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
			items = query.indicatorIds(indicatorId).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicatorOrIndicatorAccess).useThisColumnForAccess(distinctKey).collectDistinct(distinctKey, SortOrder.ASC, (x) -> x, afterKey);

			for (String item : items.getItems()) {
				DataTreeLevelItemEntity levelConfigItem = new DataTreeLevelItemEntity();
				levelConfigItem.setValue(item);
				if (nextLevelConfig.getDashboardOverrides() != null) {
					nextLevelDashboardOverrideFieldRequirementEntity.setValue(item);
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
			afterKey = items.getAfterKey();
		} while (items != null && !items.getItems().isEmpty() && afterKey != null && indicatorReportConfigs.size() < items.getTotal());
		return indicatorReportConfigs;
	}
}
