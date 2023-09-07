package gr.cite.intelcomp.stiviewer.elastic.query.indicator;

import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.IndicatorMetadataEntity;
import gr.cite.intelcomp.stiviewer.model.elasticindicator.Metadata;
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

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IndicatorMetadataQuery extends ElasticInnerObjectQuery<IndicatorMetadataQuery, IndicatorMetadataEntity, String> {

	private String innerPath;

	private final QueryFactory queryFactory;

	public IndicatorMetadataQuery innerPath(String value) {
		this.innerPath = value;
		return this;
	}

	private final ConventionService conventionService;

	public IndicatorMetadataQuery(
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
	protected Class<IndicatorMetadataEntity> entityClass() {
		return IndicatorMetadataEntity.class;
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
	public IndicatorMetadataEntity convert(Map<String, Object> rawData, Set<String> columns) {
		IndicatorMetadataEntity mocDoc = new IndicatorMetadataEntity();
		if (columns.contains(IndicatorMetadataEntity.Fields.label))
			mocDoc.setLabel(FieldBasedMapper.shallowSafeConversion(rawData.get(IndicatorMetadataEntity.Fields.label), String.class));
		if (columns.contains(IndicatorMetadataEntity.Fields.description))
			mocDoc.setDescription(FieldBasedMapper.shallowSafeConversion(rawData.get(IndicatorMetadataEntity.Fields.description), String.class));
		if (columns.contains(IndicatorMetadataEntity.Fields.url))
			mocDoc.setUrl(FieldBasedMapper.shallowSafeConversion(rawData.get(IndicatorMetadataEntity.Fields.url), String.class));
		if (columns.contains(IndicatorMetadataEntity.Fields.code))
			mocDoc.setCode(FieldBasedMapper.shallowSafeConversion(rawData.get(IndicatorMetadataEntity.Fields.code), String.class));
		if (columns.contains(IndicatorMetadataEntity.Fields.date))
			mocDoc.setDate(FieldBasedMapper.shallowSafeConversion(rawData.get(IndicatorMetadataEntity.Fields.date), Instant.class));
		if (columns.contains(IndicatorMetadataEntity.Fields.date))
			mocDoc.setDate(FieldBasedMapper.shallowSafeConversion(rawData.get(IndicatorMetadataEntity.Fields.date), Instant.class));

		mocDoc.setSemanticLabels(this.convertNested(rawData, columns, this.queryFactory.query(SemanticLabelQuery.class), IndicatorMetadataEntity.Fields.semanticLabels, this.getInnerPath()));
		mocDoc.setAltLabels(this.convertNested(rawData, columns, this.queryFactory.query(AltTextQuery.class), IndicatorMetadataEntity.Fields.altLabels, this.getInnerPath()));
		mocDoc.setAltDescriptions(this.convertNested(rawData, columns, this.queryFactory.query(AltTextQuery.class), IndicatorMetadataEntity.Fields.altDescriptions, this.getInnerPath()));
		mocDoc.setCoverage(this.convertNested(rawData, columns, this.queryFactory.query(CoverageQuery.class), IndicatorMetadataEntity.Fields.coverage, this.getInnerPath()));
		return mocDoc;
	}


	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(Metadata._label))
			return this.elasticFieldOf(IndicatorMetadataEntity.Fields.label).disableInfer(true);
		else if (item.match(Metadata._description))
			return this.elasticFieldOf(IndicatorMetadataEntity.Fields.description).disableInfer(true);
		else if (item.match(Metadata._url))
			return this.elasticFieldOf(IndicatorMetadataEntity.Fields.url).disableInfer(true);
		else if (item.match(Metadata._code))
			return this.elasticFieldOf(IndicatorMetadataEntity.Fields.code).disableInfer(true);
		else if (item.match(Metadata._date))
			return this.elasticFieldOf(IndicatorMetadataEntity.Fields.date).disableInfer(true);
		else if (item.prefix(Metadata._semanticLabels))
			return this.queryFactory.query(SemanticLabelQuery.class).nestedPath(this.conventionService.asIndexer(this.getInnerPath(), IndicatorMetadataEntity.Fields.semanticLabels)).fieldNameOf(this.extractPrefixed(item, Metadata._semanticLabels));
		else if (item.prefix(Metadata._altLabels))
			return this.queryFactory.query(AltTextQuery.class).nestedPath(this.conventionService.asIndexer(this.getInnerPath(), IndicatorMetadataEntity.Fields.altLabels)).fieldNameOf(this.extractPrefixed(item, Metadata._altLabels));
		else if (item.prefix(Metadata._altDescriptions))
			return this.queryFactory.query(AltTextQuery.class).nestedPath(this.conventionService.asIndexer(this.getInnerPath(), IndicatorMetadataEntity.Fields.altDescriptions)).fieldNameOf(this.extractPrefixed(item, Metadata._altDescriptions));
		else if (item.prefix(Metadata._coverage))
			return this.queryFactory.query(CoverageQuery.class).nestedPath(this.conventionService.asIndexer(this.getInnerPath(), IndicatorMetadataEntity.Fields.coverage)).fieldNameOf(this.extractPrefixed(item, Metadata._coverage));
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
