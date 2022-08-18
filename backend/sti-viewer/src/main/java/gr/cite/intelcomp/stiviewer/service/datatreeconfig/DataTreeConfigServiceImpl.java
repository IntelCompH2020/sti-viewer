package gr.cite.intelcomp.stiviewer.service.datatreeconfig;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationContentResolver;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.IndicatorFieldBaseType;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.enums.UserSettingsEntityType;
import gr.cite.intelcomp.stiviewer.common.enums.UserSettingsType;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.common.types.datatreeconfig.DataTreeConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.datatreeconfig.DataTreeLevelConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.datatreeconfig.DataTreeLevelEntity;
import gr.cite.intelcomp.stiviewer.common.types.datatreeconfig.DataTreeLevelItemEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.IndicatorAccessEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.data.UserSettingsEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.FieldEntity;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpoint.IndicatorPointQuery;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter.IndicatorPointKeywordFilter;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.IndicatorAccess;
import gr.cite.intelcomp.stiviewer.model.UserSettings;
import gr.cite.intelcomp.stiviewer.model.builder.datatreeconfig.BrowseDataTreeConfigBuilder;
import gr.cite.intelcomp.stiviewer.model.builder.datatreeconfig.BrowseDataTreeLevelBuilder;
import gr.cite.intelcomp.stiviewer.model.datatreeconfig.DataTreeConfig;
import gr.cite.intelcomp.stiviewer.model.datatreeconfig.DataTreeLevel;
import gr.cite.intelcomp.stiviewer.query.IndicatorAccessQuery;
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
				DataTreeConfigEntity dataTreeConfig = this.jsonHandlingService.fromJsonSafe(DataTreeConfigEntity.class, userSettingsEntity.getValue());
				if (browseDataTreeConfigEntities != null) browseDataTreeConfigEntities.add(dataTreeConfig);
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
			if (lookup.getSelectedLevels() == null || !lookup.getSelectedLevels().stream().anyMatch(x -> x.getCode().equals(dataTreeLevelConfigEntity.getField().getCode()))) {
				nextLevelConfig = dataTreeLevelConfigEntity;
				break;
			}
		}
		if (nextLevelConfig == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{lookup.getConfigId(), DataTreeLevelConfigEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		DataTreeLevelEntity browseDataTreeLevelType = new DataTreeLevelEntity();
		browseDataTreeLevelType.setField(nextLevelConfig.getField());
		browseDataTreeLevelType.setOrder(nextLevelConfig.getOrder());
		browseDataTreeLevelType.setSupportedDashboards(nextLevelConfig.getSupportedDashboards());
		browseDataTreeLevelType.setSupportSubLevel(nextLevelConfig.getSupportSubLevel());

		List<IndicatorAccessEntity> indicatorAccesses = this.queryFactory.query(IndicatorAccessQuery.class).isActive(IsActive.ACTIVE).collectAs(new BaseFieldSet().ensure(this.conventionService.asIndexer(IndicatorAccess._indicator, Indicator._id), IndicatorAccess._id));
		List<UUID> indicatorIds = indicatorAccesses.stream().map(x -> x.getIndicatorId()).distinct().collect(Collectors.toList());
		List<DataTreeLevelItemEntity> indicatorReportConfigs = new ArrayList<>();

		for (UUID indicatorId : indicatorIds) {
			List<DataTreeLevelItemEntity> itemEntities = this.collectBrowseDataTreeLevelItems(lookup, indicatorId, nextLevelConfig);
			for (DataTreeLevelItemEntity itemEntity : itemEntities) {
				if (indicatorReportConfigs.stream().filter(x -> Objects.equals(x.getValue(), itemEntity.getValue())).count() == 0) indicatorReportConfigs.add(itemEntity);
			}
		}
		browseDataTreeLevelType.setItems(indicatorReportConfigs);

		return this.builderFactory.builder(BrowseDataTreeLevelBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fields, browseDataTreeLevelType);
	}

	private List<DataTreeLevelItemEntity> collectBrowseDataTreeLevelItems(IndicatorReportLevelLookup lookup, UUID indicatorId, DataTreeLevelConfigEntity nextLevelConfig) {
		List<DataTreeLevelItemEntity> indicatorReportConfigs = new ArrayList<>();

		IndicatorConfigItem indicatorConfigItem = this.indicatorConfigService.getConfig(indicatorId);
		if (indicatorConfigItem == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{indicatorId, IndicatorConfigItem.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		FieldEntity nextLevelField = indicatorConfigItem.getExtraProps().get(nextLevelConfig.getField().getCode());
		if (nextLevelField == null) return indicatorReportConfigs;

		List<IndicatorPointKeywordFilter> pointKeywordFilterList = new ArrayList<>();
		if (lookup.getSelectedLevels() != null) {
			for (IndicatorReportLevelLookup.SelectedLevel selectedLevel : lookup.getSelectedLevels()) {
				FieldEntity filterField = indicatorConfigItem.getExtraProps().get(selectedLevel.getCode());
				if (filterField == null) return indicatorReportConfigs;

				String filterFieldKey = filterField.getCode();

				IndicatorPointKeywordFilter pointKeywordFilter = new IndicatorPointKeywordFilter();
				pointKeywordFilter.setField(filterFieldKey);
				pointKeywordFilter.setValues(List.of(selectedLevel.getValue()));
				pointKeywordFilterList.add(pointKeywordFilter);
			}
		}

		DistinctValuesResponse<String> items = null;
		Map<String, Object> afterKey = null;
		do {
			String distinctKey = nextLevelConfig.getField().getCode();
			if (nextLevelField.getBaseType() == IndicatorFieldBaseType.DoubleMap || nextLevelField.getBaseType() == IndicatorFieldBaseType.IntegerMap) {
				distinctKey = nextLevelConfig.getField().getCode() + ".key";
			}

			items = this.queryFactory.query(IndicatorPointQuery.class).indicatorIds(indicatorId).keywordFilters(pointKeywordFilterList).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicatorOrIndicatorAccess).useThisColumnForAccess(distinctKey).collectDistinct(distinctKey, SortOrder.ASC, (x) -> x, afterKey);

			for (String item : items.getItems()) {
				DataTreeLevelItemEntity levelConfigItem = new DataTreeLevelItemEntity();
				levelConfigItem.setValue(item);
				indicatorReportConfigs.add(levelConfigItem);
			}
			afterKey = items.getAfterKey();
		} while (items != null && !items.getItems().isEmpty() && afterKey != null && indicatorReportConfigs.size() < items.getTotal());
		return indicatorReportConfigs;
	}
}
