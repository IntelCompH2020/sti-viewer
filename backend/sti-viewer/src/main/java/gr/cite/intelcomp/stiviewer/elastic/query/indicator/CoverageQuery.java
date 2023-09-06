package gr.cite.intelcomp.stiviewer.elastic.query.indicator;

import gr.cite.intelcomp.stiviewer.elastic.data.indicator.CoverageEntity;
import gr.cite.intelcomp.stiviewer.model.elasticindicator.Coverage;
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

import java.util.Map;
import java.util.Set;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CoverageQuery extends ElasticNestedQuery<CoverageQuery, CoverageEntity, String> {

    private String nestedPath;

    @Override
    public CoverageQuery nestedPath(String value) {
        this.nestedPath = value;
        return this;
    }

    public CoverageQuery(
            ElasticsearchRestTemplate elasticsearchRestTemplate,
            ElasticProperties elasticProperties
    ) {
        super(elasticsearchRestTemplate, elasticProperties);
    }

    @Override
    protected Class<CoverageEntity> entityClass() {
        return CoverageEntity.class;
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
    public CoverageEntity convert(Map<String, Object> rawData, Set<String> columns) {
        CoverageEntity mocDoc = new CoverageEntity();
        if (columns.contains(CoverageEntity.Fields.label))
            mocDoc.setLabel(FieldBasedMapper.shallowSafeConversion(rawData.get(CoverageEntity.Fields.label), String.class));
        if (columns.contains(CoverageEntity.Fields.max))
            mocDoc.setMax(FieldBasedMapper.shallowSafeConversion(rawData.get(CoverageEntity.Fields.max), Double.class));
        if (columns.contains(CoverageEntity.Fields.min))
            mocDoc.setMin(FieldBasedMapper.shallowSafeConversion(rawData.get(CoverageEntity.Fields.min), Double.class));
        return mocDoc;
    }

    @Override
    protected ElasticField fieldNameOf(FieldResolver item) {
        if (item.match(Coverage._label)) return this.elasticFieldOf(CoverageEntity.Fields.label).disableInfer(true);
        else if (item.match(Coverage._max)) return this.elasticFieldOf(CoverageEntity.Fields.max).disableInfer(true);
        else if (item.match(Coverage._min)) return this.elasticFieldOf(CoverageEntity.Fields.min).disableInfer(true);
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
