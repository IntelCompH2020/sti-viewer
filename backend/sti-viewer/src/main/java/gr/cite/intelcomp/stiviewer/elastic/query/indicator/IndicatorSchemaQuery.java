package gr.cite.intelcomp.stiviewer.elastic.query.indicator;

import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorIntegerMapEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.IndicatorSchemaEntity;
import gr.cite.intelcomp.stiviewer.model.elasticindicator.Schema;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.configuration.ElasticProperties;
import gr.cite.tools.elastic.mapper.FieldBasedMapper;
import gr.cite.tools.elastic.query.ElasticField;
import gr.cite.tools.elastic.query.ElasticInnerObjectQuery;
import gr.cite.tools.elastic.query.ElasticNestedQuery;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IndicatorSchemaQuery extends ElasticInnerObjectQuery<IndicatorSchemaQuery, IndicatorSchemaEntity, UUID> {

	private String innerPath;

	private final QueryFactory queryFactory;

	@Override
	public IndicatorSchemaQuery innerPath(String value) {
		this.innerPath = value;
		return this;
	}

	private final ConventionService conventionService;

	public IndicatorSchemaQuery(
			ElasticsearchRestTemplate elasticsearchRestTemplate,
			ElasticProperties elasticProperties,
			QueryFactory queryFactory,
			ConventionService conventionService
	) {
		super(elasticsearchRestTemplate, elasticProperties);
		this.queryFactory = queryFactory;
		this.conventionService = conventionService;
	}

	@Override
	protected Class<IndicatorSchemaEntity> entityClass() {
		return IndicatorSchemaEntity.class;
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
	public IndicatorSchemaEntity convert(Map<String, Object> rawData, Set<String> columns) {
		IndicatorSchemaEntity mocDoc = new IndicatorSchemaEntity();
		if (columns.contains(IndicatorSchemaEntity.Fields.id)) mocDoc.setId(FieldBasedMapper.shallowSafeConversion(rawData.get(IndicatorSchemaEntity.Fields.id), UUID.class));
		mocDoc.setFields(this.convertNested(rawData, columns, this.queryFactory.query(FieldQuery.class), IndicatorSchemaEntity.Fields.fields, this.getInnerPath()));
		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(Schema._id)) return this.elasticFieldOf(IndicatorSchemaEntity.Fields.id).disableInfer(true);
		else if (item.prefix(Schema._fields)) return this.queryFactory.query(FieldQuery.class).nestedPath(this.conventionService.asIndexer(this.getInnerPath(), IndicatorSchemaEntity.Fields.fields)).fieldNameOf(this.extractPrefixed(item, Schema._fields));
		else return null;
	}

	@Override
	protected String getInnerPath() {
		return this.innerPath;
	}

	@Override
	protected UUID toKey(String key) {
		return UUID.fromString(key);
	}

	@Override
	protected ElasticField getKeyField() {
		return this.elasticFieldOf(IndicatorIntegerMapEntity.Fields.key);
	}

	@Override
	protected ElasticNestedQuery<?, ?, ?> nestedQueryOf(FieldResolver item) {
		return null;
	}
}
