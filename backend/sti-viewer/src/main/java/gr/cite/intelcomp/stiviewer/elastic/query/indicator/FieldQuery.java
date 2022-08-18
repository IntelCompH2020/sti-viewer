package gr.cite.intelcomp.stiviewer.elastic.query.indicator;

import gr.cite.intelcomp.stiviewer.common.enums.IndicatorFieldBaseType;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.FieldEntity;
import gr.cite.intelcomp.stiviewer.model.elasticindicator.Field;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.configuration.ElasticProperties;
import gr.cite.tools.elastic.mapper.FieldBasedMapper;
import gr.cite.tools.elastic.query.ElasticField;
import gr.cite.tools.elastic.query.ElasticNestedQuery;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FieldQuery extends ElasticNestedQuery<FieldQuery, FieldEntity, String> {

	private String nestedPath;
	private final QueryFactory queryFactory;

	@Override
	public FieldQuery nestedPath(String value) {
		this.nestedPath = value;
		return this;
	}

	private final ConventionService conventionService;

	public FieldQuery(
			ElasticsearchRestTemplate elasticsearchRestTemplate,
			ElasticProperties elasticProperties,
			QueryFactory queryFactory, ConventionService conventionService
	) {
		super(elasticsearchRestTemplate, elasticProperties);
		this.queryFactory = queryFactory;
		this.conventionService = conventionService;
	}

	@Override
	protected Class<FieldEntity> entityClass() {
		return FieldEntity.class;
	}

	@Override
	protected Boolean isFalseQuery() {
		return false;
	}

	@Override
	protected QueryBuilder applyAuthZ() {
		return null;
	}

	@Override
	protected QueryBuilder applyFilters() {
		List<QueryBuilder> predicates = new ArrayList<>();

		if (predicates.size() > 0) {
			return this.and(predicates);
		} else {
			return null;
		}
	}

	@Override
	public FieldEntity convert(Map<String, Object> rawData, Set<String> columns) {
		FieldEntity mocDoc = new FieldEntity();
		if (columns.contains(FieldEntity.Fields.id)) mocDoc.setId(FieldBasedMapper.shallowSafeConversion(rawData.get(FieldEntity.Fields.id), UUID.class));
		if (columns.contains(FieldEntity.Fields.name)) mocDoc.setName(FieldBasedMapper.shallowSafeConversion(rawData.get(FieldEntity.Fields.name), String.class));
		if (columns.contains(FieldEntity.Fields.code)) mocDoc.setCode(FieldBasedMapper.shallowSafeConversion(rawData.get(FieldEntity.Fields.code), String.class));
		if (columns.contains(FieldEntity.Fields.baseType)) mocDoc.setBaseType(FieldBasedMapper.shallowSafeConversion(rawData.get(FieldEntity.Fields.baseType), IndicatorFieldBaseType.class));
		if (columns.contains(FieldEntity.Fields.label)) mocDoc.setLabel(FieldBasedMapper.shallowSafeConversion(rawData.get(FieldEntity.Fields.label), String.class));
		if (columns.contains(FieldEntity.Fields.description)) mocDoc.setDescription(FieldBasedMapper.shallowSafeConversion(rawData.get(FieldEntity.Fields.description), String.class));
		if (columns.contains(FieldEntity.Fields.typeSemantics)) mocDoc.setTypeSemantics(FieldBasedMapper.shallowSafeConversion(rawData.get(FieldEntity.Fields.typeSemantics), String.class));
		if (columns.contains(FieldEntity.Fields.typeId)) mocDoc.setTypeId(FieldBasedMapper.shallowSafeConversion(rawData.get(FieldEntity.Fields.typeId), String.class));
		if (columns.contains(FieldEntity.Fields.subfieldOf)) mocDoc.setSubfieldOf(FieldBasedMapper.shallowSafeConversion(rawData.get(FieldEntity.Fields.subfieldOf), String.class));
		if (columns.contains(FieldEntity.Fields.valueField)) mocDoc.setValueField(FieldBasedMapper.shallowSafeConversion(rawData.get(FieldEntity.Fields.valueField), String.class));
		if (columns.contains(FieldEntity.Fields.useAs)) mocDoc.setUseAs(FieldBasedMapper.shallowSafeConversion(rawData.get(FieldEntity.Fields.useAs), String.class));
		mocDoc.setAltLabels(this.convertNested(rawData, columns, this.queryFactory.query(AltTextQuery.class), FieldEntity.Fields.altLabels, this.getNestedPath()));
		mocDoc.setAltDescriptions(this.convertNested(rawData, columns, this.queryFactory.query(AltTextQuery.class), FieldEntity.Fields.altDescriptions, this.getNestedPath()));
		mocDoc.setOperations(this.convertNested(rawData, columns, this.queryFactory.query(OperationQuery.class), FieldEntity.Fields.operations, this.getNestedPath()));
		mocDoc.setValueRange(this.convertInnerObject(rawData, columns, this.queryFactory.query(ValueRangeQuery.class), FieldEntity.Fields.valueRange, this.getNestedPath()));
		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(Field._id)) return this.elasticFieldOf(FieldEntity.Fields.id).disableInfer(true);
		else if (item.match(Field._name)) return this.elasticFieldOf(FieldEntity.Fields.name).disableInfer(true);
		else if (item.match(Field._code)) return this.elasticFieldOf(FieldEntity.Fields.code).disableInfer(true);
		else if (item.match(Field._baseType)) return this.elasticFieldOf(FieldEntity.Fields.baseType).disableInfer(true);
		else if (item.match(Field._label)) return this.elasticFieldOf(FieldEntity.Fields.label).disableInfer(true);
		else if (item.match(Field._description)) return this.elasticFieldOf(FieldEntity.Fields.description).disableInfer(true);
		else if (item.match(Field._typeSemantics)) return this.elasticFieldOf(FieldEntity.Fields.typeSemantics).disableInfer(true);
		else if (item.match(Field._typeId)) return this.elasticFieldOf(FieldEntity.Fields.typeId).disableInfer(true);
		else if (item.match(Field._subfieldOf)) return this.elasticFieldOf(FieldEntity.Fields.subfieldOf).disableInfer(true);
		else if (item.match(Field._valueField)) return this.elasticFieldOf(FieldEntity.Fields.valueField).disableInfer(true);
		else if (item.match(Field._useAs)) return this.elasticFieldOf(FieldEntity.Fields.useAs).disableInfer(true);
		else if (item.prefix(Field._altLabels)) return this.queryFactory.query(AltTextQuery.class).nestedPath(this.conventionService.asIndexer(this.getNestedPath(), FieldEntity.Fields.altLabels)).fieldNameOf(this.extractPrefixed(item, Field._altLabels));
		else if (item.prefix(Field._altDescriptions)) return this.queryFactory.query(AltTextQuery.class).nestedPath(this.conventionService.asIndexer(this.getNestedPath(), FieldEntity.Fields.altDescriptions)).fieldNameOf(this.extractPrefixed(item, Field._altDescriptions));
		else if (item.prefix(Field._operations)) return this.queryFactory.query(OperationQuery.class).nestedPath(this.conventionService.asIndexer(this.getNestedPath(), FieldEntity.Fields.operations)).fieldNameOf(this.extractPrefixed(item, Field._operations));
		else if (item.prefix(Field._valueRange)) return this.queryFactory.query(ValueRangeQuery.class).innerPath(this.conventionService.asIndexer(this.getNestedPath(), FieldEntity.Fields.valueRange)).fieldNameOf(this.extractPrefixed(item, Field._valueRange));
		else return null;
	}

	@Override
	protected String getNestedPath() {
		return this.nestedPath;
	}

	@Override
	protected String toKey(String key) {
		return key;
	}

	@Override
	protected ElasticField getKeyField() {
		return this.elasticFieldOf(FieldEntity.Fields.id);
	}

	@Override
	protected ElasticNestedQuery<?, ?, ?> nestedQueryOf(FieldResolver item) {
		return null;
	}
}
