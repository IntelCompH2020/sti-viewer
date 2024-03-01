package gr.cite.intelcomp.stiviewer.model.builder.elasticreport;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.enums.IndicatorFieldBaseType;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.IndicatorEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.FieldEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.IndicatorPointEntity;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpoint.IndicatorPointQuery;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter.IndicatorPointKeywordFilter;
import gr.cite.intelcomp.stiviewer.elastic.query.lookup.IndicatorPointLookup;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.elasticreport.AggregateResponseModel;
import gr.cite.intelcomp.stiviewer.model.elasticreport.Bucket;
import gr.cite.intelcomp.stiviewer.model.elasticreport.IndicatorPointReportLookup;
import gr.cite.intelcomp.stiviewer.model.indicatorpoint.IndicatorPoint;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorConfigItem;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorConfigService;
import gr.cite.intelcomp.stiviewer.service.indicatorpoint.IndicatorPointServiceImpl;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.query.Aggregation.*;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.management.InvalidApplicationException;
import java.util.*;
import java.util.function.Function;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AggregateResponseModelBuilder extends BaseBuilder<AggregateResponseModel, AggregateResponse> {

	private final BuilderFactory builderFactory;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);
	private final TenantEntityManager entityManager;
	private final QueryFactory queryFactory;
	private final MessageSource messageSource;
	private final IndicatorConfigService indicatorConfigService;
	private final IndicatorPointServiceImpl indicatorPointService;
	private UUID indicatorId;
	private IndicatorPointReportLookup lookup;

	public AggregateResponseModelBuilder(
			ConventionService conventionService,
			BuilderFactory builderFactory,
			QueryFactory queryFactory,
			MessageSource messageSource,
			TenantEntityManager entityManager,
			IndicatorConfigService indicatorConfigService,
			IndicatorPointServiceImpl indicatorPointService
	) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(AggregateResponseModelBuilder.class)));
		this.builderFactory = builderFactory;
		this.queryFactory = queryFactory;
		this.entityManager = entityManager;
		this.messageSource = messageSource;
		this.indicatorConfigService = indicatorConfigService;
		this.indicatorPointService = indicatorPointService;
	}

	public AggregateResponseModelBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}
	public AggregateResponseModelBuilder indicatorId(UUID indicatorId) {
		this.indicatorId = indicatorId;
		return this;
	}
	public AggregateResponseModelBuilder fields(IndicatorPointReportLookup lookup) {
		this.lookup = lookup;
		return this;
	}
	@Override
	public List<AggregateResponseModel> build(FieldSet fields, List<AggregateResponse> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));

		List<AggregateResponseModel> models = new ArrayList<>();
		Map<String, Map<String, String>> groupItemLabels = getItemLabels(data);
		for (AggregateResponse d : data) {
			AggregateResponseModel m = new AggregateResponseModel();
			m.setItems(this.builderFactory.builder(AggregateResponseItemModelBuilder.class).groupItemLabels(groupItemLabels).authorize(this.authorize).build(null, d.getItems()));
			m.setAfterKey(d.getAfterKey());
			m.setTotal(d.getTotal());
			models.add(m);
		}
		return models;
	}

	private Map<String, Map<String, String>> getItemLabels(List<AggregateResponse> data) {
		Map<String, Map<String, String>> itemLabels = new HashMap<>();
		if (this.indicatorId == null || this.lookup == null) return  itemLabels;
		IndicatorConfigItem indicatorConfigItem = null;
		try {
			indicatorConfigItem = this.getIndicatorConfigItem(indicatorId);
		} catch (InvalidApplicationException e) { throw new MyApplicationException(e.getMessage()); }

		Map<String, FieldEntity> propMap = indicatorConfigItem.getExtraProps();

		Map<String, List<String>> itemsToCalculateDisplayValue = new HashMap<>();
		Map<String, FieldEntity> displayFieldMappings = new HashMap<>();
		for (AggregateResponse d: data) {
			List<AggregateResponseItem> items = d.getItems();
			for (AggregateResponseItem m: items) {
				m.getGroup().getItems().forEach((key, value) -> {
					for (FieldEntity extraPropValue : propMap.values()) {
						if (extraPropValue.getValueField() != null && extraPropValue.getValueField().equalsIgnoreCase(key)) {
							if(!displayFieldMappings.containsKey(key)) displayFieldMappings.put(key, extraPropValue);
							if (itemsToCalculateDisplayValue.containsKey(key)){
								itemsToCalculateDisplayValue.get(key).add(value);
							} else {
								itemsToCalculateDisplayValue.put(key, new ArrayList<>(Arrays.asList(value)));
							}
						}
					}
				});
			}
			if (!itemsToCalculateDisplayValue.isEmpty()) {
				for (String key: itemsToCalculateDisplayValue.keySet()) {
					List<String> valuesToRemap = itemsToCalculateDisplayValue.get(key);
					if(!displayFieldMappings.containsKey(key)) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{key, FieldEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
					FieldEntity fieldEntity = displayFieldMappings.get(key);
					if (!valuesToRemap.isEmpty()){

						List<IndicatorPointKeywordFilter> keywordFilters = new ArrayList<>();
						keywordFilters.add(new IndicatorPointKeywordFilter(key, valuesToRemap));
						keywordFilters.addAll(lookup.getFilters().getKeywordFilters());

						IndicatorPointQuery indicatorPointQuery = lookup.getFilters() == null ? this.queryFactory.query(IndicatorPointQuery.class) : lookup.getFilters().enrich(this.queryFactory);
						indicatorPointQuery = indicatorPointQuery.indicatorIds(indicatorId).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicatorOrIndicatorAccess).keywordFilters(keywordFilters);//

						AggregationQuery aggregationQuery = new AggregationQuery();
						List<CompositeSource> sources = new ArrayList<>(Arrays.asList(new CompositeSource(key), new CompositeSource(fieldEntity.getCode())));

						Composite composite = new Composite(sources, null);
						aggregationQuery.setBucketAggregate(composite);

						AggregateResponse aggregateResponse = indicatorPointQuery.collectAggregate(aggregationQuery);

						Map<String, String> mapper = new HashMap<>();
						for (AggregateResponseItem item: aggregateResponse.getItems()) {
							if (item.getGroup().getItems().get(key) != null && item.getGroup().getItems().get(fieldEntity.getCode()) != null && !mapper.containsKey(item.getGroup().getItems().get(key))) {
								mapper.put(item.getGroup().getItems().get(key), item.getGroup().getItems().get(fieldEntity.getCode()));
							}
						}
						itemLabels.put(key, mapper);
					}
				}
			}
		}
		return itemLabels;
	}

	private IndicatorConfigItem getIndicatorConfigItem(UUID indicatorId) throws InvalidApplicationException {
		IndicatorEntity indicatorEntity = this.entityManager.find(IndicatorEntity.class, indicatorId);
		if (indicatorEntity == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{indicatorId, Indicator.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		IndicatorConfigItem indicatorConfigItem = this.indicatorConfigService.getConfig(indicatorEntity.getId());
		if (indicatorConfigItem == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{indicatorId, IndicatorConfigItem.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		return indicatorConfigItem;
	}
}
