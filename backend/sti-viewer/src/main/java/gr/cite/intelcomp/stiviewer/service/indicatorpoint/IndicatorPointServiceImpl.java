package gr.cite.intelcomp.stiviewer.service.indicatorpoint;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationContentResolver;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.enums.IndicatorFieldBaseType;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.IndicatorEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorDoubleMapEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorIntegerMapEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.FieldEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.DataGroupInfoColumnEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.DataGroupInfoEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.IndicatorPointEntity;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpoint.IndicatorPointQuery;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.builder.elasticreport.AggregateResponseModelBuilder;
import gr.cite.intelcomp.stiviewer.model.builder.indicatorpoint.IndicatorPointBuilder;
import gr.cite.intelcomp.stiviewer.model.elasticreport.AggregateResponseModel;
import gr.cite.intelcomp.stiviewer.model.elasticreport.Bucket;
import gr.cite.intelcomp.stiviewer.model.elasticreport.IndicatorPointReportLookup;
import gr.cite.intelcomp.stiviewer.model.indicatorpoint.IndicatorPoint;
import gr.cite.intelcomp.stiviewer.model.persist.indicatorpoint.DataGroupInfoColumnPersist;
import gr.cite.intelcomp.stiviewer.model.persist.indicatorpoint.IndicatorPointPersist;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorConfigItem;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorConfigService;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorService;
import gr.cite.intelcomp.stiviewer.service.indicatorelastic.ElasticIndicatorService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.deleter.DeleterFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.query.Aggregation.*;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;

