package gr.cite.intelcomp.stiviewer.service.dataaccessrequest;


import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.DataAccessRequestStatus;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationContactType;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.common.types.dataaccessrequest.DataAccessRequestConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.dataaccessrequest.DataAccessRequestFilterColumnEntity;
import gr.cite.intelcomp.stiviewer.common.types.dataaccessrequest.DataAccessRequestIndicatorConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.dataaccessrequest.DataAccessRequestIndicatorGroupConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatoraccess.FilterColumnConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatoraccess.IndicatorAccessConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupAccessColumnConfigItemViewEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupAccessColumnConfigViewEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupAccessConfigViewEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupEntity;
import gr.cite.intelcomp.stiviewer.common.types.notification.*;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.*;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.integrationevent.outbox.notification.NotificationIntegrationEvent;
import gr.cite.intelcomp.stiviewer.integrationevent.outbox.notification.NotificationIntegrationEventHandler;
import gr.cite.intelcomp.stiviewer.model.IndicatorAccess;
import gr.cite.intelcomp.stiviewer.model.builder.dataaccessrequest.DataAccessRequestBuilder;
import gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequest;
import gr.cite.intelcomp.stiviewer.model.persist.IndicatorAccessPersist;
import gr.cite.intelcomp.stiviewer.model.persist.dataaccessrequest.*;
import gr.cite.intelcomp.stiviewer.model.persist.indicatoraccess.FilterColumnConfigPersist;
import gr.cite.intelcomp.stiviewer.model.persist.indicatoraccess.IndicatorAccessConfigPersist;
import gr.cite.intelcomp.stiviewer.query.DataAccessRequestQuery;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.util.*;
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
    private final NotificationIntegrationEventHandler eventHandler;
    private final DataAccessRequestProperties config;

    @PersistenceContext
    private EntityManager globalEntityManager;

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
            IndicatorGroupService indicatorGroupService,
            NotificationIntegrationEventHandler eventHandler,
            DataAccessRequestProperties config
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
        this.eventHandler = eventHandler;
        this.config = config;
    }

    public DataAccessRequest persist(DataAccessRequestPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting Data Access Request").And("model", model).And("fields", fields));

        this.authorizationService.authorizeForce(Permission.EditDataAccessRequest);

        Boolean isUpdate = this.conventionService.isValidGuid(model.getId());

        DataAccessRequestEntity data;
        if (isUpdate) {
            data = this.entityManager.find(DataAccessRequestEntity.class, model.getId());
            if (data == null)
                throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), DataAccessRequest.class.getSimpleName()}, LocaleContextHolder.getLocale()));
            if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash()))
                throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
        } else {
            data = new DataAccessRequestEntity();
            data.setId(UUID.randomUUID());
            data.setCreatedAt(Instant.now());
            data.setUserId(this.userScope.getUserId());
            data.setStatus(DataAccessRequestStatus.NEW);
        }
        boolean statusChanged = model.getStatus() != data.getStatus();

        if (model.getStatus() != DataAccessRequestStatus.NEW && model.getStatus() != DataAccessRequestStatus.SUBMITTED)
            throw new MyApplicationException("Operation not supported");

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
            boolean shouldNotifyForApprove = false;
            boolean shouldNotifyForReject = false;

            if (shouldChange) {
                TenantEntity tenant = this.entityManager.find(TenantEntity.class, data.getTenantId());
                this.tenantScope.setTempTenant(this.globalEntityManager, tenant.getId());
            }

            if (statusChanged && data.getStatus() == DataAccessRequestStatus.APPROVED) {
                this.saveIndicatorAccessEntity(data);
                shouldNotifyForApprove = true;
            }

            if (statusChanged && data.getStatus() == DataAccessRequestStatus.REJECTED) {
                shouldNotifyForReject = true;
            }

            if (isUpdate) this.entityManager.merge(data);
            else this.entityManager.persist(data);

            this.entityManager.flush();

            if (shouldNotifyForApprove) this.notifyForApprove(data);
            if (shouldNotifyForReject) this.notifyForReject(data);
        } finally {
            if (shouldChange) this.tenantScope.removeTempTenant(this.globalEntityManager);
        }

        this.entityManager.flush();

        return this.builderFactory.builder(DataAccessRequestBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, DataAccessRequest._id, DataAccessRequest._hash), data);
    }

    private void notifyForApprove(DataAccessRequestEntity data) throws InvalidApplicationException {
        NotificationIntegrationEvent eventEmail = this.createNotificationForApprove(data);
        eventEmail.setContactTypeHint(NotificationContactType.EMAIL);
        eventHandler.handle(eventEmail);

        NotificationIntegrationEvent eventInApp = this.createNotificationForApprove(data);
        eventInApp.setContactTypeHint(NotificationContactType.IN_APP);
        eventHandler.handle(eventInApp);
    }

    private NotificationIntegrationEvent createNotificationForApprove(DataAccessRequestEntity data) throws InvalidApplicationException {
        UserEntity user = this.entityManager.find(UserEntity.class, data.getUserId());
        if (user == null)
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{data.getUserId(), UserEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));

        NotificationIntegrationEvent event = new NotificationIntegrationEvent();
        event.setTenant(tenantScope.getTenant());
        event.setUserId(data.getUserId());

        event.setContactTypeHint(NotificationContactType.EMAIL);
        event.setNotificationType(this.config.getDataAccessRequestApprovedNotificationKey());
        NotificationFieldData fieldData = new NotificationFieldData();
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        fieldInfoList.add(new FieldInfo(this.config.getDataAccessRequestApprovedTemplate().getName(), DataType.String, user.getLastName() + " " + user.getFirstName()));
        fieldInfoList.add(new FieldInfo(this.config.getDataAccessRequestApprovedTemplate().getTenantCode(), DataType.String, tenantScope.getTenantCode()));
        fieldData.setFields(fieldInfoList);
        event.setData(jsonHandlingService.toJsonSafe(fieldData));
        return event;
    }

    private void notifyForReject(DataAccessRequestEntity data) throws InvalidApplicationException {
        NotificationIntegrationEvent eventEmail = this.createNotificationForReject(data);
        eventEmail.setContactTypeHint(NotificationContactType.EMAIL);
        eventHandler.handle(eventEmail);

        NotificationIntegrationEvent eventInApp = this.createNotificationForReject(data);
        eventInApp.setContactTypeHint(NotificationContactType.IN_APP);
        eventHandler.handle(eventInApp);
    }

    private NotificationIntegrationEvent createNotificationForReject(DataAccessRequestEntity data) throws InvalidApplicationException {
        UserEntity user = this.entityManager.find(UserEntity.class, data.getUserId());
        if (user == null)
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{data.getUserId(), UserEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));

        NotificationIntegrationEvent event = new NotificationIntegrationEvent();
        event.setTenant(tenantScope.getTenant());
        event.setUserId(data.getUserId());

        event.setContactTypeHint(NotificationContactType.EMAIL);
        event.setNotificationType(this.config.getDataAccessRequestRejectedNotificationKey());
        NotificationFieldData fieldData = new NotificationFieldData();
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        fieldInfoList.add(new FieldInfo(this.config.getDataAccessRequestRejectedTemplate().getName(), DataType.String, user.getLastName() + " " + user.getFirstName()));
        fieldInfoList.add(new FieldInfo(this.config.getDataAccessRequestRejectedTemplate().getTenantCode(), DataType.String, tenantScope.getTenantCode()));
        fieldData.setFields(fieldInfoList);
        event.setData(jsonHandlingService.toJsonSafe(fieldData));
        return event;
    }

    private DataAccessRequestConfigEntity mapToDataAccessRequestConfig(DataAccessRequestConfigPersist config) {
        if (config == null) return null;
        DataAccessRequestConfigEntity persistConfig = new DataAccessRequestConfigEntity();
        if (config.getIndicators() != null && !config.getIndicators().isEmpty()) {
            List<DataAccessRequestIndicatorConfigEntity> indicators = new ArrayList<>();
            for (DataAccessRequestIndicatorConfigPersist dataAccessRequestIndicatorConfigPersist : config.getIndicators()) {
                indicators.add(this.toEntityModel(dataAccessRequestIndicatorConfigPersist));
            }
            persistConfig.setIndicators(indicators);
        }

        if (config.getIndicatorGroups() != null && !config.getIndicatorGroups().isEmpty()) {
            List<DataAccessRequestIndicatorGroupConfigEntity> indicatorGroups = new ArrayList<>();
            for (DataAccessRequestIndicatorGroupConfigPersist dataAccessRequestIndicatorGroupConfigPersist : config.getIndicatorGroups()) {
                if (dataAccessRequestIndicatorGroupConfigPersist.isSet())
                    indicatorGroups.add(this.toEntityModel(dataAccessRequestIndicatorGroupConfigPersist));
            }
            persistConfig.setIndicatorGroups(indicatorGroups);
        }

        return persistConfig;
    }

    private DataAccessRequestIndicatorGroupConfigEntity toEntityModel(DataAccessRequestIndicatorGroupConfigPersist dataAccessRequestIndicatorGroupConfigPersist) {
        DataAccessRequestIndicatorGroupConfigEntity newConfig = new DataAccessRequestIndicatorGroupConfigEntity();
        newConfig.setGroupId(dataAccessRequestIndicatorGroupConfigPersist.getGroupId());
        if (dataAccessRequestIndicatorGroupConfigPersist.getFilterColumns() != null && !dataAccessRequestIndicatorGroupConfigPersist.getFilterColumns().isEmpty()) {
            newConfig.setFilterColumns(new ArrayList<>());
            for (gr.cite.intelcomp.stiviewer.model.persist.dataaccessrequest.FilterColumnConfigPersist filterColumnConfigPersist : dataAccessRequestIndicatorGroupConfigPersist.getFilterColumns()) {
                newConfig.getFilterColumns().add(this.toEntityModel(filterColumnConfigPersist));
            }
        }
        return newConfig;
    }

    private DataAccessRequestIndicatorConfigEntity toEntityModel(DataAccessRequestIndicatorConfigPersist dataAccessRequestIndicatorConfigPersist) {
        DataAccessRequestIndicatorConfigEntity newConfig = new DataAccessRequestIndicatorConfigEntity();
        newConfig.setId(dataAccessRequestIndicatorConfigPersist.getId());
        if (dataAccessRequestIndicatorConfigPersist.getFilterColumns() != null && !dataAccessRequestIndicatorConfigPersist.getFilterColumns().isEmpty()) {
            newConfig.setFilterColumns(new ArrayList<>());
            for (gr.cite.intelcomp.stiviewer.model.persist.dataaccessrequest.FilterColumnConfigPersist filterColumnConfigPersist : dataAccessRequestIndicatorConfigPersist.getFilterColumns()) {
                newConfig.getFilterColumns().add(this.toEntityModel(filterColumnConfigPersist));
            }
        }
        return newConfig;
    }

    private DataAccessRequestFilterColumnEntity toEntityModel(gr.cite.intelcomp.stiviewer.model.persist.dataaccessrequest.FilterColumnConfigPersist filterColumnConfigPersist) {
        DataAccessRequestFilterColumnEntity columnConfig = new DataAccessRequestFilterColumnEntity();
        columnConfig.setColumn(filterColumnConfigPersist.getColumn());
        columnConfig.setValues(filterColumnConfigPersist.getValues());
        return columnConfig;
    }


    @Override
    public DataAccessRequest persist(DataAccessRequestStatusPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting data access request status").And("model", model).And("fields", fields));

        Boolean isUpdate = this.conventionService.isValidGuid(model.getId());

        DataAccessRequestEntity data;
        if (isUpdate) {
            data = this.entityManager.find(DataAccessRequestEntity.class, model.getId());
            if (data == null)
                throw new MyNotFoundException(messageSource.getMessage("Gene ral_ItemNotFound", new Object[]{model.getId(), DataAccessRequest.class}, LocaleContextHolder.getLocale()));
            if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash()))
                throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
        } else {
            throw new MyApplicationException("Operation not supported");
        }

        if (model.getStatus() == DataAccessRequestStatus.WITHDRAWN) {
            if (!this.userScope.getUserIdSafe().equals(data.getUserId()))
                throw new MyForbiddenException("Access is denied");
        } else {
            this.canEditStatusForce(data);
        }
        this.canSetStatusForce(model.getStatus(), data);

        boolean statusChanged = model.getStatus() != data.getStatus();

        data.setStatus(model.getStatus());
        data.setUpdatedAt(Instant.now());

        boolean shouldChange = this.shouldChangeTenant(model.getStatus());

        try {
            boolean shouldNotifyForApprove = false;
            boolean shouldNotifyForReject = false;

            if (shouldChange) {
                TenantEntity tenant = this.entityManager.find(TenantEntity.class, data.getTenantId());
                this.tenantScope.setTempTenant(this.globalEntityManager, tenant.getId());
            }

            if (statusChanged && data.getStatus() == DataAccessRequestStatus.APPROVED) {
                this.saveIndicatorAccessEntity(data);
                shouldNotifyForApprove = true;
            }

            if (statusChanged && data.getStatus() == DataAccessRequestStatus.REJECTED) {
                shouldNotifyForReject = true;
            }

            this.entityManager.merge(data);

            this.entityManager.flush();

            if (shouldNotifyForApprove) this.notifyForApprove(data);
            if (shouldNotifyForReject) this.notifyForReject(data);

        } finally {
            if (shouldChange) this.tenantScope.removeTempTenant(this.globalEntityManager);
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
        DataAccessRequestConfigEntity dataAccessRequestConfigEntity = this.jsonHandlingService.fromJsonSafe(DataAccessRequestConfigEntity.class, request.getConfig());
        if (dataAccessRequestConfigEntity == null)
            throw new MyValidationException(this.errors.getConfigRequired().getCode(), this.errors.getConfigRequired().getMessage());
        if ((dataAccessRequestConfigEntity.getIndicators() == null || dataAccessRequestConfigEntity.getIndicators().isEmpty()) && (dataAccessRequestConfigEntity.getIndicatorGroups() == null || dataAccessRequestConfigEntity.getIndicatorGroups().isEmpty()))
            throw new MyValidationException(this.errors.getConfigIndicatorsRequired().getCode(), this.errors.getConfigIndicatorsRequired().getMessage());

        if (dataAccessRequestConfigEntity.getIndicators() != null && !dataAccessRequestConfigEntity.getIndicators().isEmpty()) {
            for (DataAccessRequestIndicatorConfigEntity config : dataAccessRequestConfigEntity.getIndicators()) {
                this.persistIndicatorAccessConfig(config.getId(), null, config.getFilterColumns());
            }
        }

        if (dataAccessRequestConfigEntity.getIndicatorGroups() != null && !dataAccessRequestConfigEntity.getIndicatorGroups().isEmpty()) {
            List<IndicatorGroupEntity> indicatorGroupEntities = this.indicatorGroupService.getIndicatorGroups();
            for (DataAccessRequestIndicatorGroupConfigEntity dataAccessRequestIndicatorGroupConfigEntity : dataAccessRequestConfigEntity.getIndicatorGroups()) {
                IndicatorGroupEntity indicatorGroupEntity = indicatorGroupEntities.stream().filter(x -> x.getId().equals(dataAccessRequestIndicatorGroupConfigEntity.getGroupId())).findFirst().orElse(null);
                if (indicatorGroupEntity == null)
                    throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{dataAccessRequestIndicatorGroupConfigEntity.getGroupId(), IndicatorGroupEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
                if (indicatorGroupEntity.getIndicatorIds() != null && !indicatorGroupEntity.getIndicatorIds().isEmpty()) {
                    for (UUID indicatorId : indicatorGroupEntity.getIndicatorIds()) {
                        this.persistIndicatorAccessConfig(indicatorId, indicatorGroupEntity.getId(), dataAccessRequestIndicatorGroupConfigEntity.getFilterColumns());
                    }
                }
            }
        }
    }

    private void persistIndicatorAccessConfig(UUID indicatorId, UUID groupId, List<DataAccessRequestFilterColumnEntity> filterColumns) {
        IndicatorAccessEntity indicatorAccessEntity = this.queryFactory.query(IndicatorAccessQuery.class).indicatorIds(indicatorId).isActive(IsActive.ACTIVE).hasUser(false).first();

        IndicatorAccessPersist persist = new IndicatorAccessPersist();
        persist.setIndicatorId(indicatorId);
        IndicatorAccessConfigEntity existingIndicatorAccessConfigEntity = null;
        if (indicatorAccessEntity != null) {
            persist.setId(indicatorAccessEntity.getId());
            persist.setHash(this.conventionService.hashValue(indicatorAccessEntity.getUpdatedAt()));
            existingIndicatorAccessConfigEntity = this.jsonHandlingService.fromJsonSafe(IndicatorAccessConfigEntity.class, indicatorAccessEntity.getConfig());
        }

        existingIndicatorAccessConfigEntity = this.mergeIndicatorAccessConfig(filterColumns, groupId, existingIndicatorAccessConfigEntity);
        persist.setConfig(this.buildIndicatorAccessConfigPersist(existingIndicatorAccessConfigEntity));
        validation.validateForce(persist);
        try {
            this.indicatorAccessService.persist(persist, null);
        } catch (InvalidApplicationException e) {
            throw new RuntimeException(e);
        }
    }

    private IndicatorAccessConfigPersist buildIndicatorAccessConfigPersist(IndicatorAccessConfigEntity indicatorAccessConfigEntity) {
        if (indicatorAccessConfigEntity == null || indicatorAccessConfigEntity.getAllFilterColumns() == null || indicatorAccessConfigEntity.getAllFilterColumns().isEmpty())
            return null;
        IndicatorAccessConfigPersist indicatorAccessConfigPersist = new IndicatorAccessConfigPersist();

        if (indicatorAccessConfigEntity.getGlobalFilterColumns() != null && !indicatorAccessConfigEntity.getGlobalFilterColumns().isEmpty()) {
            List<FilterColumnConfigPersist> filterColumnConfigPersists = new ArrayList<>();
            for (FilterColumnConfigEntity columnConfig : indicatorAccessConfigEntity.getGlobalFilterColumns()) {
                filterColumnConfigPersists.add(this.toPersistModel(columnConfig));
            }
            indicatorAccessConfigPersist.setGlobalFilterColumns(filterColumnConfigPersists);
        } else {
            indicatorAccessConfigPersist.setGlobalFilterColumns(null);
        }

        if (indicatorAccessConfigEntity.getGroupFilterColumns() != null && !indicatorAccessConfigEntity.getGroupFilterColumns().isEmpty()) {
            Map<UUID, List<FilterColumnConfigPersist>> persistMap = new HashMap<>();
            for (UUID groupId : indicatorAccessConfigEntity.getGroupFilterColumns().keySet()) {
                List<FilterColumnConfigEntity> filterColumnConfigEntitiesToPersist = indicatorAccessConfigEntity.getGroupFilterColumns().get(groupId);
                if (filterColumnConfigEntitiesToPersist != null && !filterColumnConfigEntitiesToPersist.isEmpty()) {
                    List<FilterColumnConfigPersist> filterColumnConfigPersists = new ArrayList<>();
                    for (FilterColumnConfigEntity columnConfig : filterColumnConfigEntitiesToPersist) {
                        filterColumnConfigPersists.add(this.toPersistModel(columnConfig));
                    }
                    persistMap.put(groupId, filterColumnConfigPersists);
                }
            }
            indicatorAccessConfigPersist.setGroupFilterColumns(persistMap);
        } else {
            indicatorAccessConfigPersist.setGroupFilterColumns(null);
        }

        return indicatorAccessConfigPersist;
    }

    private FilterColumnConfigPersist toPersistModel(FilterColumnConfigEntity columnConfig) {
        FilterColumnConfigPersist filterColumnConfigPersist = new FilterColumnConfigPersist();
        filterColumnConfigPersist.setColumn(columnConfig.getColumn());
        filterColumnConfigPersist.setValues(columnConfig.getValues());
        return filterColumnConfigPersist;
    }

    private IndicatorAccessConfigEntity mergeIndicatorAccessConfig(List<DataAccessRequestFilterColumnEntity> filterColumns, UUID groupId, IndicatorAccessConfigEntity existingConfig) {
        if (filterColumns == null || filterColumns.isEmpty()) return existingConfig;
        boolean isGlobalFilters = groupId == null;
        if (existingConfig == null) existingConfig = new IndicatorAccessConfigEntity();
        if (isGlobalFilters && existingConfig.getGlobalFilterColumns() == null) {
            if (existingConfig.getGlobalFilterColumns() == null)
                existingConfig.setGlobalFilterColumns(new ArrayList<>());
            List<FilterColumnConfigEntity> filterColumnConfigEntitiesToUse = existingConfig.getGlobalFilterColumns();
            this.mergeFilterColumns(filterColumns, filterColumnConfigEntitiesToUse);
            existingConfig.setGlobalFilterColumns(!filterColumnConfigEntitiesToUse.isEmpty() ? filterColumnConfigEntitiesToUse : null);
        }
        if (!isGlobalFilters) {
            if (existingConfig.getGroupFilterColumns() == null) existingConfig.setGroupFilterColumns(new HashMap<>());
            List<FilterColumnConfigEntity> filterColumnConfigEntitiesToUse = existingConfig.getGroupFilterColumns().getOrDefault(groupId, new ArrayList<>());
            filterColumnConfigEntitiesToUse = this.mergeFilterColumns(filterColumns, filterColumnConfigEntitiesToUse);
            existingConfig.getGroupFilterColumns().put(groupId, filterColumnConfigEntitiesToUse != null && !filterColumnConfigEntitiesToUse.isEmpty() ? filterColumnConfigEntitiesToUse : null);
        }
        return existingConfig;
    }


    private List<FilterColumnConfigEntity> mergeFilterColumns(List<DataAccessRequestFilterColumnEntity> filterColumns, List<FilterColumnConfigEntity> filterColumnConfigEntitiesToUse) {
        for (DataAccessRequestFilterColumnEntity columnConfig : filterColumns) {
            FilterColumnConfigEntity existingColumnConfig = filterColumnConfigEntitiesToUse.stream().filter(x -> x.getColumn().equals(columnConfig.getColumn())).findFirst().orElse(null);
            if (existingColumnConfig != null && existingColumnConfig.getValues() == null && existingColumnConfig.getValues().isEmpty())
                continue;
            if (existingColumnConfig == null) {
                existingColumnConfig = new FilterColumnConfigEntity();
                existingColumnConfig.setColumn(columnConfig.getColumn());
                existingColumnConfig.setValues(new ArrayList<>());
                filterColumnConfigEntitiesToUse.add(existingColumnConfig);
            }
            if (columnConfig.getValues() != null && !columnConfig.getValues().isEmpty()) {
                existingColumnConfig.getValues().addAll(columnConfig.getValues());
                existingColumnConfig.setValues(existingColumnConfig.getValues().stream().distinct().collect(Collectors.toList()));
            } else {
                existingColumnConfig.setValues(null);
            }
        }
        return filterColumnConfigEntitiesToUse.stream().filter(x -> x.getValues() != null && !x.getValues().isEmpty()).collect(Collectors.toList());
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
                if (!this.userScope.getUserIdSafe().equals(data.getUserId()))
                    throw new MyForbiddenException("Access is denied"); //TODO Owner permission
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

    @Override
    public IndicatorGroupAccessConfigViewEntity getIndicatorGroupAccessConfigViewEntity(String code) {
        IndicatorGroupEntity indicatorGroupEntity = this.indicatorGroupService.getIndicatorGroups().stream().filter(x -> x.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
        if (indicatorGroupEntity == null)
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{code, IndicatorGroupEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
        IndicatorGroupAccessConfigViewEntity indicatorGroupAccessConfigViewEntity = new IndicatorGroupAccessConfigViewEntity();
        indicatorGroupAccessConfigViewEntity.setId(indicatorGroupEntity.getId());
        indicatorGroupAccessConfigViewEntity.setFilterColumns(new ArrayList<>());

        this.addColumnAccessFromIndicatorAccessEntity(indicatorGroupEntity, indicatorGroupAccessConfigViewEntity);
        this.addColumnAccessFromDataAccessRequestEntity(indicatorGroupEntity, indicatorGroupAccessConfigViewEntity);

        return indicatorGroupAccessConfigViewEntity;
    }

    private void addColumnAccessFromIndicatorAccessEntity(IndicatorGroupEntity indicatorGroupEntity, IndicatorGroupAccessConfigViewEntity indicatorGroupAccessConfigViewEntity) {
        List<IndicatorAccessEntity> indicatorAccesses = this.queryFactory.query(IndicatorAccessQuery.class).isActive(IsActive.ACTIVE).hasUser(false).indicatorIds(indicatorGroupEntity.getIndicatorIds())
                .collectAs(new BaseFieldSet().ensure(IndicatorAccess._config).ensure(IndicatorAccess._indicator));
        for (IndicatorAccessEntity indicatorAccess : indicatorAccesses) {
            IndicatorAccessConfigEntity indicatorAccessConfig = this.jsonHandlingService.fromJsonSafe(IndicatorAccessConfigEntity.class, indicatorAccess.getConfig());
            if (indicatorAccessConfig != null && indicatorAccessConfig.getGroupFilterColumns().containsKey(indicatorGroupEntity.getId())) {
                List<FilterColumnConfigEntity> filterColumnConfigEntities = indicatorAccessConfig.getGroupFilterColumns().get(indicatorGroupEntity.getId());
                if (filterColumnConfigEntities != null && !filterColumnConfigEntities.isEmpty()) {
                    for (FilterColumnConfigEntity filterColumnConfig : filterColumnConfigEntities) {
                        IndicatorGroupAccessColumnConfigViewEntity portofolioColumnAccess = indicatorGroupAccessConfigViewEntity.getFilterColumns().stream().filter(x -> x.getCode().equalsIgnoreCase(filterColumnConfig.getColumn())).findFirst().orElse(null);
                        if (portofolioColumnAccess == null) {
                            portofolioColumnAccess = new IndicatorGroupAccessColumnConfigViewEntity();
                            portofolioColumnAccess.setCode(filterColumnConfig.getColumn());
                            indicatorGroupAccessConfigViewEntity.getFilterColumns().add(portofolioColumnAccess);
                        }
                        if (filterColumnConfig.getValues() != null && !filterColumnConfig.getValues().isEmpty()) {
                            if (portofolioColumnAccess.getItems() == null)
                                portofolioColumnAccess.setItems(new ArrayList<>());
                            for (String value : filterColumnConfig.getValues()) {
                                if (!this.conventionService.isNullOrEmpty(value) && portofolioColumnAccess.getItems().stream().noneMatch(x -> x.getValue().equalsIgnoreCase(value))) {
                                    IndicatorGroupAccessColumnConfigItemViewEntity groupAccessColumnConfigItemViewEntity = new IndicatorGroupAccessColumnConfigItemViewEntity();
                                    groupAccessColumnConfigItemViewEntity.setValue(value);
                                    groupAccessColumnConfigItemViewEntity.setStatus(DataAccessRequestStatus.APPROVED);
                                    portofolioColumnAccess.getItems().add(groupAccessColumnConfigItemViewEntity);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void addColumnAccessFromDataAccessRequestEntity(IndicatorGroupEntity indicatorGroupEntity, IndicatorGroupAccessConfigViewEntity indicatorGroupAccessConfigViewEntity) {
        List<DataAccessRequestEntity> dataAccessRequestEntities = this.queryFactory.query(DataAccessRequestQuery.class).statuses(DataAccessRequestStatus.NEW, DataAccessRequestStatus.SUBMITTED, DataAccessRequestStatus.IN_PROCESS, DataAccessRequestStatus.REJECTED, DataAccessRequestStatus.WITHDRAWN)
                .collectAs(new BaseFieldSet().ensure(DataAccessRequest._config).ensure(DataAccessRequest._updatedAt).ensure(DataAccessRequest._config).ensure(DataAccessRequest._status));
        dataAccessRequestEntities = dataAccessRequestEntities.stream().sorted(Comparator.comparing(DataAccessRequestEntity::getUpdatedAt).reversed()).collect(Collectors.toList());
        for (DataAccessRequestEntity dataAccessRequestEntity : dataAccessRequestEntities) {
            DataAccessRequestConfigEntity dataAccessRequestConfigEntity = this.jsonHandlingService.fromJsonSafe(DataAccessRequestConfigEntity.class, dataAccessRequestEntity.getConfig());
            List<DataAccessRequestIndicatorGroupConfigEntity> dataAccessRequestIndicatorGroupConfigEntities = dataAccessRequestConfigEntity.getIndicatorGroups().stream().filter(x -> x.getGroupId().equals(indicatorGroupEntity.getId())).collect(Collectors.toList());
            if (dataAccessRequestIndicatorGroupConfigEntities.isEmpty()) continue;

            for (DataAccessRequestIndicatorGroupConfigEntity dataAccessRequestIndicatorGroupConfigEntity : dataAccessRequestIndicatorGroupConfigEntities) {
                if (dataAccessRequestIndicatorGroupConfigEntity.getFilterColumns() != null && !dataAccessRequestIndicatorGroupConfigEntity.getFilterColumns().isEmpty()) {
                    for (DataAccessRequestFilterColumnEntity filterColumnConfig : dataAccessRequestIndicatorGroupConfigEntity.getFilterColumns()) {
                        IndicatorGroupAccessColumnConfigViewEntity portofolioColumnAccess = indicatorGroupAccessConfigViewEntity.getFilterColumns().stream().filter(x -> x.getCode().equalsIgnoreCase(filterColumnConfig.getColumn())).findFirst().orElse(null);
                        if (portofolioColumnAccess == null) {
                            portofolioColumnAccess = new IndicatorGroupAccessColumnConfigViewEntity();
                            portofolioColumnAccess.setCode(filterColumnConfig.getColumn());
                            indicatorGroupAccessConfigViewEntity.getFilterColumns().add(portofolioColumnAccess);
                        }
                        if (filterColumnConfig.getValues() != null && !filterColumnConfig.getValues().isEmpty()) {
                            if (portofolioColumnAccess.getItems() == null)
                                portofolioColumnAccess.setItems(new ArrayList<>());
                            for (String value : filterColumnConfig.getValues()) {
                                if (!this.conventionService.isNullOrEmpty(value) && portofolioColumnAccess.getItems().stream().noneMatch(x -> x.getValue().equalsIgnoreCase(value))) {
                                    IndicatorGroupAccessColumnConfigItemViewEntity groupAccessColumnConfigItemViewEntity = new IndicatorGroupAccessColumnConfigItemViewEntity();
                                    groupAccessColumnConfigItemViewEntity.setValue(value);
                                    groupAccessColumnConfigItemViewEntity.setStatus(dataAccessRequestEntity.getStatus());
                                    portofolioColumnAccess.getItems().add(groupAccessColumnConfigItemViewEntity);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
