package gr.cite.intelcomp.stiviewer.elastic.query.indicatorpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationContentResolver;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.HierarchyIndicatorColumnAccess;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.enums.IndicatorFieldBaseType;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorDoubleMapEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorIntegerMapEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.FieldEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.IndicatorPointEntity;
import gr.cite.intelcomp.stiviewer.elastic.query.IndicatorDoubleMapQuery;
import gr.cite.intelcomp.stiviewer.elastic.query.IndicatorIntegerMapQuery;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter.*;
import gr.cite.intelcomp.stiviewer.model.indicatorpoint.IndicatorPoint;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorConfigItem;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorConfigService;
import gr.cite.intelcomp.stiviewer.service.indicatorelastic.ElasticIndicatorService;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.configuration.ElasticProperties;
import gr.cite.tools.elastic.mapper.FieldBasedMapper;
import gr.cite.tools.elastic.query.Aggregation.MetricAggregateType;
import gr.cite.tools.elastic.query.ElasticField;
import gr.cite.tools.elastic.query.ElasticFields;
import gr.cite.tools.elastic.query.ElasticNestedQuery;
import gr.cite.tools.elastic.query.ElasticQuery;
import gr.cite.tools.exception.MyApplicationException;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Component;

import javax.management.InvalidApplicationException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IndicatorPointQuery extends ElasticQuery<IndicatorPointEntity, UUID> {
	private Collection<UUID> ids;
	private Collection<UUID> indicatorIds;
	private Collection<IndicatorPointKeywordFilter> keywordFilters;
	private Collection<IndicatorPointKeywordFilter> keywordExcludedFilters;
	private Collection<IndicatorPointDateFilter> dateFilters;
	private Collection<IndicatorPointIntegerFilter> integerFilters;
	private Collection<IndicatorPointDoubleFilter> doubleFilters;
	private Collection<IndicatorPointDateRangeFilter> dateRangeFilters;
	private Collection<IndicatorPointIntegerRangeFilter> integerRangeFilters;
	private Collection<IndicatorPointDoubleRangeFilter> doubleRangeFilters;
	private Collection<String> groupHashes;
	private IndicatorPointLikeFilter fieldLikeFilter;
	private List<IndicatorConfigItem> indicatorConfigItems;

	private final ElasticIndicatorService elasticIndicatorService;
	private final IndicatorConfigService indicatorsConfigService;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);
	private final UserScope userScope;
	private final AuthorizationService authService;
	private final AuthorizationContentResolver authorizationContentResolver;
	private final QueryFactory queryFactory;

	public IndicatorPointQuery(
			ElasticsearchRestTemplate elasticsearchRestTemplate,
			ElasticIndicatorService elasticIndicatorService,
			IndicatorConfigService indicatorsConfigService,
			UserScope userScope,
			AuthorizationService authService,
			AuthorizationContentResolver authorizationContentResolver,
			ElasticProperties elasticProperties,
			QueryFactory queryFactory) {
		super(elasticsearchRestTemplate, elasticProperties);
		this.elasticIndicatorService = elasticIndicatorService;
		this.indicatorsConfigService = indicatorsConfigService;
		this.userScope = userScope;
		this.authService = authService;
		this.authorizationContentResolver = authorizationContentResolver;
		this.queryFactory = queryFactory;
	}