@Service
@RequestScope
public class IndicatorPointServiceImpl implements IndicatorPointService {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(IndicatorPointServiceImpl.class));
	private final TenantEntityManager entityManager;
	private final AuthorizationService authorizationService;
	private final DeleterFactory deleterFactory;
	private final BuilderFactory builderFactory;
	private final ConventionService conventionService;
	private final ErrorThesaurusProperties errors;
	private final MessageSource messageSource;
	private final ElasticsearchRestTemplate elasticsearchTemplate;
	private final IndicatorService indicatorService;
	private final IndicatorConfigService indicatorConfigService;
	private final AuthorizationContentResolver authorizationContentResolver;
	private final ElasticIndicatorService elasticIndicatorService;
	private final QueryFactory queryFactory;
	private final IndicatorPointValidationService validationService;


	@Autowired
	public IndicatorPointServiceImpl(
			TenantEntityManager entityManager,
			AuthorizationService authorizationService,
			DeleterFactory deleterFactory,
			BuilderFactory builderFactory,
			ConventionService conventionService,
			ErrorThesaurusProperties errors,
			MessageSource messageSource,
			ElasticsearchRestTemplate elasticsearchTemplate,
			IndicatorService indicatorService,
			IndicatorConfigService indicatorConfigService,
			AuthorizationContentResolver authorizationContentResolver,
			ElasticIndicatorService elasticIndicatorService,
			QueryFactory queryFactory,
			IndicatorPointValidationServiceImpl validationService) {
		this.entityManager = entityManager;
		this.authorizationService = authorizationService;
		this.deleterFactory = deleterFactory;
		this.builderFactory = builderFactory;
		this.conventionService = conventionService;
		this.errors = errors;
		this.messageSource = messageSource;
		this.elasticsearchTemplate = elasticsearchTemplate;
		this.indicatorService = indicatorService;
		this.indicatorConfigService = indicatorConfigService;
		this.authorizationContentResolver = authorizationContentResolver;
		this.elasticIndicatorService = elasticIndicatorService;
		this.queryFactory = queryFactory;
		this.validationService = validationService;
	}

	public IndicatorPoint persist(UUID indicatorId, IndicatorPointPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting dataset").And("model", model).And("fields", fields));

		this.authorizationService.authorizeAtLeastOneForce(List.of(this.authorizationContentResolver.indicatorAffiliation(indicatorId)), Permission.EditIndicatorPoint);
		IndicatorConfigItem indicatorConfigItem = this.getIndicatorConfigItem(indicatorId);

		IndicatorPointEntity data = this.build(indicatorId, indicatorConfigItem, model, Date.from(Instant.now()));

		data = elasticsearchTemplate.save(data, IndexCoordinates.of(this.elasticIndicatorService.getIndexName(indicatorId)));
		IndicatorPoint persisted = this.builderFactory.builder(IndicatorPointBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, IndicatorPoint._id), data);
		return persisted;
	}

	public void persist(UUID indicatorId, List<IndicatorPointPersist> models) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting dataset").And("models", models));
		this.authorizationService.authorizeAtLeastOneForce(List.of(this.authorizationContentResolver.indicatorAffiliation(indicatorId)), Permission.EditIndicatorPoint);

		IndicatorConfigItem indicatorConfigItem = this.getIndicatorConfigItem(indicatorId);
		List<IndicatorPointEntity> items = new ArrayList<>();
		for (IndicatorPointPersist model : models) {
			IndicatorPointEntity data = this.build(indicatorId, indicatorConfigItem, model, Date.from(Instant.now()));
			items.add(data);
		}
		this.saveToElasticInBatches(items, IndexCoordinates.of(this.elasticIndicatorService.getIndexName(indicatorId)));
	}

	private void saveToElasticInBatches(List<IndicatorPointEntity> items, IndexCoordinates indexCoordinates) {
		int batchingFactor = 100; //TODO config
		int batch = (int) (items.size() / batchingFactor);
		int remaining = items.size() - (batch * batchingFactor);
		for (int i = 0; i < batch; i += 1) {
			int from = i * batchingFactor;
			elasticsearchTemplate.save(items.subList(from, from + batchingFactor), indexCoordinates);
		}
		if (remaining > 0) {
			int from = items.size() - remaining;
			elasticsearchTemplate.save(items.subList(from, items.size()), indexCoordinates);
		}
	}

	private IndicatorConfigItem getIndicatorConfigItem(UUID indicatorId) throws InvalidApplicationException {
		IndicatorEntity indicatorEntity = this.entityManager.find(IndicatorEntity.class, indicatorId);
		if (indicatorEntity == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{indicatorId, Indicator.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		IndicatorConfigItem indicatorConfigItem = this.indicatorConfigService.getConfig(indicatorEntity.getId());
		if (indicatorConfigItem == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{indicatorId, IndicatorConfigItem.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		return indicatorConfigItem;
	}

	private IndicatorPointEntity build(UUID indicatorId, IndicatorConfigItem indicatorConfigItem, IndicatorPointPersist model, Date now) {
		IndicatorPointEntity data = new IndicatorPointEntity();
		data.setId(UUID.randomUUID());
		data.setIndicatorId(indicatorId);
		data.setBatchId(model.getBatchId() == null ? UUID.randomUUID().toString() : model.getBatchId());
		data.setGroupHash(model.getGroupHash());
		data.setBatchTimestamp(model.getBatchTimestamp() == null ? now : model.getBatchTimestamp());
		data.setTimestamp(model.getTimestamp() == null ? now : model.getTimestamp());

		if (model.getGroupInfo() != null && model.getGroupInfo().getColumns() != null && !model.getGroupInfo().getColumns().isEmpty()) {
			DataGroupInfoEntity groupInfo = new DataGroupInfoEntity();
			groupInfo.setColumns(new ArrayList<>());
			for (DataGroupInfoColumnPersist columnPersist : model.getGroupInfo().getColumns()) {
				DataGroupInfoColumnEntity dataGroupInfoColumnEntity = new DataGroupInfoColumnEntity();
				dataGroupInfoColumnEntity.setFieldCode(columnPersist.getFieldCode());
				dataGroupInfoColumnEntity.setValues(columnPersist.getValues());
				groupInfo.getColumns().add(dataGroupInfoColumnEntity);
			}
			data.setGroupInfo(groupInfo);
		}

		if (model.getProperties() != null && indicatorConfigItem.getExtraProps() != null) {
			this.validationService.checkIfMissingRequiredFields(model.getProperties(), indicatorConfigItem.getExtraProps());
			Map<String, Object> properties = new HashMap<>();
			List<Map.Entry<String, List<String>>> validationErrors = new ArrayList<>();
			for (Map.Entry<String, Object> prop : model.getProperties().entrySet()) {
				if (indicatorConfigItem.getExtraProps().containsKey(prop.getKey())) {
					FieldEntity fieldEntity = indicatorConfigItem.getExtraProps().get(prop.getKey());
					if (fieldEntity.getValidation() != null) this.validationService.validateField(fieldEntity.getValidation(), prop, fieldEntity.getBaseType());
					switch (fieldEntity.getBaseType()) {
						case String:
						case Keyword:
							if (prop.getValue() == null) {
								properties.put(prop.getKey(), (Map<String, Double>) null);
							} else {
								properties.put(prop.getKey(), String.class.cast(prop.getValue()));
							}
							break;
						case Date:
							if (prop.getValue() == null) {
								properties.put(prop.getKey(), (Date) null);
							} else {
								ZonedDateTime zonedDateTime = ZonedDateTime.parse(String.class.cast(prop.getValue()));
								properties.put(prop.getKey(), new Date(zonedDateTime.toInstant().toEpochMilli()));
							}
							break;
						case Double:
							if (prop.getValue() == null) {
								properties.put(prop.getKey(), (Double) null);
							} else {
								if (prop.getValue().getClass().equals(Integer.class)) properties.put(prop.getKey(), Double.valueOf(Integer.class.cast(prop.getValue())));
								else properties.put(prop.getKey(), Double.class.cast(prop.getValue()));
							}
							break;
						case Integer:
							if (prop.getValue() == null) {
								properties.put(prop.getKey(), (Integer) null);
							} else {
								properties.put(prop.getKey(), Integer.class.cast(prop.getValue()));
							}
							break;
						case IntegerArray:
							if (prop.getValue() == null) {
								properties.put(prop.getKey(), (List<Integer>) null);
							} else {
								properties.put(prop.getKey(), (List<Integer>) prop.getValue());
							}
							break;
						case DoubleArray:
							if (prop.getValue() == null) {
								properties.put(prop.getKey(), (List<Double>) null);
							} else {
								properties.put(prop.getKey(), (List<Double>) prop.getValue());
							}
							break;
						case KeywordArray:
							if (prop.getValue() == null) {
								properties.put(prop.getKey(), (List<String>) null);
							} else {
								properties.put(prop.getKey(), (List<String>) prop.getValue());
							}
							break;
						case DoubleMap: {
							if (prop.getValue() == null) {
								properties.put(prop.getKey(), (IndicatorDoubleMapEntity) null);
							} else {
								List<IndicatorDoubleMapEntity> values = new ArrayList<>();
								for (Map.Entry<String, Double> textItem : ((HashMap<String, Double>) prop.getValue()).entrySet()) {
									IndicatorDoubleMapEntity val = new IndicatorDoubleMapEntity();
									val.setKey(textItem.getKey());
									val.setVal(textItem.getValue());
									values.add(val);
								}
								properties.put(prop.getKey(), values);
							}
							break;
						}
						case IntegerMap: {
							if (prop.getValue() == null) {
								properties.put(prop.getKey(), (IndicatorIntegerMapEntity) null);
							} else {
								List<IndicatorIntegerMapEntity> values = new ArrayList<>();
								for (Map.Entry<String, Integer> textItem : ((HashMap<String, Integer>) prop.getValue()).entrySet()) {
									IndicatorIntegerMapEntity val = new IndicatorIntegerMapEntity();
									val.setKey(textItem.getKey());
									val.setVal(textItem.getValue());
									values.add(val);
								}
								properties.put(prop.getKey(), values);
							}
							break;
						}
						default:
							throw new MyApplicationException("invalid type " + fieldEntity.getBaseType());
					}
				}
			}
			this.validationService.validateModel();
			if (properties.size() > 0) data.setProperties(properties);
		}
		return data;
	}

	public AggregateResponseModel report(@NotNull UUID indicatorId, @NotNull IndicatorPointReportLookup lookup, FieldSet fields) throws InvalidApplicationException {
		logger.debug(new MapLogEntry("report" + IndicatorPoint.class.getSimpleName()).And("lookup", lookup).And("fieldSet", fields));

		AggregateResponse aggregateResponse = this.report(indicatorId, lookup);

		AggregateResponseModel model = this.builderFactory.builder(AggregateResponseModelBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicatorOrIndicatorAccess).build(BaseFieldSet.build(fields, IndicatorPoint._id), aggregateResponse);
		return model;
	}

	private AggregateResponse report(UUID indicatorId, IndicatorPointReportLookup lookup) throws InvalidApplicationException {
		this.authorizationService.authorizeAtLeastOneForce(List.of(this.authorizationContentResolver.indicatorAffiliation(indicatorId)), Permission.BrowseIndicatorPoint);

		IndicatorEntity indicatorEntity = this.entityManager.find(IndicatorEntity.class, indicatorId);
		if (indicatorEntity == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{indicatorId, Indicator.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		IndicatorConfigItem indicatorConfigItem = this.getIndicatorConfigItem(indicatorId);
		if (indicatorConfigItem == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{indicatorId, IndicatorConfigItem.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		AggregationQuery aggregationQuery = new AggregationQuery();
		aggregationQuery.setMetrics(this.buildMetricsForQuery(f -> this.getIndicatorFieldBaseType(indicatorConfigItem, f), lookup.getMetrics()));
		aggregationQuery.setBucketAggregate(this.buildBucketAggregate(f -> this.getIndicatorFieldBaseType(indicatorConfigItem, f), lookup.getBucket()));

		IndicatorPointQuery indicatorPointQuery = lookup.getFilters() == null ? this.queryFactory.query(IndicatorPointQuery.class) : lookup.getFilters().enrich(this.queryFactory);
		indicatorPointQuery = indicatorPointQuery.indicatorIds(indicatorId).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicatorOrIndicatorAccess);

		AggregateResponse aggregateResponse = indicatorPointQuery.collectAggregate(aggregationQuery);

		return aggregateResponse;
	}

	public byte[] export(UUID indicatorId, IndicatorPointReportLookup lookup) throws InvalidApplicationException, IOException {
		logger.debug(new MapLogEntry("export" + IndicatorPoint.class.getSimpleName()).And("lookup", lookup));

		IndicatorConfigItem indicatorConfigItem = this.getIndicatorConfigItem(indicatorId);
		if (indicatorConfigItem == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{indicatorId, IndicatorConfigItem.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		AggregateResponse aggregateResponse = this.report(indicatorId, lookup);

		Workbook workbook = new XSSFWorkbook();

		int sheetIndex = 1;
		gr.cite.intelcomp.stiviewer.model.elasticreport.Bucket bucket = lookup.getBucket();
		if (lookup.getBucket() != null) {
			if (lookup.getBucket().getType() == BucketAggregateType.Nested) {
				gr.cite.intelcomp.stiviewer.model.elasticreport.Nested nested = (gr.cite.intelcomp.stiviewer.model.elasticreport.Nested) lookup.getBucket();
				if (this.addBucketSheet(workbook, indicatorConfigItem, nested.getBucket(), aggregateResponse, sheetIndex)) sheetIndex++;
				if (this.addMetricSheet(workbook, indicatorConfigItem, nested.getMetrics(), aggregateResponse, sheetIndex)) sheetIndex++;
			} else {
				if (this.addBucketSheet(workbook, indicatorConfigItem, lookup.getBucket(), aggregateResponse, sheetIndex)) sheetIndex++;
			}
		}

		if (this.addMetricSheet(workbook, indicatorConfigItem, lookup.getMetrics(), aggregateResponse, sheetIndex)) sheetIndex++;

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		workbook.write(stream);

		return stream.toByteArray();
	}

	private boolean addMetricSheet(Workbook workbook, IndicatorConfigItem indicatorConfigItem, List<gr.cite.intelcomp.stiviewer.model.elasticreport.Metric> metrics, AggregateResponse aggregateResponse, int sheetIndex) {
		if (metrics == null) return false;

		Map<String, Integer> columnMapping = new Hashtable<>();

		Sheet sheet = workbook.createSheet("Sheet " + sheetIndex);
		int rowIndex = 0;
		Row header = sheet.createRow(rowIndex);
		rowIndex++;

		this.addMetricsToHeader(metrics, indicatorConfigItem, header, 0, columnMapping);

		for (AggregateResponseItem aggregateResponseItem : aggregateResponse.getItems()) {
			if (this.addDataRow(columnMapping, aggregateResponseItem, sheet, rowIndex)) {
				rowIndex++;
			}
		}
		for (int i = 0; i < columnMapping.size(); i++) {
			sheet.autoSizeColumn(i);
		}
		return true;
	}

	private boolean addBucketSheet(Workbook workbook, IndicatorConfigItem indicatorConfigItem, gr.cite.intelcomp.stiviewer.model.elasticreport.Bucket bucket, AggregateResponse aggregateResponse, int sheetIndex) {
		if (bucket == null) return false;

		Map<String, Integer> columnMapping = new Hashtable<>();

		Sheet sheet = workbook.createSheet("Sheet " + sheetIndex);
		int rowIndex = 0;
		Row header = sheet.createRow(rowIndex);
		rowIndex++;

		switch (bucket.getType()) {
			case DateHistogram:
			case Terms: {
				FieldEntity fieldEntity = indicatorConfigItem.getExtraProps().getOrDefault(bucket.getField(), null);
				if (fieldEntity == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{bucket.getField(), FieldEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));

				this.addHeaderCell(fieldEntity.getName(), header, 0);
				columnMapping.put(bucket.getField(), 0);
				this.addMetricsToHeader(bucket.getMetrics(), indicatorConfigItem, header, 1, columnMapping);
				break;
			}
			case Composite: {
				gr.cite.intelcomp.stiviewer.model.elasticreport.Composite composite = (gr.cite.intelcomp.stiviewer.model.elasticreport.Composite) bucket;
				int i = 0;
				if (composite.getDateHistogramSource() != null) {
					FieldEntity fieldEntity = indicatorConfigItem.getExtraProps().getOrDefault(composite.getDateHistogramSource().getField(), null);
					if (fieldEntity == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{composite.getDateHistogramSource().getField(), FieldEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));

					this.addHeaderCell(fieldEntity.getName(), header, i);
					columnMapping.put(composite.getDateHistogramSource().getField(), i);
					i++;
				}

				if (composite.getSources() != null) {
					for (gr.cite.intelcomp.stiviewer.model.elasticreport.CompositeSource source : composite.getSources()) {
						FieldEntity fieldEntity = indicatorConfigItem.getExtraProps().getOrDefault(source.getField(), null);
						if (fieldEntity == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{source.getField(), FieldEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));

						this.addHeaderCell(fieldEntity.getName(), header, 0);
						columnMapping.put(source.getField(), i);
						i++;
					}
				}

				this.addMetricsToHeader(composite.getMetrics(), indicatorConfigItem, header, i, columnMapping);
				break;
			}
			default:
				throw new MyApplicationException("invalid type " + bucket.getType());
		}

		for (AggregateResponseItem aggregateResponseItem : aggregateResponse.getItems()) {
			if (this.addDataRow(columnMapping, aggregateResponseItem, sheet, rowIndex)) {
				rowIndex++;
			}
		}

		for (int i = 0; i < columnMapping.size(); i++) {
			sheet.autoSizeColumn(i);
		}
		return true;
	}

	private boolean addDataRow(Map<String, Integer> columnMapping, AggregateResponseItem item, Sheet sheet, int rowIndex) {
		int itemColumnSize = (item.getGroup() != null && item.getGroup().getItems() != null ? item.getGroup().getItems().size() : 0)
				+ (item.getValues() != null ? item.getValues().size() : 0);
		if (itemColumnSize != columnMapping.size()) return false;
		Map<Integer, String> valuesStringMap = new HashMap<>();
		Map<Integer, Double> valuesDoubleMap = new HashMap<>();

		for (Map.Entry<String, Integer> columnMappingEntry : columnMapping.entrySet()) {
			if (item.getGroup() != null && item.getGroup().getItems() != null && item.getGroup().getItems().containsKey(columnMappingEntry.getKey())) {
				valuesStringMap.put(columnMappingEntry.getValue(), item.getGroup().getItems().get(columnMappingEntry.getKey()));
			} else if (item.getValues() != null) {
				boolean found = false;
				for (AggregateResponseValue aggregateResponseValue : item.getValues()) {
					if (this.generateMetricFieldKey(aggregateResponseValue.getField(), aggregateResponseValue.getAggregateType()).equals(columnMappingEntry.getKey())) {
						valuesDoubleMap.put(columnMappingEntry.getValue(), aggregateResponseValue.getValue());
						found = true;
						break;
					}
				}
				if (!found) return false;
			} else {
				return false;
			}
		}

		Row row = sheet.createRow(rowIndex);
		for (Map.Entry<Integer, String> value : valuesStringMap.entrySet()) {
			Cell headerCell = row.createCell(value.getKey());
			headerCell.setCellValue(value.getValue());
		}
		for (Map.Entry<Integer, Double> value : valuesDoubleMap.entrySet()) {
			Cell headerCell = row.createCell(value.getKey());
			headerCell.setCellValue(value.getValue());
		}
		return true;
	}

	private void addMetricsToHeader(List<gr.cite.intelcomp.stiviewer.model.elasticreport.Metric> metrics, IndicatorConfigItem indicatorConfigItem, Row header, int startColumn, Map<String, Integer> columnMapping) {
		if (metrics != null) {
			int i = startColumn;
			for (gr.cite.intelcomp.stiviewer.model.elasticreport.Metric metric : metrics) {
				FieldEntity fieldEntity = indicatorConfigItem.getExtraProps().getOrDefault(metric.getField(), null);
				if (fieldEntity == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{metric.getField(), FieldEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
				this.addHeaderCell(fieldEntity.getName() + " (" + metric.getType() + ")", header, i);
				columnMapping.put(this.generateMetricFieldKey(metric.getField(), metric.getType()), i);
				i++;
			}
		}
	}

	private String generateMetricFieldKey(String field, MetricAggregateType metricAggregateType) {
		return field + "_" + metricAggregateType;
	}

	private void addHeaderCell(String value, Row header, int column) {
		CellStyle headerStyle = header.getSheet().getWorkbook().createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setBorderBottom(BorderStyle.MEDIUM);

		Cell headerCell = header.createCell(column);
		headerCell.setCellValue(value);
		headerCell.setCellStyle(headerStyle);
	}

	private BucketAggregate buildBucketAggregate(Function<String, IndicatorFieldBaseType> indicatorFieldBaseTypeFunction, Bucket bucket) {
		if (bucket == null) return null;
		switch (bucket.getType()) {
			case DateHistogram:
				return this.buildDateHistogramBucketAggregate(indicatorFieldBaseTypeFunction, (gr.cite.intelcomp.stiviewer.model.elasticreport.DateHistogram) bucket);
			case Terms:
				return this.buildTermsBucketAggregate(indicatorFieldBaseTypeFunction, (gr.cite.intelcomp.stiviewer.model.elasticreport.Terms) bucket);
			case Nested:
				return this.buildNestedBucketAggregate(indicatorFieldBaseTypeFunction, (gr.cite.intelcomp.stiviewer.model.elasticreport.Nested) bucket);
			case Composite:
				return this.buildCompositeBucketAggregate(indicatorFieldBaseTypeFunction, (gr.cite.intelcomp.stiviewer.model.elasticreport.Composite) bucket);
			default:
				throw new MyApplicationException("invalid type " + bucket.getType());
		}
	}

	private AggregationMetricHaving buildHaving(gr.cite.intelcomp.stiviewer.model.elasticreport.AggregationMetricHaving having) {
		if (having == null) return null;
		return new AggregationMetricHaving(having.getField(), having.getMetricAggregateType(), having.getType(), having.getOperator(), having.getValue());
	}

	private BucketAggregate buildTermsBucketAggregate(Function<String, IndicatorFieldBaseType> indicatorFieldBaseTypeFunction, gr.cite.intelcomp.stiviewer.model.elasticreport.Terms bucket) {
		if (bucket == null) return null;
		switch (indicatorFieldBaseTypeFunction.apply(bucket.getField())) {
			case Integer:
			case Double:
			case Date:
			case Keyword:
				Terms terms = new Terms(bucket.getField());
				terms.setMetrics(this.buildMetricsForQuery(indicatorFieldBaseTypeFunction, bucket.getMetrics()));
				terms.setBucketAggregate(this.buildBucketAggregate(indicatorFieldBaseTypeFunction, bucket.getBucket()));
				terms.setHaving(this.buildHaving(bucket.getHaving()));
				if (bucket.getOrder() != null) terms.setOrder(bucket.getOrder());
				return terms;
			default:
				throw new MyApplicationException("invalid bucket field " + bucket.getField());
		}
	}


	private BucketAggregate buildNestedBucketAggregate(Function<String, IndicatorFieldBaseType> indicatorFieldBaseTypeFunction, gr.cite.intelcomp.stiviewer.model.elasticreport.Nested bucket) {
		if (bucket == null) return null;
		switch (indicatorFieldBaseTypeFunction.apply(bucket.getField())) {
			case DoubleMap: {
				Nested nested = new Nested(bucket.getField());
				nested.setMetrics(this.buildMetricsForQuery(f -> this.getDoubleMapFieldBaseType(f), bucket.getMetrics()));
				nested.setBucketAggregate(this.buildBucketAggregate(f -> this.getDoubleMapFieldBaseType(f), bucket.getBucket()));
				nested.setHaving(this.buildHaving(bucket.getHaving()));
				return nested;
			}
			case IntegerMap: {
				Nested nested = new Nested(bucket.getField());
				nested.setMetrics(this.buildMetricsForQuery(f -> this.getIntegerMapFieldBaseType(f), bucket.getMetrics()));
				nested.setBucketAggregate(this.buildBucketAggregate(f -> this.getIntegerMapFieldBaseType(f), bucket.getBucket()));
				nested.setHaving(this.buildHaving(bucket.getHaving()));
				return nested;
			}
			default:
				throw new MyApplicationException("invalid bucket field " + bucket.getField());
		}
	}

	private BucketAggregate buildCompositeBucketAggregate(Function<String, IndicatorFieldBaseType> indicatorFieldBaseTypeFunction, gr.cite.intelcomp.stiviewer.model.elasticreport.Composite bucket) {
		if (bucket == null) return null;
		List<CompositeSource> sources = new ArrayList<>();
		if (bucket.getSources() != null) {
			for (gr.cite.intelcomp.stiviewer.model.elasticreport.CompositeSource sourceModel : bucket.getSources()) {
				switch (indicatorFieldBaseTypeFunction.apply(sourceModel.getField())) {
					case Integer:
					case Double:
					case Date:
					case Keyword:
						CompositeSource source = new CompositeSource(sourceModel.getField());
						if (sourceModel.getOrder() != null) source.setOrder(sourceModel.getOrder());
						sources.add(source);
						break;
					default:
						throw new MyApplicationException("invalid bucket field " + sourceModel.getField());
				}
			}
		}
		DateHistogram dateHistogram = null;
		if (bucket.getDateHistogramSource() != null) {
			switch (indicatorFieldBaseTypeFunction.apply(bucket.getDateHistogramSource().getField())) {
				case Date:
					dateHistogram = new DateHistogram(bucket.getDateHistogramSource().getField(), bucket.getDateHistogramSource().getInterval());
					dateHistogram.setMetrics(this.buildMetricsForQuery(indicatorFieldBaseTypeFunction, bucket.getMetrics()));
					dateHistogram.setBucketAggregate(this.buildBucketAggregate(indicatorFieldBaseTypeFunction, bucket.getBucket()));
					dateHistogram.setHaving(this.buildHaving(bucket.getHaving()));
					if (bucket.getDateHistogramSource().getOrder() != null) dateHistogram.setOrder(bucket.getDateHistogramSource().getOrder());
					break;
				default:
					throw new MyApplicationException("invalid date bucket field " + bucket.getDateHistogramSource().getField());
			}
		}

		Composite composite = new Composite(sources, bucket.getAfterKey());
		composite.setMetrics(this.buildMetricsForQuery(indicatorFieldBaseTypeFunction, bucket.getMetrics()));
		composite.setBucketAggregate(this.buildBucketAggregate(indicatorFieldBaseTypeFunction, bucket.getBucket()));
		composite.setHaving(this.buildHaving(bucket.getHaving()));
		composite.setDateHistogramSource(dateHistogram);
		return composite;
	}

	private BucketAggregate buildDateHistogramBucketAggregate(Function<String, IndicatorFieldBaseType> indicatorFieldBaseTypeFunction, gr.cite.intelcomp.stiviewer.model.elasticreport.DateHistogram bucket) {
		if (bucket == null) return null;
		switch (indicatorFieldBaseTypeFunction.apply(bucket.getField())) {
			case Date:
				DateHistogram dateHistogram = new DateHistogram(bucket.getField(), bucket.getInterval());
				dateHistogram.setMetrics(this.buildMetricsForQuery(indicatorFieldBaseTypeFunction, bucket.getMetrics()));
				dateHistogram.setBucketAggregate(this.buildBucketAggregate(indicatorFieldBaseTypeFunction, bucket.getBucket()));
				dateHistogram.setHaving(this.buildHaving(bucket.getHaving()));
				if (bucket.getOrder() != null) dateHistogram.setOrder(bucket.getOrder());
				return dateHistogram;
			default:
				throw new MyApplicationException("invalid date bucket field " + bucket.getField());
		}
	}

	private List<Metric> buildMetricsForQuery(Function<String, IndicatorFieldBaseType> indicatorFieldBaseTypeFunction, List<gr.cite.intelcomp.stiviewer.model.elasticreport.Metric> metricModels) {
		if (metricModels == null) return null;

		List<Metric> metrics = new ArrayList<>();
		for (gr.cite.intelcomp.stiviewer.model.elasticreport.Metric metricModel : metricModels) {
			switch (indicatorFieldBaseTypeFunction.apply(metricModel.getField())) {
				case Integer:
				case Double:
					metrics.add(new Metric(metricModel.getField(), metricModel.getType()));
					break;
				default:
					throw new MyApplicationException("invalid metric field " + metricModel.getField());
			}
		}
		return metrics;
	}

	private IndicatorFieldBaseType getIndicatorFieldBaseType(IndicatorConfigItem indicatorConfigItem, String fieldName) {
		if (IndicatorPoint._id.equals(fieldName)) return IndicatorFieldBaseType.Keyword;
		else if (IndicatorPoint._batchId.equals(fieldName)) return IndicatorFieldBaseType.Keyword;
		else if (IndicatorPoint._batchTimestamp.equals(fieldName)) return IndicatorFieldBaseType.Date;
		else if (IndicatorPoint._timestamp.equals(fieldName)) return IndicatorFieldBaseType.Date;
		else {
			FieldEntity fieldEntity = indicatorConfigItem.getExtraProps().getOrDefault(fieldName, null);
			if (fieldEntity == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{fieldName, FieldEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
			return fieldEntity.getBaseType();
		}
	}

	private IndicatorFieldBaseType getDoubleMapFieldBaseType(String fieldName) {
		if (IndicatorDoubleMapEntity.Fields.val.equals(fieldName)) return IndicatorFieldBaseType.Double;
		else if (IndicatorDoubleMapEntity.Fields.key.equals(fieldName)) return IndicatorFieldBaseType.Keyword;
		else {
			throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{fieldName, IndicatorDoubleMapEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		}
	}

	private IndicatorFieldBaseType getIntegerMapFieldBaseType(String fieldName) {
		if (IndicatorIntegerMapEntity.Fields.val.equals(fieldName)) return IndicatorFieldBaseType.Integer;
		else if (IndicatorIntegerMapEntity.Fields.key.equals(fieldName)) return IndicatorFieldBaseType.Keyword;
		else {
			throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{fieldName, IndicatorIntegerMapEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		}
	}
}
