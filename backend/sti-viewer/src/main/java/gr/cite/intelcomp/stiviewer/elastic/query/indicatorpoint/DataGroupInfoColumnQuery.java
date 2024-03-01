package gr.cite.intelcomp.stiviewer.elastic.query.indicatorpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.DataGroupInfoColumnEntity;
import gr.cite.intelcomp.stiviewer.model.indicatorpoint.DataGroupInfoColumn;
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
public class DataGroupInfoColumnQuery extends ElasticNestedQuery<DataGroupInfoColumnQuery, DataGroupInfoColumnEntity, String> {

	private String nestedPath;

	@Override
	public DataGroupInfoColumnQuery nestedPath(String value) {
		this.nestedPath = value;
		return this;
	}

	private final ConventionService conventionService;

	public DataGroupInfoColumnQuery(
			ElasticsearchRestTemplate elasticsearchRestTemplate,
			ElasticProperties elasticProperties,
			ConventionService conventionService
	) {
		super(elasticsearchRestTemplate, elasticProperties);
		this.conventionService = conventionService;
	}

	@Override
	protected Class<DataGroupInfoColumnEntity> entityClass() {
		return DataGroupInfoColumnEntity.class;
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
	public DataGroupInfoColumnEntity convert(Map<String, Object> rawData, Set<String> columns) {
		DataGroupInfoColumnEntity mocDoc = new DataGroupInfoColumnEntity();
		ObjectMapper mapper = new ObjectMapper();
		if (columns.contains(DataGroupInfoColumnEntity.Fields.fieldCode)) mocDoc.setFieldCode(FieldBasedMapper.shallowSafeConversion(rawData.get(DataGroupInfoColumnEntity.Fields.fieldCode), String.class));
		if (columns.contains(DataGroupInfoColumnEntity.Fields.values)) mocDoc.setValues(FieldBasedMapper.shallowSafeConversion(rawData.get(DataGroupInfoColumnEntity.Fields.values), mapper.getTypeFactory().constructParametricType(List.class, String.class)));
		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(DataGroupInfoColumn._fieldCode)) return this.elasticFieldOf(DataGroupInfoColumnEntity.Fields.fieldCode).disableInfer(true);
		else if (item.match(DataGroupInfoColumn._values)) return this.elasticFieldOf(DataGroupInfoColumnEntity.Fields.values).disableInfer(true);
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
