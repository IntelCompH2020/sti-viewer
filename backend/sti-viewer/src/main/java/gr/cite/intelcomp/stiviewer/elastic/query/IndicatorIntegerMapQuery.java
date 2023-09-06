package gr.cite.intelcomp.stiviewer.elastic.query;

import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorIntegerMapEntity;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.elastic.configuration.ElasticProperties;
import gr.cite.tools.elastic.mapper.FieldBasedMapper;
import gr.cite.tools.elastic.query.Aggregation.MetricAggregateType;
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
public class IndicatorIntegerMapQuery extends ElasticNestedQuery<IndicatorIntegerMapQuery, IndicatorIntegerMapEntity, String> {

    private String nestedPath;
    private Collection<String> keys;

    public IndicatorIntegerMapQuery keys(String value) {
        this.keys = List.of(value);
        return this;
    }

    public IndicatorIntegerMapQuery keys(String... value) {
        this.keys = Arrays.asList(value);
        return this;
    }

    public IndicatorIntegerMapQuery keys(Collection<String> values) {
        this.keys = values;
        return this;
    }

    @Override
    public IndicatorIntegerMapQuery nestedPath(String nestedPath) {
        this.nestedPath = nestedPath;
        return this;
    }

    public IndicatorIntegerMapQuery(
            ElasticsearchRestTemplate elasticsearchRestTemplate,
            ElasticProperties elasticProperties) {
        super(elasticsearchRestTemplate, elasticProperties);
    }

    @Override
    protected Class<IndicatorIntegerMapEntity> entityClass() {
        return IndicatorIntegerMapEntity.class;
    }

    @Override
    protected Boolean isFalseQuery() {
        return this.isEmpty(this.keys);
    }

    @Override
    protected QueryBuilder applyAuthZ() {
        return null;
    }

    @Override
    protected QueryBuilder applyFilters() {
        List<QueryBuilder> predicates = new ArrayList<>();
        if (keys != null) {
            predicates.add(this.containsString(this.elasticFieldOf(IndicatorIntegerMapEntity.Fields.key), keys));
        }

        if (!predicates.isEmpty()) {
            return this.and(predicates);
        } else {
            return null;
        }
    }

    @Override
    public IndicatorIntegerMapEntity convert(Map<String, Object> rawData, Set<String> columns) {
        IndicatorIntegerMapEntity mocDoc = new IndicatorIntegerMapEntity();
        if (columns.contains(IndicatorIntegerMapEntity.Fields.key))
            mocDoc.setKey(FieldBasedMapper.shallowSafeConversion(rawData.get(IndicatorIntegerMapEntity.Fields.key), String.class));
        if (columns.contains(IndicatorIntegerMapEntity.Fields.val))
            mocDoc.setVal(FieldBasedMapper.shallowSafeConversion(rawData.get(IndicatorIntegerMapEntity.Fields.val), Integer.class));
        return mocDoc;
    }

    @Override
    protected ElasticField fieldNameOf(FieldResolver item) {
        if (item.match(IndicatorIntegerMapEntity.Fields.key))
            return this.elasticFieldOf(IndicatorIntegerMapEntity.Fields.key);
        else if (item.match(IndicatorIntegerMapEntity.Fields.val))
            return this.elasticFieldOf(IndicatorIntegerMapEntity.Fields.val);
        else return null;
    }

    @Override
    protected String getNestedPath() {
        return this.nestedPath;
    }

    @Override
    protected Boolean supportsMetricAggregate(MetricAggregateType metricAggregateType, FieldResolver item) {
        return item.match(IndicatorIntegerMapEntity.Fields.val);
    }

    @Override
    protected String toKey(String key) {
        return key;
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
