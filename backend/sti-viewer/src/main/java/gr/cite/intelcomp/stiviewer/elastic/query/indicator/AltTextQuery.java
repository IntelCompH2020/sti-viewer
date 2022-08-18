package gr.cite.intelcomp.stiviewer.elastic.query.indicator;

import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.AltTextEntity;
import gr.cite.intelcomp.stiviewer.model.elasticindicator.AltText;
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
public class AltTextQuery extends ElasticNestedQuery<AltTextQuery, AltTextEntity, String> {

	private String nestedPath;

	@Override
	public AltTextQuery nestedPath(String value) {
		this.nestedPath = value;
		return this;
	}

	private final ConventionService conventionService;

	public AltTextQuery(
			ElasticsearchRestTemplate elasticsearchRestTemplate,
			ElasticProperties elasticProperties,
			ConventionService conventionService
	) {
		super(elasticsearchRestTemplate, elasticProperties);
		this.conventionService = conventionService;
	}

	@Override
	protected Class<AltTextEntity> entityClass() {
		return AltTextEntity.class;
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
	public AltTextEntity convert(Map<String, Object> rawData, Set<String> columns) {
		AltTextEntity mocDoc = new AltTextEntity();
		if (columns.contains(AltTextEntity.Fields.text)) mocDoc.setText(FieldBasedMapper.shallowSafeConversion(rawData.get(AltTextEntity.Fields.text), String.class));
		if (columns.contains(AltTextEntity.Fields.lang)) mocDoc.setLang(FieldBasedMapper.shallowSafeConversion(rawData.get(AltTextEntity.Fields.lang), String.class));
		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(AltText._text)) return this.elasticFieldOf(AltTextEntity.Fields.text).disableInfer(true);
		else if (item.match(AltText._lang)) return this.elasticFieldOf(AltTextEntity.Fields.lang).disableInfer(true);
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
