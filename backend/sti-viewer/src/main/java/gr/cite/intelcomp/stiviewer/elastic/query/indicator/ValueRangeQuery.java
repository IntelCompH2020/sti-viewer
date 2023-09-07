package gr.cite.intelcomp.stiviewer.elastic.query.indicator;

import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorIntegerMapEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.ValueRangeEntity;
import gr.cite.intelcomp.stiviewer.model.elasticindicator.ValueRange;
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
public class ValueRangeQuery extends ElasticInnerObjectQuery<ValueRangeQuery, ValueRangeEntity, UUID> {

    private String innerPath;

    private final QueryFactory queryFactory;

    @Override
    public ValueRangeQuery innerPath(String value) {
        this.innerPath = value;
        return this;
    }

    private final ConventionService conventionService;

    public ValueRangeQuery(
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
    protected Class<ValueRangeEntity> entityClass() {
        return ValueRangeEntity.class;
    }

    @Override
    protected Boolean isFalseQuery() {
        return Boolean.FALSE;
    }

    @Override
    protected QueryBuilder applyAuthZ() {
        return null;
    }

    @Override
    protected QueryBuilder applyFilters() {
//		List<QueryBuilder> predicates = new ArrayList<>();
//
//		if (predicates.size() > 0) {
//			return this.and(predicates);
//		} else {
//			return null;
//		}
        return null;
    }

    @Override
    public ValueRangeEntity convert(Map<String, Object> rawData, Set<String> columns) {
        ValueRangeEntity mocDoc = new ValueRangeEntity();
        if (columns.contains(ValueRangeEntity.Fields.min))
            mocDoc.setMin(FieldBasedMapper.shallowSafeConversion(rawData.get(ValueRangeEntity.Fields.min), Double.class));
        if (columns.contains(ValueRangeEntity.Fields.max))
            mocDoc.setMax(FieldBasedMapper.shallowSafeConversion(rawData.get(ValueRangeEntity.Fields.max), Double.class));
        mocDoc.setValues(this.convertNested(rawData, columns, this.queryFactory.query(ValueRangeValueQuery.class), ValueRangeEntity.Fields.values, this.getInnerPath()));
        return mocDoc;
    }

    @Override
    protected ElasticField fieldNameOf(FieldResolver item) {
        if (item.match(ValueRange._min))
            return this.elasticFieldOf(ValueRangeEntity.Fields.min).disableInfer(true);
        else if (item.match(ValueRange._max))
            return this.elasticFieldOf(ValueRangeEntity.Fields.max).disableInfer(true);
        else if (item.prefix(ValueRange._values))
            return this.queryFactory.query(ValueRangeValueQuery.class).nestedPath(this.conventionService.asIndexer(this.getInnerPath(), ValueRangeEntity.Fields.values)).fieldNameOf(this.extractPrefixed(item, ValueRange._values));
        else
            return null;
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
