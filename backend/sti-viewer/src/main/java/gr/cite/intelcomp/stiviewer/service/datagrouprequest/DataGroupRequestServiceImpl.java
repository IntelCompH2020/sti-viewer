package gr.cite.intelcomp.stiviewer.service.datagrouprequest;


import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.DataGroupRequestStatus;
import gr.cite.intelcomp.stiviewer.common.enums.IndicatorFieldBaseType;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.enums.ScheduledEventType;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.common.types.datagrouprequest.DataGroupColumnEntity;
import gr.cite.intelcomp.stiviewer.common.types.datagrouprequest.DataGroupRequestConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.DataGroupRequestEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorDoubleMapEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorIntegerMapEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.FieldEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.DataGroupInfoColumnEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.DataGroupInfoEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.IndicatorPointEntity;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpoint.IndicatorPointQuery;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter.IndicatorPointKeywordFilter;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.eventscheduler.manage.ScheduledEventManageService;
import gr.cite.intelcomp.stiviewer.eventscheduler.manage.ScheduledEventPublishData;
import gr.cite.intelcomp.stiviewer.eventscheduler.processing.datagroupbuild.CreateDataGroupScheduledEventData;
import gr.cite.intelcomp.stiviewer.model.builder.datagrouprequest.DataGroupRequestBuilder;
import gr.cite.intelcomp.stiviewer.model.datagrouprequest.DataGroupRequest;
import gr.cite.intelcomp.stiviewer.model.deleter.DataGroupRequestDeleter;
import gr.cite.intelcomp.stiviewer.model.persist.datagrouprequest.DataGroupColumnPersist;
import gr.cite.intelcomp.stiviewer.model.persist.datagrouprequest.DataGroupRequestConfigPersist;
import gr.cite.intelcomp.stiviewer.model.persist.datagrouprequest.DataGroupRequestNamePersist;
import gr.cite.intelcomp.stiviewer.model.persist.datagrouprequest.DataGroupRequestPersist;
import gr.cite.intelcomp.stiviewer.query.DataGroupRequestQuery;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorConfigItem;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorConfigService;
import gr.cite.intelcomp.stiviewer.service.indicatorelastic.ElasticIndicatorService;
import gr.cite.intelcomp.stiviewer.service.indicatorgroup.IndicatorGroupService;
import gr.cite.intelcomp.stiviewer.service.user.UserServiceImpl;
import gr.cite.tools.cipher.CipherService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.deleter.DeleterFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.query.ElasticField;
import gr.cite.tools.elastic.query.ScrollResponse;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import gr.cite.tools.validation.ValidationService;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequestScope
public class DataGroupRequestServiceImpl implements DataGroupRequestService {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserServiceImpl.class));

	private final TenantEntityManager entityManager;

	private final AuthorizationService authorizationService;
	private final BuilderFactory builderFactory;
	private final DeleterFactory deleterFactory;
	private final ConventionService conventionService;
	private final ErrorThesaurusProperties errors;
	private final MessageSource messageSource;
	private final UserScope userScope;
	private final TenantScope tenantScope;
	private final JsonHandlingService jsonHandlingService;
	private final IndicatorGroupService indicatorGroupService;
	private final ValidationService validation;
	private final QueryFactory queryFactory;
	private final CipherService cipherService;
	private final IndicatorConfigService indicatorsConfigService;
	private final ElasticsearchRestTemplate elasticsearchTemplate;
	private final ElasticIndicatorService elasticIndicatorService;

	private final ScheduledEventManageService scheduledEventManageService;

	@Autowired
	public DataGroupRequestServiceImpl(
			TenantEntityManager entityManager,
			AuthorizationService authorizationService,
			BuilderFactory builderFactory,
			DeleterFactory deleterFactory,
			ConventionService conventionService,
			ErrorThesaurusProperties errors,
			MessageSource messageSource,
			UserScope userScope,
			TenantScope tenantScope,
			JsonHandlingService jsonHandlingService,
			IndicatorGroupService indicatorGroupService,
			ValidationService validation,
			QueryFactory queryFactory,
			CipherService cipherService,
			IndicatorConfigService indicatorsConfigService,
			ElasticsearchRestTemplate elasticsearchTemplate,
			ElasticIndicatorService elasticIndicatorService,
			ScheduledEventManageService scheduledEventManageService
	) {
		this.entityManager = entityManager;
		this.authorizationService = authorizationService;
		this.builderFactory = builderFactory;
		this.deleterFactory = deleterFactory;
		this.conventionService = conventionService;
		this.errors = errors;
		this.messageSource = messageSource;
		this.userScope = userScope;
		this.tenantScope = tenantScope;
		this.jsonHandlingService = jsonHandlingService;
		this.indicatorGroupService = indicatorGroupService;
		this.validation = validation;
		this.queryFactory = queryFactory;
		this.cipherService = cipherService;
		this.indicatorsConfigService = indicatorsConfigService;
		this.elasticsearchTemplate = elasticsearchTemplate;
		this.elasticIndicatorService = elasticIndicatorService;
		this.scheduledEventManageService = scheduledEventManageService;
	}

	public DataGroupRequest persist(DataGroupRequestPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException, NoSuchAlgorithmException {
		logger.debug(new MapLogEntry("persisting Data Group Request").And("model", model).And("fields", fields));

		this.authorizationService.authorizeForce(Permission.EditDataGroupRequest);

		Boolean isUpdate = this.conventionService.isValidGuid(model.getId());

		DataGroupRequestEntity data;
		if (isUpdate) {
			data = this.entityManager.find(DataGroupRequestEntity.class, model.getId());
			if (data == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), DataGroupRequest.class.getSimpleName()}, LocaleContextHolder.getLocale()));
			if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash())) throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
		} else {
			data = new DataGroupRequestEntity();
			data.setId(UUID.randomUUID());
			data.setCreatedAt(Instant.now());
			data.setIsActive(IsActive.ACTIVE);
			data.setUserId(this.userScope.getUserId());
			data.setStatus(DataGroupRequestStatus.NEW);
		}

		this.canEditStatusForce(data);
		this.canSetStatusForce(model.getStatus(), data);

		if (model.getStatus() != DataGroupRequestStatus.NEW) {
			this.deleteAndSave(data.getId());

			data = new DataGroupRequestEntity();
			data.setId(UUID.randomUUID());
			data.setName(model.getName());
			data.setCreatedAt(Instant.now());
			data.setIsActive(IsActive.ACTIVE);
			data.setUserId(this.userScope.getUserId());
			data.setStatus(DataGroupRequestStatus.NEW);

			isUpdate = false;
		}

		DataGroupRequestConfigEntity config = this.mapToDataGroupRequestConfig(model.getConfig());
		boolean statusChanged = !isUpdate || model.getStatus() != data.getStatus();

		data.setConfig(jsonHandlingService.toJsonSafe(config));
		data.setGroupHash(this.computeGroupHash(config));
		data.setStatus(model.getStatus());
		data.setName(model.getName());
		data.setUpdatedAt(Instant.now());

		if (isUpdate) this.entityManager.merge(data);
		else this.entityManager.persist(data);

		this.setDataGroupStatusForBuild(data);

		this.entityManager.flush();

		return this.builderFactory.builder(DataGroupRequestBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, DataGroupRequest._id, DataGroupRequest._hash), data);
	}

	public DataGroupRequest persist(DataGroupRequestNamePersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException, NoSuchAlgorithmException {
		logger.debug(new MapLogEntry("persisting Data Group Request").And("model", model).And("fields", fields));

		this.authorizationService.authorizeForce(Permission.EditDataGroupRequest);

		DataGroupRequestEntity data = this.entityManager.find(DataGroupRequestEntity.class, model.getId());
		if (data == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), DataGroupRequest.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash())) throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());

		this.canEditStatusForce(data);

		data.setName(model.getName());
		data.setUpdatedAt(Instant.now());

		this.entityManager.merge(data);

		this.setDataGroupStatusForBuild(data);

		this.entityManager.flush();

		return this.builderFactory.builder(DataGroupRequestBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, DataGroupRequest._id, DataGroupRequest._hash), data);
	}

	private DataGroupRequestConfigEntity mapToDataGroupRequestConfig(DataGroupRequestConfigPersist config) {
		if (config == null) return null;
		DataGroupRequestConfigEntity persistConfig = new DataGroupRequestConfigEntity();
		persistConfig.setIndicatorGroupId(config.getIndicatorGroupId());
		if (config.getGroupColumns() != null) {
			List<DataGroupColumnEntity> dataGroupColumnEntities = new ArrayList<>();
			for (DataGroupColumnPersist groupColumn : config.getGroupColumns()) {
				DataGroupColumnEntity dataGroupColumnEntity = new DataGroupColumnEntity();
				dataGroupColumnEntity.setFieldCode(groupColumn.getFieldCode());
				dataGroupColumnEntity.setValues(groupColumn.getValues());
				dataGroupColumnEntities.add(dataGroupColumnEntity);
			}
			persistConfig.setGroupColumns(dataGroupColumnEntities);
		}

		return persistConfig;
	}

	private void setDataGroupStatusForBuild(DataGroupRequestEntity request) {
		DataGroupRequestEntity dataGroupRequest = this.queryFactory.query(DataGroupRequestQuery.class).status(DataGroupRequestStatus.PENDING, DataGroupRequestStatus.COMPLETED).groupHashes(request.getGroupHash()).first();
		if (dataGroupRequest == null) {
			CreateDataGroupScheduledEventData scheduledEventData = new CreateDataGroupScheduledEventData();
			scheduledEventData.setDataGroupRequestId(request.getId());
			ScheduledEventPublishData publishData = new ScheduledEventPublishData();
			publishData.setType(ScheduledEventType.BUILD_DATA_GROUP);
			publishData.setKey(request.getGroupHash());
			publishData.setKeyType(DataGroupRequestEntity._groupHash);
			publishData.setCreatorId(this.userScope.getUserIdSafe());
			publishData.setRunAt(Instant.now());
			publishData.setData(this.jsonHandlingService.toJsonSafe(scheduledEventData));
			this.scheduledEventManageService.publishAsync(publishData);
			request.setStatus(DataGroupRequestStatus.PENDING);
		} else {
			request.setStatus(dataGroupRequest.getStatus());
		}
	}

	public boolean buildGroup(DataGroupRequestEntity request) throws InvalidApplicationException {
		return this.buildGroupInternal(request); //TODO: Add logic for external build
	}

	private boolean buildGroupInternal(DataGroupRequestEntity request) throws InvalidApplicationException {
		DataGroupRequestConfigEntity config = this.jsonHandlingService.fromJsonSafe(DataGroupRequestConfigEntity.class, request.getConfig());
		IndicatorGroupEntity indicatorGroupEntity = this.indicatorGroupService.getIndicatorGroups().stream().filter(x -> x.getId().equals(config.getIndicatorGroupId())).findFirst().orElse(null);
		if (indicatorGroupEntity == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{config.getIndicatorGroupId(), IndicatorGroupEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		for (UUID indicatorId : indicatorGroupEntity.getIndicatorIds()) {
			this.deleteOldGroupValues(request, indicatorId);

			IndicatorConfigItem indicatorConfigItem = this.indicatorsConfigService.getConfig(indicatorId);
			List<String> columnsForDataGroupItemKey = this.getColumnsForDataGroupItemKey(config, indicatorConfigItem);


			HashMap<String, List<IndicatorPointEntity>> indicatorPointEntityByKey = this.collectItemsToBuildDataGroup(indicatorId, config, indicatorConfigItem, columnsForDataGroupItemKey);

			DataGroupInfoEntity dataGroupInfoEntity = this.buildDataGroupInfoEntity(config);

			List<IndicatorPointEntity> groupedEntries = new ArrayList<>();
			for (Map.Entry<String, List<IndicatorPointEntity>> entry : indicatorPointEntityByKey.entrySet()) {
				if (entry.getValue() != null && !entry.getValue().isEmpty()) {
					IndicatorPointEntity grouped = this.createDataGroupItemForIndicatorPointEntities(request, indicatorId, indicatorConfigItem, dataGroupInfoEntity, columnsForDataGroupItemKey, entry.getValue());
					groupedEntries.add(grouped);
					if (groupedEntries.size() > 100) {
						elasticsearchTemplate.save(groupedEntries, IndexCoordinates.of(this.elasticIndicatorService.getIndexName(indicatorId)));
						groupedEntries.clear();
					}
				}
			}
			if (!groupedEntries.isEmpty()) elasticsearchTemplate.save(groupedEntries, IndexCoordinates.of(this.elasticIndicatorService.getIndexName(indicatorId)));
		}

		return true;
	}

	private void deleteOldGroupValues(DataGroupRequestEntity request, UUID indicatorId) throws InvalidApplicationException {
		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
		ElasticField groupField = new ElasticField(IndicatorPointEntity.Fields.groupHash, IndicatorPointEntity.class);
		nativeSearchQueryBuilder.withQuery(QueryBuilders.termQuery(groupField.getFieldNameWithPath(), request.getGroupHash()));
		org.springframework.data.elasticsearch.core.query.Query typedQuery = nativeSearchQueryBuilder.build();
		this.elasticsearchTemplate.delete(typedQuery, IndicatorPointEntity.class, IndexCoordinates.of(this.elasticIndicatorService.getIndexName(indicatorId)));
	}

	private List<String> getColumnsForDataGroupItemKey(DataGroupRequestConfigEntity config, IndicatorConfigItem indicatorConfigItem) {
		List<String> columnsForDataGroupItemKey = new ArrayList<>();
		for (String key : indicatorConfigItem.getExtraProps().keySet().stream().sorted().collect(Collectors.toList())) {
			FieldEntity fieldEntity = indicatorConfigItem.getExtraProps().get(key);
			if (fieldEntity.getBaseType() == IndicatorFieldBaseType.Keyword || fieldEntity.getBaseType() == IndicatorFieldBaseType.Date &&
					config.getGroupColumns().stream().filter(x -> !x.getFieldCode().equals(key)).findFirst().orElse(null) == null) {
				columnsForDataGroupItemKey.add(key);
			}
		}

		return columnsForDataGroupItemKey;
	}

	private DataGroupInfoEntity buildDataGroupInfoEntity(DataGroupRequestConfigEntity config) {
		DataGroupInfoEntity dataGroupInfoEntity = new DataGroupInfoEntity();
		List<DataGroupInfoColumnEntity> dataGroupInfoColumnEntities = new ArrayList<>();
		for (DataGroupColumnEntity groupColumn : config.getGroupColumns()) {
			DataGroupInfoColumnEntity dataGroupInfoColumnEntity = new DataGroupInfoColumnEntity();
			dataGroupInfoColumnEntity.setFieldCode(groupColumn.getFieldCode());
			dataGroupInfoColumnEntity.setValues(groupColumn.getValues());
			dataGroupInfoColumnEntities.add(dataGroupInfoColumnEntity);
		}
		dataGroupInfoEntity.setColumns(dataGroupInfoColumnEntities);

		return dataGroupInfoEntity;
	}

	private IndicatorPointQuery buildIndicatorPointQueryToBuildDataGroup(UUID indicatorId, DataGroupRequestConfigEntity config, IndicatorConfigItem indicatorConfigItem) {
		IndicatorPointQuery query = this.queryFactory.query(IndicatorPointQuery.class).indicatorIds(indicatorId);
		List<IndicatorPointKeywordFilter> indicatorPointKeywordFilters = new ArrayList<>();
		for (DataGroupColumnEntity groupColumn : config.getGroupColumns()) {
			FieldEntity fieldEntity = indicatorConfigItem.getExtraProps().get(groupColumn.getFieldCode());
			if (fieldEntity == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{groupColumn.getFieldCode(), FieldEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
			if (fieldEntity.getBaseType() != IndicatorFieldBaseType.Keyword) throw new MyApplicationException("Not supported " + fieldEntity.getBaseType() + "for grouping");
			IndicatorPointKeywordFilter indicatorPointLikeFilter = new IndicatorPointKeywordFilter();
			indicatorPointLikeFilter.setField(groupColumn.getFieldCode());
			indicatorPointLikeFilter.setValues(groupColumn.getValues());
			indicatorPointKeywordFilters.add(indicatorPointLikeFilter);
		}
		query.keywordFilters(indicatorPointKeywordFilters);
		return query;
	}

	private HashMap<String, List<IndicatorPointEntity>> collectItemsToBuildDataGroup(UUID indicatorId, DataGroupRequestConfigEntity config, IndicatorConfigItem indicatorConfigItem, List<String> columnsForDataGroupItemKey) {

		IndicatorPointQuery query = this.buildIndicatorPointQueryToBuildDataGroup(indicatorId, config, indicatorConfigItem);
		HashMap<String, List<IndicatorPointEntity>> indicatorPointEntityByKey = new HashMap<>();
		ScrollResponse<IndicatorPointEntity> indicatorPointEntityScrollResponse = query.collectWithScroll();

		do {
			if (indicatorPointEntityScrollResponse == null) break;

			for (IndicatorPointEntity indicatorPointEntity : indicatorPointEntityScrollResponse.getItems()) {
				String indicatorPointEntityKey = "";
				for (String keyColumn : columnsForDataGroupItemKey) {
					if (indicatorPointEntity.getProperties() != null) {
						Object value = indicatorPointEntity.getProperties().getOrDefault(keyColumn, null);
						if (value != null) indicatorPointEntityKey = indicatorPointEntityKey + value.toString();
					}
				}
				String indicatorPointEntityKeyHash = null;
				try {
					indicatorPointEntityKeyHash = this.cipherService.toSha1(indicatorPointEntityKey);
				} catch (NoSuchAlgorithmException e) {
					indicatorPointEntityKeyHash = indicatorPointEntityKey;
				}
				List<IndicatorPointEntity> items = indicatorPointEntityByKey.getOrDefault(indicatorPointEntityKeyHash, null);
				if (items == null) {
					items = new ArrayList<>();
					indicatorPointEntityByKey.put(indicatorPointEntityKeyHash, items);
				}
				items.add(indicatorPointEntity);
			}

			indicatorPointEntityScrollResponse = query.scroll(indicatorPointEntityScrollResponse.getScrollId());
		} while (indicatorPointEntityScrollResponse != null && !indicatorPointEntityScrollResponse.getItems().isEmpty());

		return indicatorPointEntityByKey;
	}

	private IndicatorPointEntity createDataGroupItemForIndicatorPointEntities(DataGroupRequestEntity request, UUID indicatorId, IndicatorConfigItem indicatorConfigItem, DataGroupInfoEntity dataGroupInfoEntity, List<String> columnsForDataGroupItemKey, List<IndicatorPointEntity> itemsToGroup) {
		IndicatorPointEntity grouped = new IndicatorPointEntity();
		grouped.setId(UUID.randomUUID());
		grouped.setTimestamp(Date.from(Instant.now()));
		grouped.setBatchTimestamp(Date.from(Instant.now()));
		grouped.setGroupHash(request.getGroupHash());
		grouped.setGroupInfo(dataGroupInfoEntity);
		grouped.setIndicatorId(indicatorId);

		Map<String, java.lang.Object> properties = new HashMap<>();
		for (Map.Entry<String, FieldEntity> prop : indicatorConfigItem.getExtraProps().entrySet()) {
			FieldEntity fieldEntity = indicatorConfigItem.getExtraProps().get(prop.getKey());
			if (columnsForDataGroupItemKey.stream().filter(x -> x.equals(prop.getKey())).findFirst().orElse(null) != null) {
				try {
					properties.put(prop.getKey(), itemsToGroup.get(0).getProperties().get(prop.getKey()));

				} catch (Exception e) {
					throw e;
				}
			} else {
				String finalValString = null;
				Date finalValDate = null;
				Double finalValDouble = 0.0d;
				Integer finalValInteger = 0;
				List<String> finalValKeywordArray = null;
				List<Integer> finalValIntegerArray = null;
				List<Double> finalValDoubleArray = null;
				List<IndicatorIntegerMapEntity> finalValIntegerMap = null;
				List<IndicatorDoubleMapEntity> finalValDoubleMap = null;
				for (IndicatorPointEntity pointEntity : itemsToGroup) {
					Object propValue = pointEntity.getProperties().getOrDefault(prop.getKey(), null);
					if (propValue != null) {
						switch (prop.getValue().getBaseType()) {
							case String:
							case Keyword:
								finalValString = (String) propValue;
								break;
							case Date:
								finalValDate = (Date) propValue;
								break;
							case Double:
								finalValDouble = (Double) propValue;
								break;
							case Integer:
								finalValInteger = (Integer) propValue;
								break;
							case KeywordArray: {
								List<String> typedValue = (List<String>) propValue;
								if (typedValue != null) {
									if (finalValKeywordArray == null) finalValKeywordArray = new ArrayList<>();
									finalValKeywordArray.addAll(typedValue);
								}
								break;
							}
							case IntegerArray: {
								List<Integer> typedValue = (List<Integer>) propValue;
								if (typedValue != null) {
									if (finalValIntegerArray == null) finalValIntegerArray = new ArrayList<>();
									finalValIntegerArray.addAll(typedValue);
								}
								break;
							}
							case DoubleArray: {
								List<Double> typedValue = (List<Double>) propValue;
								if (typedValue != null) {
									if (finalValDoubleArray == null) finalValDoubleArray = new ArrayList<>();
									finalValDoubleArray.addAll(typedValue);
								}
								break;
							}
							case IntegerMap: {
								List<IndicatorIntegerMapEntity> typedValue = (List<IndicatorIntegerMapEntity>) propValue;
								if (typedValue != null) {
									if (finalValIntegerMap == null) finalValIntegerMap = new ArrayList<>();
									for (IndicatorIntegerMapEntity typedValueItem : typedValue) {
										IndicatorIntegerMapEntity mappedIndicatorIntegerMapEntity = finalValIntegerMap.stream().filter(x -> x.getKey().equals(typedValueItem.getKey())).findFirst().orElse(null);
										if (mappedIndicatorIntegerMapEntity == null) {
											mappedIndicatorIntegerMapEntity = new IndicatorIntegerMapEntity();
											mappedIndicatorIntegerMapEntity.setKey(typedValueItem.getKey());
											mappedIndicatorIntegerMapEntity.setVal(0);
											finalValIntegerMap.add(mappedIndicatorIntegerMapEntity);
										}
										mappedIndicatorIntegerMapEntity.setVal(mappedIndicatorIntegerMapEntity.getVal() + typedValueItem.getVal());
									}
								}
								break;
							}
							case DoubleMap:
								List<IndicatorDoubleMapEntity> typedValue = (List<IndicatorDoubleMapEntity>) propValue;
								if (typedValue != null) {
									if (finalValIntegerMap == null) finalValIntegerMap = new ArrayList<>();
									for (IndicatorDoubleMapEntity typedValueItem : typedValue) {
										IndicatorDoubleMapEntity mappedIndicatorDoubleMapEntity = finalValDoubleMap.stream().filter(x -> x.getKey().equals(typedValueItem.getKey())).findFirst().orElse(null);
										if (mappedIndicatorDoubleMapEntity == null) {
											mappedIndicatorDoubleMapEntity = new IndicatorDoubleMapEntity();
											mappedIndicatorDoubleMapEntity.setKey(typedValueItem.getKey());
											mappedIndicatorDoubleMapEntity.setVal(0d);
											finalValDoubleMap.add(mappedIndicatorDoubleMapEntity);
										}
										mappedIndicatorDoubleMapEntity.setVal(mappedIndicatorDoubleMapEntity.getVal() + typedValueItem.getVal());
									}
								}
								break;
							default:
								throw new MyApplicationException("invalid type " + prop.getValue().getBaseType());
						}
					}
				}

				switch (prop.getValue().getBaseType()) {
					case String:
					case Keyword:
						properties.put(prop.getKey(), finalValString);
						break;
					case Date:
						properties.put(prop.getKey(), finalValDate);
						break;
					case Double:
						properties.put(prop.getKey(), finalValDouble);
						break;
					case Integer:
						properties.put(prop.getKey(), finalValInteger);
						break;
					case KeywordArray:
						properties.put(prop.getKey(), finalValKeywordArray);
						break;
					case IntegerArray:
						properties.put(prop.getKey(), finalValIntegerArray);
						break;
					case DoubleArray:
						properties.put(prop.getKey(), finalValDoubleArray);
						break;
					case IntegerMap:
						properties.put(prop.getKey(), finalValIntegerMap);
						break;
					case DoubleMap:
						properties.put(prop.getKey(), finalValDoubleMap);
						break;
					default:
						throw new MyApplicationException("invalid type " + prop.getValue().getBaseType());
				}
			}
		}

		grouped.setProperties(properties);
		return grouped;
	}

	private String computeGroupHash(DataGroupRequestConfigEntity config) throws NoSuchAlgorithmException {
		StringBuilder hashText = new StringBuilder();
		hashText.append(config.getIndicatorGroupId().toString().toLowerCase(Locale.ROOT));
		if (config.getGroupColumns() != null) {
			List<DataGroupColumnEntity> dataGroupColumnEntities = new ArrayList<>();
			var groupColumns = config.getGroupColumns();
			Collections.sort(groupColumns, (o1, o2) -> o1.getFieldCode().compareTo(o2.getFieldCode()));

			for (DataGroupColumnEntity groupColumn : groupColumns) {
				hashText.append(groupColumn.getFieldCode());

				String[] values = groupColumn.getValues().stream().distinct().collect(Collectors.toList()).toArray(new String[0]);
				Arrays.sort(values);

				for (String value : values) hashText.append(value);
			}
		}
		String base64String = this.cipherService.toBase64Safe(hashText.toString().getBytes(StandardCharsets.UTF_8));
		return this.cipherService.toSha512(base64String);
	}

	private void canSetStatusForce(DataGroupRequestStatus status, DataGroupRequestEntity data) {
		switch (status) {
			case PENDING:
			case NEW:
				this.authorizationService.authorizeForce(Permission.CreateDataGroupRequest);
				break;
			case ERROR:
			case COMPLETED:
				this.authorizationService.authorizeForce(Permission.EditDataGroupRequest);
				break;
			default:
				throw new MyApplicationException("invalid type " + status);
		}
	}

	private void canEditStatusForce(DataGroupRequestEntity data) {
		switch (data.getStatus()) {
			case PENDING:
				this.authorizationService.authorizeForce(Permission.EditDataGroupRequest);
				break;
			case NEW:
				this.authorizationService.authorizeForce(Permission.CreateDataGroupRequest);
				break;
			case COMPLETED:
			case ERROR:
				this.authorizationService.authorizeForce(Permission.EditDataGroupRequest);
				break;
			default:
				throw new MyApplicationException("invalid type " + data.getStatus());
		}
	}

	public void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException {
		logger.debug("deleting dataset: {}", id);

		this.authorizationService.authorize(Permission.DeleteDataGroupRequest);

		this.deleterFactory.deleter(DataGroupRequestDeleter.class).deleteAndSaveByIds(List.of(id));
	}

}
