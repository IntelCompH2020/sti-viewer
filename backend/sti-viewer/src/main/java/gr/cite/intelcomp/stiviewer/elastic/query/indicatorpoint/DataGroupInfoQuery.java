package gr.cite.intelcomp.stiviewer.elastic.query.indicatorpoint;

import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.DataGroupInfoEntity;
import gr.cite.intelcomp.stiviewer.model.indicatorpoint.DataGroupInfo;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.configuration.ElasticProperties;
import gr.cite.tools.elastic.query.ElasticField;
import gr.cite.tools.elastic.query.ElasticInnerObjectQuery;
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
public class DataGroupInfoQuery extends ElasticInnerObjectQuery<DataGroupInfoQuery, DataGroupInfoEntity, String> {

    private String innerPath;

    private final QueryFactory queryFactory;

    public DataGroupInfoQuery innerPath(String value) {
        this.innerPath = value;
        return this;
    }

    private final ConventionService conventionService;

    public DataGroupInfoQuery(
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
    protected Class<DataGroupInfoEntity> entityClass() {
        return DataGroupInfoEntity.class;
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
    public DataGroupInfoEntity convert(Map<String, Object> rawData, Set<String> columns) {
        DataGroupInfoEntity mocDoc = new DataGroupInfoEntity();
        mocDoc.setColumns(this.convertNested(rawData, columns, this.queryFactory.query(DataGroupInfoColumnQuery.class), DataGroupInfoEntity.Fields.columns, this.getInnerPath()));
        return mocDoc;
    }


    @Override
    protected ElasticField fieldNameOf(FieldResolver item) {
        if (item.prefix(DataGroupInfo._columns))
            return this.queryFactory.query(DataGroupInfoColumnQuery.class).nestedPath(this.conventionService.asIndexer(this.getInnerPath(), DataGroupInfoEntity.Fields.columns)).fieldNameOf(this.extractPrefixed(item, DataGroupInfo._columns));
        else
            return null;
    }

    @Override
    protected String getInnerPath() {
        return this.innerPath;
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