//	public IndicatorPointQuery indicatorId(UUID value) {
//		this.indicatorId = value;
//		this.indicatorConfigItem = this.indicatorId == null ? null : this.indicatorsConfigService.getConfig(value);
//		return this;
//	}

	public IndicatorPointQuery indicatorIds(UUID value) {
		this.indicatorIds = List.of(value);
		this.updateIndicatorConfigItems();
		return this;
	}

	public IndicatorPointQuery indicatorIds(UUID... value) {
		this.indicatorIds = Arrays.asList(value);
		this.updateIndicatorConfigItems();
		return this;
	}

	public IndicatorPointQuery indicatorIds(Collection<UUID> values) {
		this.indicatorIds = values;
		this.updateIndicatorConfigItems();
		return this;
	}

	private void updateIndicatorConfigItems() {
		List<IndicatorConfigItem> items = new ArrayList<>();
		if (this.indicatorIds == null || this.indicatorIds.isEmpty()) {
			items = null;
		} else {
			for (UUID indicatorId : this.indicatorIds) items.add(this.indicatorsConfigService.getConfig(indicatorId));
		}
		this.indicatorConfigItems = items;
	}

	public IndicatorPointQuery ids(UUID value) {
		this.ids = List.of(value);
		return this;
	}

	public IndicatorPointQuery ids(UUID... value) {
		this.ids = Arrays.asList(value);
		return this;
	}

	public IndicatorPointQuery ids(Collection<UUID> values) {
		this.ids = values;
		return this;
	}

	public IndicatorPointQuery groupHashes(String value) {
		this.groupHashes = List.of(value);
		return this;
	}

	public IndicatorPointQuery groupHashes(String... value) {
		this.groupHashes = Arrays.asList(value);
		return this;
	}

	public IndicatorPointQuery groupHashes(Collection<String> values) {
		this.groupHashes = values;
		return this;
	}

	public IndicatorPointQuery keywordFilters(IndicatorPointKeywordFilter value) {
		this.keywordFilters = List.of(value);
		return this;
	}

	public IndicatorPointQuery keywordFilters(IndicatorPointKeywordFilter... value) {
		this.keywordFilters = Arrays.asList(value);
		return this;
	}

	public IndicatorPointQuery keywordFilters(Collection<IndicatorPointKeywordFilter> values) {
		this.keywordFilters = values;
		return this;
	}

	public IndicatorPointQuery keywordExcludedFilters(IndicatorPointKeywordFilter value) {
		this.keywordExcludedFilters = List.of(value);
		return this;
	}

	public IndicatorPointQuery keywordExcludedFilters(IndicatorPointKeywordFilter... value) {
		this.keywordExcludedFilters = Arrays.asList(value);
		return this;
	}

	public IndicatorPointQuery keywordExcludedFilters(Collection<IndicatorPointKeywordFilter> values) {
		this.keywordExcludedFilters = values;
		return this;
	}

	public IndicatorPointQuery fieldLikeFilter(IndicatorPointLikeFilter value) {
		this.fieldLikeFilter = value;
		return this;
	}

	public IndicatorPointQuery dateFilters(IndicatorPointDateFilter value) {
		this.dateFilters = List.of(value);
		return this;
	}

	public IndicatorPointQuery dateFilters(IndicatorPointDateFilter... value) {
		this.dateFilters = Arrays.asList(value);
		return this;
	}

	public IndicatorPointQuery dateFilters(Collection<IndicatorPointDateFilter> values) {
		this.dateFilters = values;
		return this;
	}

	public IndicatorPointQuery doubleFilters(IndicatorPointDoubleFilter value) {
		this.doubleFilters = List.of(value);
		return this;
	}

	public IndicatorPointQuery doubleFilters(IndicatorPointDoubleFilter... value) {
		this.doubleFilters = Arrays.asList(value);
		return this;
	}

	public IndicatorPointQuery doubleFilters(Collection<IndicatorPointDoubleFilter> values) {
		this.doubleFilters = values;
		return this;
	}

	public IndicatorPointQuery integerFilters(IndicatorPointIntegerFilter value) {
		this.integerFilters = List.of(value);
		return this;
	}

	public IndicatorPointQuery integerFilters(IndicatorPointIntegerFilter... value) {
		this.integerFilters = Arrays.asList(value);
		return this;
	}

	public IndicatorPointQuery integerFilters(Collection<IndicatorPointIntegerFilter> values) {
		this.integerFilters = values;
		return this;
	}

	public IndicatorPointQuery dateRangeFilters(IndicatorPointDateRangeFilter value) {
		this.dateRangeFilters = List.of(value);
		return this;
	}

	public IndicatorPointQuery dateRangeFilters(IndicatorPointDateRangeFilter... value) {
		this.dateRangeFilters = Arrays.asList(value);
		return this;
	}

	public IndicatorPointQuery dateRangeFilters(Collection<IndicatorPointDateRangeFilter> values) {
		this.dateRangeFilters = values;
		return this;
	}

	public IndicatorPointQuery doubleRangeFilters(IndicatorPointDoubleRangeFilter value) {
		this.doubleRangeFilters = List.of(value);
		return this;
	}

	public IndicatorPointQuery doubleRangeFilters(IndicatorPointDoubleRangeFilter... value) {
		this.doubleRangeFilters = Arrays.asList(value);
		return this;
	}

	public IndicatorPointQuery doubleRangeFilters(Collection<IndicatorPointDoubleRangeFilter> values) {
		this.doubleRangeFilters = values;
		return this;
	}

	public IndicatorPointQuery integerRangeFilters(IndicatorPointIntegerRangeFilter value) {
		this.integerRangeFilters = List.of(value);
		return this;
	}

	public IndicatorPointQuery integerRangeFilters(IndicatorPointIntegerRangeFilter... value) {
		this.integerRangeFilters = Arrays.asList(value);
		return this;
	}

	public IndicatorPointQuery integerRangeFilters(Collection<IndicatorPointIntegerRangeFilter> values) {
		this.integerRangeFilters = values;
		return this;
	}


	public IndicatorPointQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	protected String[] getIndex() {
		if (this.indicatorIds != null && !this.indicatorIds.isEmpty()) {
			try {
				List<String> indexNames = new ArrayList<>();
				for (UUID indicatorId : this.indicatorIds) indexNames.add(this.elasticIndicatorService.getIndexName(indicatorId));
				return indexNames.toArray(new String[indexNames.size()]);
			} catch (InvalidApplicationException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			return super.getIndex();
		}
	}

	@Override
	protected Class<IndicatorPointEntity> entityClass() {
		return IndicatorPointEntity.class;
	}


	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.ids) || this.isEmpty(this.indicatorIds) || this.isEmpty(this.keywordExcludedFilters) || this.isEmpty(this.keywordFilters)
				|| this.isEmpty(this.dateRangeFilters) || this.isEmpty(this.integerRangeFilters) || this.isEmpty(this.doubleRangeFilters) || this.isEmpty(this.dateFilters)
				|| this.isEmpty(this.integerFilters) || this.isEmpty(this.doubleFilters) ||this.isEmpty(this.groupHashes);
	}

	@Override
	protected QueryBuilder applyAuthZ() {
		if (this.authorize.contains(AuthorizationFlags.None)) return null;
		if (this.authorize.contains(AuthorizationFlags.Permission) && this.authService.authorize(Permission.BrowseIndicatorPoint)) return null;
		List<UUID> allowedIndicatorIds = null;
		List<HierarchyIndicatorColumnAccess> indicatorColumnAccesses = null;
		if (this.authorize.contains(AuthorizationFlags.Indicator)) allowedIndicatorIds = this.authorizationContentResolver.affiliatedIndicators(Permission.BrowseIndicatorPoint);
		
		if (this.indicatorIds != null) {
			if (allowedIndicatorIds == null) {
				this.indicatorIds(new ArrayList<>());
			} else {
				Set<UUID> result = this.indicatorIds.stream().distinct().filter(allowedIndicatorIds::contains).collect(Collectors.toSet());
				this.indicatorIds(result);
			}
		} else {
			this.indicatorIds(allowedIndicatorIds);
		}

		if (this.authorize.contains(AuthorizationFlags.IndicatorAccess)) indicatorColumnAccesses = this.authorizationContentResolver.indicatorAllowedKeywords(this.indicatorIds != null ? indicatorIds.toArray(new UUID[this.indicatorIds.size()]) : null);
		if (indicatorColumnAccesses != null && !indicatorColumnAccesses.isEmpty()) {
			Map<String, List<HierarchyIndicatorColumnAccess>>  group = HierarchyIndicatorColumnAccess.groupByCode(indicatorColumnAccesses);
			List<QueryBuilder> predicates =  this.buildHierarchyIndicatorColumnAccessQuery(indicatorColumnAccesses, new ArrayList<>(), new HashMap<>());
			return this.or(predicates);
		}
		return null;
	}
	
	private List<QueryBuilder> buildHierarchyIndicatorColumnAccessQuery(List<HierarchyIndicatorColumnAccess> indicatorColumnAccesses, List<QueryBuilder> orItems, Map<String, String> andItems){
		Map<String, List<HierarchyIndicatorColumnAccess>>  group = HierarchyIndicatorColumnAccess.groupByCode(indicatorColumnAccesses);
		for (String code : group.keySet()) {
			List<HierarchyIndicatorColumnAccess> groupItems = group.get(code);
			List<String> lastChildValues = new ArrayList<>();
			for (HierarchyIndicatorColumnAccess groupItem : groupItems) {
				if (groupItem.isLastChild()) {
					lastChildValues.add(groupItem.getValue());
				} else {
					andItems.put(code, groupItem.getValue());
					orItems = this.buildHierarchyIndicatorColumnAccessQuery(groupItem.getChildItems(), orItems, andItems);
				}
			}
			if (!lastChildValues.isEmpty()){
				List<QueryBuilder> queryBuilders = new ArrayList<>();
				for (String key : andItems.keySet()) {
					queryBuilders.add(this.getContainsQueryBuilderForHierarchyIndicatorColumnAccess(key, List.of(andItems.get(key))));
				}
				queryBuilders.add(this.getContainsQueryBuilderForHierarchyIndicatorColumnAccess(groupItems.get(0).getCode(), lastChildValues));
				orItems.add(this.and(queryBuilders));
			}
		}
		
		return orItems;
	}
	
	private QueryBuilder getContainsQueryBuilderForHierarchyIndicatorColumnAccess(String code, List<String> values) {
		FieldEntity fieldEntity = this.getOrDefaultFieldEntity(code, null);
		if (fieldEntity == null) throw new MyApplicationException("invalid field " + code);
		if (fieldEntity.getBaseType() == IndicatorFieldBaseType.IntegerMap) {
			IndicatorIntegerMapQuery query = this.queryFactory.query(IndicatorIntegerMapQuery.class).nestedPath(fieldEntity.getCode());
			query.keys(values);
			return this.nestedQuery(query);
		} else if (fieldEntity.getBaseType() == IndicatorFieldBaseType.DoubleMap) {
			IndicatorDoubleMapQuery query = this.queryFactory.query(IndicatorDoubleMapQuery.class).nestedPath(fieldEntity.getCode());
			query.keys(values);
			return this.nestedQuery(query);
		} else if (fieldEntity.getBaseType() == IndicatorFieldBaseType.Keyword) {
			return this.containsString(this.fieldNameOf(new FieldResolver(code)), values);
		} else {
			throw new MyApplicationException("invalid field " + code);
		}
	}

	@Override
	protected QueryBuilder applyFilters() {
		List<QueryBuilder> predicates = new ArrayList<>();
		if (ids != null) {
			predicates.add(this.containsUUID(this.elasticFieldOf(IndicatorPointEntity.Fields.id), ids));
		}

		if (groupHashes != null) {
			predicates.add(this.containsString(this.elasticFieldOf(IndicatorPointEntity.Fields.groupHash), groupHashes));
		} else {
			predicates.add(this.fieldNotExists(this.elasticFieldOf(IndicatorPointEntity.Fields.groupHash)));
		}

		if (fieldLikeFilter != null && fieldLikeFilter.getLike() != null && !fieldLikeFilter.getLike().isBlank() && fieldLikeFilter.getFields() != null) {
			ElasticFields elasticFields = this.elasticFieldsOf();
			for (String field : fieldLikeFilter.getFields()) {
				FieldEntity fieldEntity = this.getOrDefaultFieldEntity(field, null);
				if (fieldEntity.getBaseType() == IndicatorFieldBaseType.Keyword || fieldEntity.getBaseType() == IndicatorFieldBaseType.KeywordArray) {
					elasticFields.add(field, "text", true, "icu_analyzer_text");
				} else {
					elasticFields.add(field, true);
				}
			}
			String like = fieldLikeFilter.getLike();
			if (!fieldLikeFilter.getLike().startsWith("*")) like = "*" + like;
			if (!fieldLikeFilter.getLike().endsWith("*")) like = like + "*";
			predicates.add(this.like(elasticFields, List.of(like)));
		}

		if (keywordFilters != null) {
			for (IndicatorPointKeywordFilter keywordFilter : keywordFilters) {
				FieldEntity fieldEntity = this.getOrDefaultFieldEntity(keywordFilter.getField(), null);
				if (fieldEntity == null) throw new MyApplicationException("invalid field " + keywordFilter.getField());

				if (fieldEntity.getBaseType() == IndicatorFieldBaseType.IntegerMap) {
					IndicatorIntegerMapQuery query = this.queryFactory.query(IndicatorIntegerMapQuery.class).nestedPath(fieldEntity.getCode());
					query.keys(keywordFilter.getValues());
					predicates.add(this.nestedQuery(query));
				} else if (fieldEntity.getBaseType() == IndicatorFieldBaseType.DoubleMap) {
					IndicatorDoubleMapQuery query = this.queryFactory.query(IndicatorDoubleMapQuery.class).nestedPath(fieldEntity.getCode());
					query.keys(keywordFilter.getValues());
					predicates.add(this.nestedQuery(query));
				} else if (fieldEntity.getBaseType() == IndicatorFieldBaseType.Keyword) {
					predicates.add(this.containsString(this.fieldNameOf(new FieldResolver(keywordFilter.getField())), keywordFilter.getValues()));
				} else {
					throw new MyApplicationException("invalid field " + keywordFilter.getField());
				}
			}
		}

		if (keywordExcludedFilters != null) {
			for (IndicatorPointKeywordFilter keywordFilter : keywordExcludedFilters) {
				FieldEntity fieldEntity = this.getOrDefaultFieldEntity(keywordFilter.getField(), null);
				if (fieldEntity == null) throw new MyApplicationException("invalid field " + keywordFilter.getField());

				if (fieldEntity.getBaseType() == IndicatorFieldBaseType.IntegerMap) {
					IndicatorIntegerMapQuery query = this.queryFactory.query(IndicatorIntegerMapQuery.class).nestedPath(fieldEntity.getCode());
					query.keys(keywordFilter.getValues());
					predicates.add(this.not(this.nestedQuery(query)));
				} else if (fieldEntity.getBaseType() == IndicatorFieldBaseType.DoubleMap) {
					IndicatorDoubleMapQuery query = this.queryFactory.query(IndicatorDoubleMapQuery.class).nestedPath(fieldEntity.getCode());
					query.keys(keywordFilter.getValues());
					predicates.add(this.not(this.nestedQuery(query)));
				} else if (fieldEntity.getBaseType() == IndicatorFieldBaseType.Keyword) {
					predicates.add(this.not(this.containsString(this.fieldNameOf(new FieldResolver(keywordFilter.getField())), keywordFilter.getValues())));
				} else {
					throw new MyApplicationException("invalid field " + keywordFilter.getField());
				}
			}
		}

		if (dateRangeFilters != null) {
			for (IndicatorPointDateRangeFilter filer : dateRangeFilters) {
				FieldEntity fieldEntity = this.getOrDefaultFieldEntity(filer.getField(), null);
				if (fieldEntity == null || fieldEntity.getBaseType() != IndicatorFieldBaseType.Date) throw new MyApplicationException("invalid field " + filer.getField());
				predicates.add(this.rangeQuery(this.fieldNameOf(new FieldResolver(filer.getField())), filer.getFrom(), filer.getTo()));
			}
		}

		if (integerRangeFilters != null) {
			for (IndicatorPointIntegerRangeFilter filer : integerRangeFilters) {
				FieldEntity fieldEntity = this.getOrDefaultFieldEntity(filer.getField(), null);
				if (fieldEntity == null || fieldEntity.getBaseType() != IndicatorFieldBaseType.Integer) throw new MyApplicationException("invalid field " + filer.getField());
				predicates.add(this.rangeQuery(this.fieldNameOf(new FieldResolver(filer.getField())), filer.getFrom(), filer.getTo()));
			}
		}

		if (doubleRangeFilters != null) {
			for (IndicatorPointDoubleRangeFilter filer : doubleRangeFilters) {
				FieldEntity fieldEntity = this.getOrDefaultFieldEntity(filer.getField(), null);
				if (fieldEntity == null || fieldEntity.getBaseType() != IndicatorFieldBaseType.Double) throw new MyApplicationException("invalid field " + filer.getField());
				predicates.add(this.rangeQuery(this.fieldNameOf(new FieldResolver(filer.getField())), filer.getFrom(), filer.getTo()));
			}
		}

		if (dateFilters != null) {
			for (IndicatorPointDateFilter filer : dateFilters) {
				FieldEntity fieldEntity = this.getOrDefaultFieldEntity(filer.getField(), null);
				if (fieldEntity == null || fieldEntity.getBaseType() != IndicatorFieldBaseType.Date) throw new MyApplicationException("invalid field " + filer.getField());
				predicates.add(this.valueCompareQuery(this.fieldNameOf(new FieldResolver(filer.getField())), filer.getValue(), filer.getCompareOperator()));
			}
		}

		if (integerFilters != null) {
			for (IndicatorPointIntegerFilter filer : integerFilters) {
				FieldEntity fieldEntity = this.getOrDefaultFieldEntity(filer.getField(), null);
				if (fieldEntity == null || fieldEntity.getBaseType() != IndicatorFieldBaseType.Integer) throw new MyApplicationException("invalid field " + filer.getField());
				predicates.add(this.valueCompareQuery(this.fieldNameOf(new FieldResolver(filer.getField())), filer.getValue(), filer.getCompareOperator()));
			}
		}

		if (doubleFilters != null) {
			for (IndicatorPointDoubleFilter filer : doubleFilters) {
				FieldEntity fieldEntity = this.getOrDefaultFieldEntity(filer.getField(), null);
				if (fieldEntity == null || fieldEntity.getBaseType() != IndicatorFieldBaseType.Double) throw new MyApplicationException("invalid field " + filer.getField());
				predicates.add(this.valueCompareQuery(this.fieldNameOf(new FieldResolver(filer.getField())), filer.getValue(), filer.getCompareOperator()));
			}
		}

		if (predicates.size() > 0) {
			return this.and(predicates);
		} else {
			return null;
		}
	}


	@Override
	public IndicatorPointEntity convert(Map<String, Object> rawData, Set<String> columns) {
		IndicatorPointEntity mocDoc = new IndicatorPointEntity();
		if (columns.contains(IndicatorPointEntity.Fields.id)) mocDoc.setId(FieldBasedMapper.shallowSafeConversion(rawData.get(IndicatorPointEntity.Fields.id), UUID.class));
		if (columns.contains(IndicatorPointEntity.Fields.batchId)) mocDoc.setBatchId(FieldBasedMapper.shallowSafeConversion(rawData.get(IndicatorPointEntity.Fields.batchId), String.class));
		if (columns.contains(IndicatorPointEntity.Fields.timestamp)) mocDoc.setBatchTimestamp(FieldBasedMapper.shallowSafeConversion(rawData.get(IndicatorPointEntity.Fields.timestamp), Date.class));
		if (columns.contains(IndicatorPointEntity.Fields.batchTimestamp)) mocDoc.setBatchTimestamp(FieldBasedMapper.shallowSafeConversion(rawData.get(IndicatorPointEntity.Fields.batchTimestamp), Date.class));
		if (columns.contains(IndicatorPointEntity.Fields.groupHash)) mocDoc.setGroupHash(FieldBasedMapper.shallowSafeConversion(rawData.get(IndicatorPointEntity.Fields.groupHash), String.class));

		mocDoc.setGroupInfo(this.convertInnerObject(rawData, columns, this.queryFactory.query(DataGroupInfoQuery.class), IndicatorPointEntity.Fields.groupInfo, null));

		ObjectMapper mapper = new ObjectMapper();
		if (this.indicatorConfigItems != null) {
			Map<String, java.lang.Object> properties = new HashMap<>();
			for (IndicatorConfigItem indicatorConfigItem : this.indicatorConfigItems) {
				for (Map.Entry<String, FieldEntity> prop : indicatorConfigItem.getExtraProps().entrySet()) {
					java.lang.Object alreadyParsedPropertyValue = properties.getOrDefault(prop.getKey(), null);
					if (alreadyParsedPropertyValue == null && rawData.containsKey(prop.getKey())) {
						switch (prop.getValue().getBaseType()) {
							case String:
							case Keyword:
								properties.put(prop.getKey(), FieldBasedMapper.shallowSafeConversion(rawData.get(prop.getKey()), String.class));
								break;
							case Date:
								properties.put(prop.getKey(), FieldBasedMapper.shallowSafeConversion(rawData.get(prop.getKey()), Date.class));
								break;
							case Double:
								properties.put(prop.getKey(), FieldBasedMapper.shallowSafeConversion(rawData.get(prop.getKey()), Double.class));
								break;
							case Integer:
								properties.put(prop.getKey(), FieldBasedMapper.shallowSafeConversion(rawData.get(prop.getKey()), Integer.class));
								break;
							case KeywordArray:
								properties.put(prop.getKey(), FieldBasedMapper.shallowSafeConversion(rawData.get(prop.getKey()), mapper.getTypeFactory().constructParametricType(List.class, String.class)));
								break;
							case IntegerArray:
								properties.put(prop.getKey(), FieldBasedMapper.shallowSafeConversion(rawData.get(prop.getKey()), mapper.getTypeFactory().constructParametricType(List.class, Integer.class)));
								break;
							case DoubleArray:
								properties.put(prop.getKey(), FieldBasedMapper.shallowSafeConversion(rawData.get(prop.getKey()), mapper.getTypeFactory().constructParametricType(List.class, Double.class)));
								break;
							case IntegerMap:
								properties.put(prop.getKey(), FieldBasedMapper.shallowSafeConversion(rawData.get(prop.getKey()), mapper.getTypeFactory().constructParametricType(List.class, IndicatorIntegerMapEntity.class)));
								break;
							case DoubleMap:
								properties.put(prop.getKey(), FieldBasedMapper.shallowSafeConversion(rawData.get(prop.getKey()), mapper.getTypeFactory().constructParametricType(List.class, IndicatorDoubleMapEntity.class)));
								break;
							default:
								throw new MyApplicationException("invalid type " + prop.getValue().getBaseType());
						}
					}
				}
			}
			mocDoc.setProperties(properties);
		}
		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(IndicatorPoint._id)) return this.elasticFieldOf(IndicatorPointEntity.Fields.id);
		else if (item.match(IndicatorPoint._score)) return this.elasticFieldOf(IndicatorPointEntity.Fields.score).disableInfer(true);
		else if (item.match(IndicatorPoint._batchId)) return this.elasticFieldOf(IndicatorPointEntity.Fields.batchId);
		else if (item.match(IndicatorPoint._batchTimestamp)) return this.elasticFieldOf(IndicatorPointEntity.Fields.batchTimestamp);
		else if (item.match(IndicatorPoint._timestamp)) return this.elasticFieldOf(IndicatorPointEntity.Fields.timestamp);
		else if (item.match(IndicatorPoint._groupHash)) return this.elasticFieldOf(IndicatorPointEntity.Fields.groupHash);
		else if (item.prefix(IndicatorPoint._groupInfo)) return this.queryFactory.query(DataGroupInfoQuery.class).innerPath(IndicatorPointEntity.Fields.groupInfo).fieldNameOf(this.extractPrefixed(item, IndicatorPoint._groupInfo));
		else if (item.prefix(IndicatorPoint._groupInfo)) return this.queryFactory.query(DataGroupInfoQuery.class).innerPath(IndicatorPointEntity.Fields.groupInfo).fieldNameOf(this.extractPrefixed(item, IndicatorPoint._groupInfo));
		else if (this.containsFieldEntity(item.getField())) return this.elasticFieldOf(this.getFieldEntity(item.getField()).getCode()).disableInfer(true);
		else if (item.getPrefix() != null && this.containsFieldEntity(item.getPrefix())) return this.elasticFieldOf(item.getFieldWithoutPrefix())
				.nestedPath(List.of(this.getFieldEntity(item.getPrefix()).getCode())).disableInfer(true);
		else return null;
	}

	@Override
	protected Boolean supportsMetricAggregate(MetricAggregateType metricAggregateType, FieldResolver resolver) {
		FieldEntity fieldEntity = this.getOrDefaultFieldEntity(resolver.getField(), null);
		return fieldEntity != null && (fieldEntity.getBaseType() == IndicatorFieldBaseType.Double || fieldEntity.getBaseType() == IndicatorFieldBaseType.Integer);
	}

	@Override
	protected UUID toKey(String key) {
		return UUID.fromString(key);
	}

	@Override
	protected ElasticField getKeyField() {
		return this.elasticFieldOf(IndicatorPointEntity.Fields.id);
	}

	@Override
	protected ElasticNestedQuery<?, ?, ?> nestedQueryOf(FieldResolver item) {
		FieldEntity fieldEntity = this.getOrDefaultFieldEntity(item.getField(), null);
		if (fieldEntity == null && item.getPrefix() != null) fieldEntity = this.getOrDefaultFieldEntity(item.getPrefix(), null);

		if (fieldEntity != null && fieldEntity.getBaseType() == IndicatorFieldBaseType.IntegerMap) return this.queryFactory.query(IndicatorIntegerMapQuery.class).nestedPath(fieldEntity.getCode());
		else if (fieldEntity != null && fieldEntity.getBaseType() == IndicatorFieldBaseType.DoubleMap) return this.queryFactory.query(IndicatorDoubleMapQuery.class).nestedPath(fieldEntity.getCode());
		else return null;
	}

	private FieldEntity getFieldEntity(String field) {
		FieldEntity fieldEntity = this.getOrDefaultFieldEntity(field, null);
		if (fieldEntity == null) throw new MyApplicationException("invalid field " + field);
		return fieldEntity;
	}

	private boolean containsFieldEntity(String field) {
		FieldEntity fieldEntity = this.getOrDefaultFieldEntity(field, null);
		return fieldEntity != null;
	}

	private FieldEntity getOrDefaultFieldEntity(String field, FieldEntity defaultValue) {
		if (this.indicatorConfigItems == null) return defaultValue;
		IndicatorConfigItem indicatorConfigItem = this.indicatorConfigItems.stream().filter(x -> x.getExtraProps().getOrDefault(field, null) != null).findFirst().orElse(null);
		if (indicatorConfigItem == null) return defaultValue;
		FieldEntity fieldEntity = indicatorConfigItem.getExtraProps().get(field);
		if (fieldEntity == null) return defaultValue;
		return fieldEntity;
	}


}

