package gr.cite.intelcomp.stiviewer.service.dataaccessrequest;


import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.DataAccessRequestStatus;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.common.types.dataaccessrequest.DataAccessRequestConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.dataaccessrequest.DataAccessRequestFilterColumnEntity;
import gr.cite.intelcomp.stiviewer.common.types.dataaccessrequest.DataAccessRequestIndicatorConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.dataaccessrequest.DataAccessRequestIndicatorGroupConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatoraccessconfig.FilterColumnConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatoraccessconfig.IndicatorAccessConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.DataAccessRequestEntity;
import gr.cite.intelcomp.stiviewer.data.IndicatorAccessEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.model.builder.dataaccessrequest.DataAccessRequestBuilder;
import gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequest;
import gr.cite.intelcomp.stiviewer.model.persist.IndicatorAccessPersist;
import gr.cite.intelcomp.stiviewer.model.persist.dataaccessrequest.DataAccessRequestConfigPersist;
import gr.cite.intelcomp.stiviewer.model.persist.dataaccessrequest.DataAccessRequestPersist;
import gr.cite.intelcomp.stiviewer.model.persist.dataaccessrequest.DataAccessRequestStatusPersist;
import gr.cite.intelcomp.stiviewer.model.persist.indicatoraccessconfig.FilterColumnConfigPersist;
import gr.cite.intelcomp.stiviewer.model.persist.indicatoraccessconfig.IndicatorAccessConfigPersist;
import gr.cite.intelcomp.stiviewer.query.IndicatorAccessQuery;
import gr.cite.intelcomp.stiviewer.service.indicatoraccess.IndicatorAccessService;
import gr.cite.intelcomp.stiviewer.service.indicatorgroup.IndicatorGroupService;
import gr.cite.intelcomp.stiviewer.service.user.UserServiceImpl;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import gr.cite.tools.validation.ValidationService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequestScope
public class DataAccessRequestServiceImpl implements DataAccessRequestService {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserServiceImpl.class));

	private final TenantEntityManager entityManager;

	private final AuthorizationService authorizationService;
	private final BuilderFactory builderFactory;
	private final ConventionService conventionService;
	private final ErrorThesaurusProperties errors;
	private final MessageSource messageSource;
	private final UserScope userScope;
	private final TenantScope tenantScope;
	private final JsonHandlingService jsonHandlingService;
	private final IndicatorAccessService indicatorAccessService;
	private final ValidationService validation;
	private final QueryFactory queryFactory;
	private final IndicatorGroupService indicatorGroupService;

	@Autowired
	public DataAccessRequestServiceImpl(
			TenantEntityManager entityManager,
			AuthorizationService authorizationService,
			BuilderFactory builderFactory,
			ConventionService conventionService,
			ErrorThesaurusProperties errors,
			MessageSource messageSource,
			UserScope userScope,
			TenantScope tenantScope,
			JsonHandlingService jsonHandlingService,
			IndicatorAccessService indicatorAccessService,
			ValidationService validation,
			QueryFactory queryFactory,
			IndicatorGroupService indicatorGroupService
	) {
		this.entityManager = entityManager;
		this.authorizationService = authorizationService;
		this.builderFactory = builderFactory;
		this.conventionService = conventionService;
		this.errors = errors;
		this.messageSource = messageSource;
		this.userScope = userScope;
		this.tenantScope = tenantScope;
		this.jsonHandlingService = jsonHandlingService;
		this.indicatorAccessService = indicatorAccessService;
		this.validation = validation;
		this.queryFactory = queryFactory;
		this.indicatorGroupService = indicatorGroupService;
	}

	public DataAccessRequest persist(DataAccessRequestPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting Data Access Request").And("model", model).And("fields", fields));

		this.authorizationService.authorizeForce(Permission.EditDataAccessRequest);

		Boolean isUpdate = this.conventionService.isValidGuid(model.getId());

		DataAccessRequestEntity data;
		if (isUpdate) {
			data = this.entityManager.find(DataAccessRequestEntity.class, model.getId());
			if (data == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), DataAccessRequest.class.getSimpleName()}, LocaleContextHolder.getLocale()));
			if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash())) throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
		} else {
			data = new DataAccessRequestEntity();
			data.setId(UUID.randomUUID());
			data.setCreatedAt(Instant.now());
			data.setUserId(this.userScope.getUserId());
			data.setStatus(DataAccessRequestStatus.NEW);
		}

		if (model.getStatus() != DataAccessRequestStatus.NEW && model.getStatus() != DataAccessRequestStatus.SUBMITTED) throw new MyApplicationException("Operation not supported");

		if (model.getStatus() == DataAccessRequestStatus.WITHDRAWN) {
			if (this.userScope.getUserIdSafe() != data.getUserId()) throw new MyForbiddenException("Access is denied");
		} else {
			this.canEditStatusForce(data);
		}
		this.canSetStatusForce(model.getStatus(), data);

		data.setConfig(jsonHandlingService.toJsonSafe(this.mapToDataAccessRequestConfig(model.getConfig())));
		data.setStatus(model.getStatus());
		data.setUpdatedAt(Instant.now());

		boolean shouldChange = this.shouldChangeTenant(model.getStatus());

		try {
			if (shouldChange) {
				TenantEntity tenant = this.entityManager.find(TenantEntity.class, data.getTenantId());
				this.tenantScope.setTempTenant(tenant.getId(), tenant.getCode());
			}

			if (isUpdate) this.entityManager.merge(data);
			else this.entityManager.persist(data);

			this.entityManager.flush();

		} finally {
			if (shouldChange) this.tenantScope.removeTempTenant();
		}

		this.entityManager.flush();

		return this.builderFactory.builder(DataAccessRequestBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, DataAccessRequest._id, DataAccessRequest._hash), data);
	}

	private DataAccessRequestConfigEntity mapToDataAccessRequestConfig(DataAccessRequestConfigPersist config) {
		if (config == null) return null;
		DataAccessRequestConfigEntity persistConfig = new DataAccessRequestConfigEntity();
		if (config.getIndicators() != null) {
			List<DataAccessRequestIndicatorConfigEntity> indicators = new ArrayList<>();
			config.getIndicators().forEach(x -> {
				DataAccessRequestIndicatorConfigEntity newConfig = new DataAccessRequestIndicatorConfigEntity();
				newConfig.setId(x.getId());
				newConfig.setFilterColumns(x.getFilterColumns().stream().map(column -> {
					DataAccessRequestFilterColumnEntity columnConfig = new DataAccessRequestFilterColumnEntity();
					columnConfig.setColumn(column.getColumn());
					columnConfig.setValues(column.getValues());
					return columnConfig;
				}).collect(Collectors.toList()));
				indicators.add(newConfig);
			});
			persistConfig.setIndicators(indicators);
		}

		if (config.getIndicatorGroups() != null) {
			List<DataAccessRequestIndicatorGroupConfigEntity> indicatorGroups = new ArrayList<>();
			config.getIndicatorGroups().forEach(x -> {
				DataAccessRequestIndicatorGroupConfigEntity newConfig = new DataAccessRequestIndicatorGroupConfigEntity();
				newConfig.setGroupId(x.getGroupId());
				newConfig.setFilterColumns(x.getFilterColumns().stream().map(column -> {
					DataAccessRequestFilterColumnEntity columnConfig = new DataAccessRequestFilterColumnEntity();
					columnConfig.setColumn(column.getColumn());
					columnConfig.setValues(column.getValues());
					return columnConfig;
				}).collect(Collectors.toList()));
				indicatorGroups.add(newConfig);
			});
			persistConfig.setIndicatorGroups(indicatorGroups);
		}

		return persistConfig;
	}

	@Override
	public DataAccessRequest persist(DataAccessRequestStatusPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting data access request status").And("model", model).And("fields", fields));

		Boolean isUpdate = this.conventionService.isValidGuid(model.getId());

		DataAccessRequestEntity data;
		if (isUpdate) {
			data = this.entityManager.find(DataAccessRequestEntity.class, model.getId());
			if (data == null) throw new MyNotFoundException(messageSource.getMessage("Gene ral_ItemNotFound", new Object[]{model.getId(), DataAccessRequest.class}, LocaleContextHolder.getLocale()));
			if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash())) throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
		} else {
			throw new MyApplicationException("Operation not supported");
		}

		if (model.getStatus() == DataAccessRequestStatus.WITHDRAWN) {
			if (!this.userScope.getUserIdSafe().equals(data.getUserId())) throw new MyForbiddenException("Access is denied");
		} else {
			this.canEditStatusForce(data);
		}
		this.canSetStatusForce(model.getStatus(), data);

		boolean statusChanged = model.getStatus() != data.getStatus();

		data.setStatus(model.getStatus());
		data.setUpdatedAt(Instant.now());

		boolean shouldChange = this.shouldChangeTenant(model.getStatus());

		try {
			if (shouldChange) {
				TenantEntity tenant = this.entityManager.find(TenantEntity.class, data.getTenantId());
				this.tenantScope.setTempTenant(tenant.getId(), tenant.getCode());
			}

			if (statusChanged && data.getStatus() == DataAccessRequestStatus.APPROVED) {
				this.saveIndicatorAccessEntity(data);
			}

			if (isUpdate) this.entityManager.merge(data);
			else this.entityManager.persist(data);

			this.entityManager.flush();

		} finally {
			if (shouldChange) this.tenantScope.removeTempTenant();
		}


		return this.builderFactory.builder(DataAccessRequestBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, DataAccessRequest._id), data);

	}

	private boolean shouldChangeTenant(DataAccessRequestStatus status) {
		boolean returnValue = false;
		switch (status) {
			case NEW:
			case WITHDRAWN:
			case SUBMITTED:
				break;
			case DELETED:
			case APPROVED:
			case IN_PROCESS:
			case REJECTED:
				returnValue = true;
				break;
			default:
				throw new MyApplicationException("invalid type " + status);
		}
		if (returnValue) this.authorizationService.authorizeForce(Permission.AllowNoTenant);
		return returnValue;
	}

	private void saveIndicatorAccessEntity(DataAccessRequestEntity request) {
		DataAccessRequestConfigEntity configEntity = this.jsonHandlingService.fromJsonSafe(DataAccessRequestConfigEntity.class, request.getConfig());
		if (configEntity == null) throw new MyValidationException(this.errors.getConfigRequired().getCode(), this.errors.getConfigRequired().getMessage());
		if (configEntity.getIndicators() == null && configEntity.getIndicatorGroups() == null) throw new MyValidationException(this.errors.getConfigIndicatorsRequired().getCode(), this.errors.getConfigIndicatorsRequired().getMessage());

		if (configEntity.getIndicators() != null) {
			for (DataAccessRequestIndicatorConfigEntity config : configEntity.getIndicators()) {
				this.persistIndicatorAccessConfig(config.getId(), config.getFilterColumns());
			}
		}

		if (configEntity.getIndicatorGroups() != null) {
			List<IndicatorGroupEntity> indicatorGroupEntities = this.indicatorGroupService.getIndicatorGroups();
			for (DataAccessRequestIndicatorGroupConfigEntity config : configEntity.getIndicatorGroups()) {
				IndicatorGroupEntity indicatorGroupEntity = indicatorGroupEntities.stream().filter(x -> x.getId().equals(config.getGroupId())).findFirst().orElse(null);
				if (indicatorGroupEntity == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{config.getGroupId(), IndicatorGroupEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
				if (indicatorGroupEntity.getIndicatorIds() != null) {
					for (UUID indicatorId : indicatorGroupEntity.getIndicatorIds()) {
						this.persistIndicatorAccessConfig(indicatorId, config.getFilterColumns());

					}
				}
			}
		}
	}

	private void persistIndicatorAccessConfig(UUID indicatorId, List<DataAccessRequestFilterColumnEntity> filterColumns) {
		IndicatorAccessEntity indicatorAccessEntity = this.queryFactory.query(IndicatorAccessQuery.class).indicatorIds(indicatorId).isActive(IsActive.ACTIVE).hasUser(false).first();

		IndicatorAccessPersist indicatorAccess = new IndicatorAccessPersist();
		indicatorAccess.setIndicatorId(indicatorId);
		IndicatorAccessConfigEntity indicatorAccessConfigEntity = null;
		if (indicatorAccessEntity != null) {
			indicatorAccess.setId(indicatorAccessEntity.getId());
			indicatorAccess.setHash(this.conventionService.hashValue(indicatorAccessEntity.getUpdatedAt()));
			indicatorAccessConfigEntity = this.jsonHandlingService.fromJsonSafe(IndicatorAccessConfigEntity.class, indicatorAccessEntity.getConfig());
		}
		if (indicatorAccessConfigEntity == null) {
			indicatorAccessConfigEntity = new IndicatorAccessConfigEntity();
		}

		indicatorAccessConfigEntity = this.mergeIndicatorAccessConfig(filterColumns, indicatorAccessConfigEntity);
		indicatorAccess.setConfig(this.buildIndicatorAccessConfigPersist(indicatorAccessConfigEntity));
		validation.validateForce(indicatorAccess);
		try {
			this.indicatorAccessService.persist(indicatorAccess, null);
		} catch (InvalidApplicationException e) {
			throw new RuntimeException(e);
		}
	}

	private IndicatorAccessConfigPersist buildIndicatorAccessConfigPersist(IndicatorAccessConfigEntity indicatorAccessConfigEntity) {
		if (indicatorAccessConfigEntity == null || indicatorAccessConfigEntity.getFilterColumns() == null || indicatorAccessConfigEntity.getFilterColumns().isEmpty()) return null;
		IndicatorAccessConfigPersist indicatorAccessConfigPersist = new IndicatorAccessConfigPersist();

		List<FilterColumnConfigPersist> filterColumnConfigPersists = new ArrayList<>();
		for (FilterColumnConfigEntity columnConfig : indicatorAccessConfigEntity.getFilterColumns()) {
			FilterColumnConfigPersist filterColumnConfigPersist = new FilterColumnConfigPersist();
			filterColumnConfigPersist.setColumn(columnConfig.getColumn());
			filterColumnConfigPersist.setValues(columnConfig.getValues());
			filterColumnConfigPersists.add(filterColumnConfigPersist);
		}
		indicatorAccessConfigPersist.setFilterColumns(filterColumnConfigPersists);

		return indicatorAccessConfigPersist;
	}

	private IndicatorAccessConfigEntity mergeIndicatorAccessConfig(List<DataAccessRequestFilterColumnEntity> filterColumns, IndicatorAccessConfigEntity existingConfig) {


		if (filterColumns == null || filterColumns.size() == 0) {
			existingConfig.setFilterColumns(null);
		} else {
			if (existingConfig == null) existingConfig = new IndicatorAccessConfigEntity();
			if (existingConfig.getFilterColumns() == null) existingConfig.setFilterColumns(new ArrayList<>());

			for (DataAccessRequestFilterColumnEntity columnConfig : filterColumns) {
				FilterColumnConfigEntity existingColumnConfig = existingConfig.getFilterColumns().stream().filter(x -> x.getColumn().equals(columnConfig.getColumn())).findFirst().orElse(null);
				if (existingColumnConfig == null) {
					existingColumnConfig = new FilterColumnConfigEntity();
					existingColumnConfig.setColumn(columnConfig.getColumn());
					existingColumnConfig.setValues(new ArrayList<>());
					existingConfig.getFilterColumns().add(existingColumnConfig);
				}
				if (columnConfig.getValues() != null && !columnConfig.getValues().isEmpty()) {
					existingColumnConfig.getValues().addAll(columnConfig.getValues());
					existingColumnConfig.setValues(existingColumnConfig.getValues().stream().distinct().collect(Collectors.toList()));
				} else {
					existingColumnConfig.setValues(null);
				}
			}
			existingConfig.setFilterColumns(existingConfig.getFilterColumns().stream().filter(x -> x.getValues() != null && !x.getValues().isEmpty()).collect(Collectors.toList()));
			if (existingConfig.getFilterColumns().isEmpty()) existingConfig.setFilterColumns(null);
		}
		return existingConfig;
	}

	private void canSetStatusForce(DataAccessRequestStatus status, DataAccessRequestEntity data) {
		switch (status) {
			case SUBMITTED:
			case NEW:
				this.authorizationService.authorizeForce(Permission.CreateDataAccessRequest);
				break;
			case DELETED:
				this.authorizationService.authorizeForce(Permission.DeleteDataAccessRequest);
				break;
			case APPROVED:
				this.authorizationService.authorizeForce(Permission.ApproveDataAccessRequest);
				break;
			case IN_PROCESS:
				this.authorizationService.authorizeForce(Permission.EditDataAccessRequest);
				break;
			case REJECTED:
				this.authorizationService.authorizeForce(Permission.RejectDataAccessRequest);
				break;
			case WITHDRAWN:
				if (!this.userScope.getUserIdSafe().equals(data.getUserId())) throw new MyForbiddenException("Access is denied"); //TODO Owner permission
				break;
			default:
				throw new MyApplicationException("invalid type " + status);
		}
	}

	private void canEditStatusForce(DataAccessRequestEntity data) {
		switch (data.getStatus()) {
			case IN_PROCESS:
			case SUBMITTED:
				this.authorizationService.authorizeForce(Permission.EditDataAccessRequest);
				break;
			case NEW:
				this.authorizationService.authorizeForce(Permission.CreateDataAccessRequest);
				break;
			case REJECTED:
			case APPROVED:
			case WITHDRAWN:
			case DELETED:
				throw new MyForbiddenException("Access is denied");
			default:
				throw new MyApplicationException("invalid type " + data.getStatus());
		}
	}
}
