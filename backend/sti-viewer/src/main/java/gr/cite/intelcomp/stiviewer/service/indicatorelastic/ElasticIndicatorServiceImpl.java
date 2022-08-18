package gr.cite.intelcomp.stiviewer.service.indicatorelastic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.IndicatorFieldBaseType;
import gr.cite.intelcomp.stiviewer.data.IndicatorEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorDoubleMapEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorElasticEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorIntegerMapEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.*;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.DataGroupInfoColumnEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.DataGroupInfoEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.IndicatorPointEntity;
import gr.cite.intelcomp.stiviewer.elastic.query.indicator.IndicatorElasticQuery;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.event.EventBroker;
import gr.cite.intelcomp.stiviewer.event.IndicatorTouchedEvent;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.IndicatorElastic;
import gr.cite.intelcomp.stiviewer.model.builder.IndicatorElasticBuilder;
import gr.cite.intelcomp.stiviewer.model.persist.IndicatorElasticPersist;
import gr.cite.intelcomp.stiviewer.model.persist.IndicatorPersist;
import gr.cite.intelcomp.stiviewer.model.persist.elasticindicator.*;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorConfigItem;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorConfigService;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorIndexCacheService;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.validation.ValidationService;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import javax.management.InvalidApplicationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ElasticIndicatorServiceImpl implements ElasticIndicatorService {

	private final TenantEntityManager entityManager;
	private final ElasticsearchRestTemplate elasticsearchRestTemplate;
	private final ObjectMapper mapper;

	private final EventBroker eventBroker;

	private final AuthorizationService authorizationService;

	private final BuilderFactory builderFactory;
	private final IndicatorService indicatorService;
	private final ValidationService vindicatorService;
	private final QueryFactory queryFactory;
	private final RestHighLevelClient restHighLevelClient;
	private final IndicatorConfigService indicatorsConfigService;
	private final IndicatorIndexCacheService indicatorIndexCacheService;
	private final MessageSource messageSource;
	private final ErrorThesaurusProperties errors;

	private final JsonHandlingService jsonHandlingService;

	@Autowired
	public ElasticIndicatorServiceImpl(TenantEntityManager entityManager,
	                                   ElasticsearchRestTemplate elasticsearchRestTemplate,
	                                   EventBroker eventBroker,
	                                   AuthorizationService authorizationService,
	                                   BuilderFactory builderFactory,
	                                   IndicatorService indicatorService,
	                                   ValidationService vindicatorService,
	                                   QueryFactory queryFactory,
	                                   RestHighLevelClient restHighLevelClient,
	                                   IndicatorConfigService indicatorsConfigService,
	                                   IndicatorIndexCacheService indicatorIndexCacheService,
	                                   ErrorThesaurusProperties errors,
	                                   MessageSource messageSource,
	                                   JsonHandlingService jsonHandlingService) {
		this.entityManager = entityManager;
		this.elasticsearchRestTemplate = elasticsearchRestTemplate;
		this.eventBroker = eventBroker;
		this.indicatorService = indicatorService;
		this.vindicatorService = vindicatorService;
		this.queryFactory = queryFactory;
		this.jsonHandlingService = jsonHandlingService;
		this.mapper = new ObjectMapper();
		this.mapper.registerModule(new JavaTimeModule());
		this.authorizationService = authorizationService;
		this.builderFactory = builderFactory;
		this.restHighLevelClient = restHighLevelClient;
		this.indicatorsConfigService = indicatorsConfigService;
		this.indicatorIndexCacheService = indicatorIndexCacheService;
		this.errors = errors;
		this.messageSource = messageSource;
	}

	@Override
	public IndicatorElastic persist(IndicatorElasticPersist persist, FieldSet fieldSet) throws IOException, InvalidApplicationException {
		this.authorizationService.authorizeForce(Permission.EditIndicator);
		UUID indicatorId = null;
		if (persist.getId() != null) {
			indicatorId = persist.getId();
			if (this.queryFactory.query(IndicatorElasticQuery.class).ids(indicatorId).count() > 0) throw new IllegalStateException("Updates not allowed");
		} else {
			indicatorId = UUID.randomUUID();
			persist.setId(indicatorId);
		}
		this.createDbIndicator(persist, indicatorId);

		IndicatorElasticEntity data = new IndicatorElasticEntity();
		data.setId(persist.getId());
		data.setMetadata(this.buildIndicatorMetadataEntity(persist.getMetadata()));
		data.setSchema(this.buildIndicatorSchemaEntity(persist.getSchema()));

		data = elasticsearchRestTemplate.save(data);
		this.ensureIndex(data.getId());

		this.eventBroker.emit(new IndicatorTouchedEvent(data.getId()));
		return builderFactory.builder(IndicatorElasticBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fieldSet, data);
	}

	private Indicator createDbIndicator(IndicatorElasticPersist persist, UUID indicatorId) throws IOException, InvalidApplicationException {
		IndicatorPersist data = new IndicatorPersist();
		data.setCode(this.calculateIndexName(persist.getMetadata().getCode(), true));
		data.setDescription(persist.getMetadata().getDescription());
		data.setName(persist.getMetadata().getLabel());
		this.vindicatorService.validateForce(data);
		return this.indicatorService.persist(data, null, indicatorId);
	}

	private IndicatorSchemaEntity buildIndicatorSchemaEntity(SchemaPersist persist) {
		if (persist == null) return null;
		IndicatorSchemaEntity data = new IndicatorSchemaEntity();
		data.setId(persist.getId() == null ? UUID.randomUUID() : persist.getId());
		if (persist.getFields() != null && !persist.getFields().isEmpty()) {
			List<FieldEntity> items = new ArrayList<>();
			for (FieldPersist item : persist.getFields()) items.add(this.buildFieldEntity(item));
			data.setFields(items);
		}
		return data;
	}

	private FieldEntity buildFieldEntity(FieldPersist persist) {
		if (persist == null) return null;
		FieldEntity data = new FieldEntity();
		data.setId(persist.getId() == null ? UUID.randomUUID() : persist.getId());
		data.setCode(persist.getCode());
		data.setDescription(persist.getDescription());
		data.setLabel(persist.getLabel());
		data.setName(persist.getName());
		data.setSubfieldOf(persist.getSubfieldOf());
		data.setTypeId(persist.getTypeId());
		data.setTypeSemantics(persist.getTypeSemantics());
		data.setBaseType(persist.getBaseType());
		data.setUseAs(persist.getUseAs());

		if (persist.getValidation() != null && !persist.getValidation().isEmpty()) {

			List<ValidationEntity> items = jsonHandlingService.fromJsonSafe(List.class, jsonHandlingService.toJsonSafe(persist.getValidation()));
			data.setValidation(items);
		}
		data.setValueField(persist.getValueField());
		data.setValueRange(this.buildValueRangeEntity(persist.getValueRange()));
		if (persist.getOperations() != null && !persist.getOperations().isEmpty()) {
			List<OperationEntity> items = new ArrayList<>();
			for (OperatorPersist item : persist.getOperations()) items.add(this.buildOperationEntity(item));
			data.setOperations(items);
		}
		if (persist.getAltLabels() != null && !persist.getAltLabels().isEmpty()) {
			List<AltTextEntity> items = new ArrayList<>();
			for (AltTextPersist item : persist.getAltLabels()) items.add(this.buildAltTextEntity(item));
			data.setAltLabels(items);
		}
		if (persist.getAltDescriptions() != null && !persist.getAltDescriptions().isEmpty()) {
			List<AltTextEntity> items = new ArrayList<>();
			for (AltTextPersist item : persist.getAltDescriptions()) items.add(this.buildAltTextEntity(item));
			data.setAltDescriptions(items);
		}
		return data;
	}

	private OperationEntity buildOperationEntity(OperatorPersist persist) {
		if (persist == null) return null;
		OperationEntity data = new OperationEntity();
		data.setOp(persist.getOp());
		return data;
	}

	private ValueRangeEntity buildValueRangeEntity(ValueRangePersist persist) {
		if (persist == null) return null;
		ValueRangeEntity data = new ValueRangeEntity();
		data.setMax(persist.getMax());
		data.setMin(persist.getMin());
		if (persist.getValues() != null && !persist.getValues().isEmpty()) {
			List<ValueRangeValueEntity> items = new ArrayList<>();
			for (ValueRangeValuePersist item : persist.getValues()) items.add(this.buildValueRangeValueEntity(item));
			data.setValues(items);
		}
		return data;
	}

	private ValueRangeValueEntity buildValueRangeValueEntity(ValueRangeValuePersist persist) {
		if (persist == null) return null;
		ValueRangeValueEntity data = new ValueRangeValueEntity();
		data.setValue(persist.getValue());
		data.setLabel(persist.getLabel());
		return data;
	}

	private IndicatorMetadataEntity buildIndicatorMetadataEntity(MetadataPersist persist) {
		if (persist == null) return null;
		IndicatorMetadataEntity data = new IndicatorMetadataEntity();
		data.setCode(persist.getCode());
		data.setDescription(persist.getDescription());
		data.setLabel(persist.getLabel());
		data.setUrl(persist.getUrl());
		data.setDate(persist.getDate());
		if (persist.getCoverage() != null && !persist.getCoverage().isEmpty()) {
			List<CoverageEntity> items = new ArrayList<>();
			for (CoveragePersist item : persist.getCoverage()) items.add(this.buildCoverageEntityEntity(item));
			data.setCoverage(items);
		}
		if (persist.getAltDescriptions() != null && !persist.getAltDescriptions().isEmpty()) {
			List<AltTextEntity> items = new ArrayList<>();
			for (AltTextPersist item : persist.getAltDescriptions()) items.add(this.buildAltTextEntity(item));
			data.setAltDescriptions(items);
		}
		if (persist.getAltLabels() != null && !persist.getAltLabels().isEmpty()) {
			List<AltTextEntity> items = new ArrayList<>();
			for (AltTextPersist item : persist.getAltLabels()) items.add(this.buildAltTextEntity(item));
			data.setAltLabels(items);
		}
		if (persist.getSemanticLabels() != null && !persist.getSemanticLabels().isEmpty()) {
			List<SemanticLabelEntity> items = new ArrayList<>();
			for (SemanticsLabelPersist item : persist.getSemanticLabels()) items.add(this.buildSemanticsLabelPersist(item));
			data.setSemanticLabels(items);
		}
		return data;
	}

	private CoverageEntity buildCoverageEntityEntity(CoveragePersist persist) {
		if (persist == null) return null;
		CoverageEntity data = new CoverageEntity();
		data.setMin(persist.getMin());
		data.setMax(persist.getMax());
		data.setLabel(persist.getLabel());
		return data;
	}

	private AltTextEntity buildAltTextEntity(AltTextPersist persist) {
		if (persist == null) return null;
		AltTextEntity data = new AltTextEntity();
		data.setText(persist.getText());
		data.setLang(persist.getLang());
		return data;
	}

	private SemanticLabelEntity buildSemanticsLabelPersist(SemanticsLabelPersist persist) {
		if (persist == null) return null;
		SemanticLabelEntity data = new SemanticLabelEntity();
		data.setLabel(persist.getLabel());
		return data;
	}

	public String calculateIndexName(String code, boolean ensureUnique) throws IOException {
		String index = "ic-sti-" + code + "-point-dev"; //TODO config
		if (ensureUnique && restHighLevelClient.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT)) throw new MyApplicationException(this.errors.getIndexAlreadyExists().getCode(), this.errors.getIndexAlreadyExists().getMessage());
		return index;
	}

	public String getIndexName(UUID indicatorId) throws InvalidApplicationException {
		IndicatorIndexCacheService.IndicatorIndexCacheValue cacheValue = this.indicatorIndexCacheService.lookup(this.indicatorIndexCacheService.buildKey(indicatorId));
		if (cacheValue == null) {
			IndicatorEntity data = this.entityManager.find(IndicatorEntity.class, indicatorId);
			if (data == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{indicatorId, Indicator.class.getSimpleName()}, LocaleContextHolder.getLocale()));
			cacheValue = new IndicatorIndexCacheService.IndicatorIndexCacheValue(indicatorId, data.getCode());
			this.indicatorIndexCacheService.put(cacheValue);
		}
		return cacheValue.getIndexName();
	}

	public String ensureIndex(UUID indicatorId) throws IOException, InvalidApplicationException {
		String index = this.getIndexName(indicatorId);
		Boolean exists = restHighLevelClient.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
		if (!exists) {
			IndicatorConfigItem indicatorConfigItem = this.indicatorsConfigService.getConfig(indicatorId);
			XContentBuilder builder = XContentFactory.jsonBuilder();
			builder.startObject();
			{
				builder.startObject("properties");
				{
					this.addFieldToIndexTemplate(builder, IndicatorPointEntity.Fields.id, IndicatorFieldBaseType.Keyword);
					this.addFieldToIndexTemplate(builder, IndicatorPointEntity.Fields.batchId, IndicatorFieldBaseType.Keyword);
					this.addFieldToIndexTemplate(builder, IndicatorPointEntity.Fields.batchTimestamp, IndicatorFieldBaseType.Date);
					this.addFieldToIndexTemplate(builder, IndicatorPointEntity.Fields.timestamp, IndicatorFieldBaseType.Date);
					this.addFieldToIndexTemplate(builder, IndicatorPointEntity.Fields.groupHash, IndicatorFieldBaseType.Keyword);
					builder.startObject(IndicatorPointEntity.Fields.groupInfo);
					{
						builder.field("type", FieldType.Object.getMappedName());
						builder.startObject("properties");
						{
							builder.startObject(DataGroupInfoEntity.Fields.columns);
							{
								builder.field("type", FieldType.Nested.getMappedName());
								builder.startObject("properties");
								{
									this.addFieldToIndexTemplate(builder, DataGroupInfoColumnEntity.Fields.fieldCode, IndicatorFieldBaseType.Keyword);
									this.addFieldToIndexTemplate(builder, DataGroupInfoColumnEntity.Fields.values, IndicatorFieldBaseType.KeywordArray);
								}
								builder.endObject();
							}
							builder.endObject();
						}
						builder.endObject();
					}
					builder.endObject();

					for (Map.Entry<String, FieldEntity> prop : indicatorConfigItem.getExtraProps().entrySet()) {
						this.addFieldToIndexTemplate(builder, this.indicatorsConfigService.ensurePropertyName(prop.getKey()), prop.getValue().getBaseType());
					}
				}
				builder.endObject();
			}
			builder.endObject();

			CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(new CreateIndexRequest(index).mapping(builder), RequestOptions.DEFAULT);
		}
		return index;
	}

	private void addFieldToIndexTemplate(XContentBuilder builder, String name, IndicatorFieldBaseType type) throws IOException {
		FieldType typeString = FieldType.Auto;
		FieldType subTypeString = FieldType.Auto;
		boolean isMap = false;
		String mapKeyName = "";
		String mapValueName = "";

		switch (type) {
			case String:
				typeString = FieldType.Text;
				break;
			case Keyword:
			case KeywordArray:
				typeString = FieldType.Keyword;
				break;
			case Date:
				typeString = FieldType.Date;
				break;
			case Double:
			case DoubleArray:
				typeString = FieldType.Double;
				break;
			case Integer:
			case IntegerArray:
				typeString = FieldType.Integer;
				break;
			case IntegerMap:
				typeString = FieldType.Nested;
				subTypeString = FieldType.Integer;
				mapKeyName = IndicatorDoubleMapEntity.Fields.key;
				mapValueName = IndicatorDoubleMapEntity.Fields.val;
				typeString = FieldType.Nested;
				isMap = true;
				break;
			case DoubleMap:
				subTypeString = FieldType.Double;
				typeString = FieldType.Nested;
				mapKeyName = IndicatorIntegerMapEntity.Fields.key;
				mapValueName = IndicatorIntegerMapEntity.Fields.val;
				isMap = true;
				break;
			default:
				throw new MyApplicationException("invalid type " + type);
		}

		if (!isMap) {
			builder.startObject(name);
			{
				builder.field("type", typeString.getMappedName());
			}
			builder.endObject();
		} else {
			builder.startObject(name);
			{
				builder.field("type", typeString.getMappedName());
				builder.startObject("properties");
				{
					builder.startObject(mapKeyName);
					{
						builder.field("type", FieldType.Keyword.getMappedName());
					}
					builder.endObject();
					builder.startObject(mapValueName);
					{
						builder.field("type", subTypeString.getMappedName());
					}
					builder.endObject();
				}
				builder.endObject();

			}
			builder.endObject();
		}
	}
}
