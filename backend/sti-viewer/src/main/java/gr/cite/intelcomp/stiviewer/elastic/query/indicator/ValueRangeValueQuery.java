package gr.cite.intelcomp.stiviewer.elastic.query.indicator;

import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.ValueRangeValueEntity;
import gr.cite.intelcomp.stiviewer.model.elasticindicator.ValueRangeValue;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.elastic.configuration.ElasticProperties;
import gr.cite.tools.elastic.mapper.FieldBasedMapper;
import gr.cite.tools.elastic.query.ElasticField;
import gr.cite.tools.elastic.query.ElasticNestedQuery;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ValueRangeValueQuery extends ElasticNestedQuery<ValueRangeValueQuery, ValueRangeValueEntity, String> {

	private String nestedPath;

	@Override
	public ValueRangeValueQuery nestedPath(String value) {
		this.nestedPath = value;
		return this;
	}

	private final ConventionService conventionService;

	public ValueRangeValueQuery(
			ElasticsearchRestTemplate elasticsearchRestTemplate,
			ElasticProperties elasticProperties,
			ConventionService conventionService
	) {
		super(elasticsearchRestTemplate, elasticProperties);
		this.conventionService = conventionService;
	}

	@Override
	protected Class<ValueRangeValueEntity> entityClass() {
		return ValueRangeValueEntity.class;
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
	public ValueRangeValueEntity convert(Map<String, Object> rawData, Set<String> columns) {
		ValueRangeValueEntity mocDoc = new ValueRangeValueEntity();
		if (columns.contains(ValueRangeValueEntity.Fields.value)) mocDoc.setValue(FieldBasedMapper.shallowSafeConversion(rawData.get(ValueRangeValueEntity.Fields.value), String.class));
		if (columns.contains(ValueRangeValueEntity.Fields.label)) mocDoc.setLabel(FieldBasedMapper.shallowSafeConversion(rawData.get(ValueRangeValueEntity.Fields.label), String.class));
		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(ValueRangeValue._value)) return this.elasticFieldOf(ValueRangeValueEntity.Fields.value).disableInfer(true);
		else if (item.match(ValueRangeValue._label)) return this.elasticFieldOf(ValueRangeValueEntity.Fields.label).disableInfer(true);
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
		return null;
	}

	@Override
	protected ElasticNestedQuery<?, ?, ?> nestedQueryOf(FieldResolver item) {
		return null;
	}
}
