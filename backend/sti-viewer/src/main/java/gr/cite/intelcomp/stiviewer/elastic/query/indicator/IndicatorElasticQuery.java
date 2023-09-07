package gr.cite.intelcomp.stiviewer.elastic.query.indicator;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.config.elastic.AppElasticProperties;
import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorElasticEntity;
import gr.cite.intelcomp.stiviewer.model.IndicatorElastic;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.configuration.ElasticProperties;
import gr.cite.tools.elastic.mapper.FieldBasedMapper;
import gr.cite.tools.elastic.query.ElasticField;
import gr.cite.tools.elastic.query.ElasticNestedQuery;
import gr.cite.tools.elastic.query.ElasticQuery;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
//Like in C# make it Transient
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class IndicatorElasticQuery extends ElasticQuery<IndicatorElasticEntity, UUID> {

    private Collection<UUID> ids;
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    public IndicatorElasticQuery ids(UUID value) {
        this.ids = List.of(value);
        return this;
    }

    public IndicatorElasticQuery ids(UUID... value) {
        this.ids = Arrays.asList(value);
        return this;
    }

    public IndicatorElasticQuery ids(Collection<UUID> values) {
        this.ids = values;
        return this;
    }

    public IndicatorElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    private final QueryFactory queryFactory;
    private final AppElasticProperties appElasticProperties;

    @Autowired()
    public IndicatorElasticQuery(ElasticsearchRestTemplate elasticsearchRestTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticProperties appElasticProperties) {
        super(elasticsearchRestTemplate, elasticProperties);
        this.queryFactory = queryFactory;
        this.appElasticProperties = appElasticProperties;
    }

    @Override
    protected Boolean isFalseQuery() {
        return this.isEmpty(this.ids);
    }

    @Override
    protected Class<IndicatorElasticEntity> entityClass() {
        return IndicatorElasticEntity.class;
    }

    @Override
    protected QueryBuilder applyFilters() {
        List<QueryBuilder> predicates = new ArrayList<>();
        if (ids != null) {
            predicates.add(this.containsUUID(this.elasticFieldOf(IndicatorElasticEntity.Fields.id), ids));
        }

        if (!predicates.isEmpty()) {
            return this.and(predicates);
        } else {
            return null;
        }
    }

    @Override
    public IndicatorElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
        IndicatorElasticEntity mocDoc = new IndicatorElasticEntity();
        if (columns.contains(IndicatorElasticEntity.Fields.id))
            mocDoc.setId(FieldBasedMapper.shallowSafeConversion(rawData.get(IndicatorElasticEntity.Fields.id), UUID.class));
        mocDoc.setSchema(this.convertInnerObject(rawData, columns, this.queryFactory.query(IndicatorSchemaQuery.class), IndicatorElasticEntity.Fields.schema, null));
        mocDoc.setMetadata(this.convertInnerObject(rawData, columns, this.queryFactory.query(IndicatorMetadataQuery.class), IndicatorElasticEntity.Fields.metadata, null));
        return mocDoc;
    }

    @Override
    protected ElasticField fieldNameOf(FieldResolver item) {
        if (item.match(IndicatorElastic._id)) return this.elasticFieldOf(IndicatorElasticEntity.Fields.id);
        else if (item.prefix(IndicatorElastic._schema))
            return this.queryFactory.query(IndicatorSchemaQuery.class).innerPath(IndicatorElasticEntity.Fields.schema).fieldNameOf(this.extractPrefixed(item, IndicatorElastic._schema));
        else return null;
    }

    @Override
    protected String[] getIndex() {
        List<String> indexNames = new ArrayList<>();
        indexNames.add(this.appElasticProperties.getIndicatorIndexName());
        return indexNames.toArray(new String[0]);
    }

    @Override
    protected UUID toKey(String key) {
        return UUID.fromString(key);
    }

    @Override
    protected ElasticField getKeyField() {
        return this.elasticFieldOf(IndicatorElasticEntity.Fields.id);
    }

    @Override
    protected ElasticNestedQuery<?, ?, ?> nestedQueryOf(FieldResolver item) {
        return null;
    }
}
