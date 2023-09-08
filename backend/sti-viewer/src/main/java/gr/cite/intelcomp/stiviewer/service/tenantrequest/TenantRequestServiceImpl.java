package gr.cite.intelcomp.stiviewer.service.tenantrequest;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.enums.TenantRequestStatus;
import gr.cite.intelcomp.stiviewer.common.enums.UserContactType;
import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationContactType;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.common.types.notification.DataType;
import gr.cite.intelcomp.stiviewer.common.types.notification.FieldInfo;
import gr.cite.intelcomp.stiviewer.common.types.notification.NotificationFieldData;
import gr.cite.intelcomp.stiviewer.config.keycloak.KeycloakAuthorities;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.*;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.event.EventBroker;
import gr.cite.intelcomp.stiviewer.event.UserAddedToTenantEvent;
import gr.cite.intelcomp.stiviewer.integrationevent.outbox.notification.NotificationIntegrationEvent;
import gr.cite.intelcomp.stiviewer.integrationevent.outbox.notification.NotificationIntegrationEventHandler;
import gr.cite.intelcomp.stiviewer.model.TenantRequest;
import gr.cite.intelcomp.stiviewer.model.builder.TenantRequestBuilder;
import gr.cite.intelcomp.stiviewer.model.persist.TenantRequestPersist;
import gr.cite.intelcomp.stiviewer.model.persist.TenantRequestStatusPersist;
import gr.cite.intelcomp.stiviewer.query.UserQuery;
import gr.cite.intelcomp.stiviewer.service.keycloak.KeycloakService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequestScope
public class TenantRequestServiceImpl implements TenantRequestService {
    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantRequestServiceImpl.class));
    private final TenantEntityManager entityManager;
    private final AuthorizationService authService;
    private final BuilderFactory builderFactory;
    private final ConventionService conventionService;
    private final ErrorThesaurusProperties errors;
    private final MessageSource messageSource;
    private final UserScope userScope;
    private final TenantScope tenantScope;
    private final EventBroker eventBroker;
    private final KeycloakService keycloakService;
    private final QueryFactory queryFactory;
    private final JsonHandlingService jsonHandlingService;
    private final NotificationIntegrationEventHandler eventHandler;
    private final TenantRequestProperties config;

    @PersistenceContext
    private EntityManager globalEntityManager;

    @Autowired
    public TenantRequestServiceImpl(TenantEntityManager entityManager,
                                    AuthorizationService authService,
                                    BuilderFactory builderFactory,
                                    ConventionService conventionService,
                                    ErrorThesaurusProperties errors,
                                    MessageSource messageSource,
                                    UserScope userScope,
                                    TenantScope tenantScope,
                                    EventBroker eventBroker,
                                    KeycloakService keycloakService,
                                    QueryFactory queryFactory,
                                    JsonHandlingService jsonHandlingService,
                                    NotificationIntegrationEventHandler eventHandler,
                                    TenantRequestProperties config) {
        this.entityManager = entityManager;
        this.authService = authService;
        this.builderFactory = builderFactory;
        this.conventionService = conventionService;
        this.errors = errors;
        this.messageSource = messageSource;
        this.userScope = userScope;
        this.tenantScope = tenantScope;
        this.eventBroker = eventBroker;
        this.keycloakService = keycloakService;
        this.queryFactory = queryFactory;
        this.jsonHandlingService = jsonHandlingService;
        this.eventHandler = eventHandler;
        this.config = config;
    }

    @Override
    public TenantRequest persist(TenantRequestPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting tenant request").And("model", model).And("fields", fields));

        Boolean isUpdate = this.conventionService.isValidGuid(model.getId());
        TenantRequestEntity data;
        if (isUpdate) {
            data = this.entityManager.find(TenantRequestEntity.class, model.getId());
            if (data == null)
                throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), TenantRequest.class}, LocaleContextHolder.getLocale()));
            if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash()))
                throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
        } else {
            data = new TenantRequestEntity();
            data.setId(UUID.randomUUID());
            data.setCreatedAt(Instant.now());
            data.setForUserId(this.userScope.getUserId());
            data.setStatus(TenantRequestStatus.NEW);
        }

        if (model.getStatus() != TenantRequestStatus.NEW && model.getStatus() != TenantRequestStatus.SUBMITTED)
            throw new MyApplicationException("Operation not supported");

        if (model.getStatus() == TenantRequestStatus.WITHDRAWN) {
            if (this.userScope.getUserIdSafe() != data.getForUserId())
                throw new MyForbiddenException("Access is denied");
        } else {
            this.canEditStatusForce(data);
        }
        this.canSetStatusForce(model.getStatus(), data);

        data.setStatus(model.getStatus());
        data.setEmail(model.getEmail());
        data.setMessage(model.getMessage());
        data.setUpdatedAt(Instant.now());

        if (isUpdate)
            this.entityManager.merge(data);
        else this.entityManager.persist(data);

        this.entityManager.flush();

        return this.builderFactory.builder(TenantRequestBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, TenantRequest._id), data);

    }

    @Override
    public TenantRequest persist(TenantRequestStatusPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting tenant request status").And("model", model).And("fields", fields));

        boolean isUpdate = this.conventionService.isValidGuid(model.getId());

        TenantRequestEntity data;
        if (isUpdate) {
            data = this.entityManager.find(TenantRequestEntity.class, model.getId());
            if (data == null)
                throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), TenantRequest.class}, LocaleContextHolder.getLocale()));
            if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash()))
                throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
        } else {
            throw new MyApplicationException("Operation not supported");
        }

        if (model.getStatus() == TenantRequestStatus.WITHDRAWN) {
            if (!this.userScope.getUserIdSafe().equals(data.getForUserId()))
                throw new MyForbiddenException("Access is denied");
        } else {
            this.canEditStatusForce(data);
        }
        this.canSetStatusForce(model.getStatus(), data);

        boolean statusChanged = model.getStatus() != data.getStatus();

        data.setStatus(model.getStatus());
        data.setUpdatedAt(Instant.now());

        if (statusChanged && data.getStatus() == TenantRequestStatus.APPROVED) {
            TenantEntity tenant = this.createTenantUserEntity(data, model);
            data.setAssignedTenantId(tenant.getId());
            createKeycloakGroupsForTenant(tenant, data.getForUserId());
            this.eventBroker.emit(new UserAddedToTenantEvent(data.getForUserId(), tenant.getId()));
        }

        if (statusChanged && data.getStatus() == TenantRequestStatus.REJECTED) {
            this.notifyForReject(data);
        }

        this.entityManager.merge(data);

        this.entityManager.flush();

        return this.builderFactory.builder(TenantRequestBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, TenantRequest._id), data);

    }

    private TenantEntity createTenantUserEntity(TenantRequestEntity request, TenantRequestStatusPersist model) throws InvalidApplicationException {
        if (this.conventionService.isNullOrEmpty(model.getTenantCode()))
            throw new MyValidationException(this.errors.getTenantCodeRequired().getCode(), this.errors.getTenantCodeRequired().getMessage());

        TenantEntity tenant = new TenantEntity();
        tenant.setId(UUID.randomUUID());
        tenant.setCreatedAt(Instant.now());
        tenant.setUpdatedAt(Instant.now());
        tenant.setIsActive(IsActive.ACTIVE);
        tenant.setConfig(null);
        tenant.setCode(model.getTenantCode());
        tenant.setName(model.getTenantName());

        try {
            this.tenantScope.setTempTenant(this.globalEntityManager, tenant.getId());

            this.entityManager.persist(tenant);

            TenantUserEntity tenantUserEntity = new TenantUserEntity();
            tenantUserEntity.setId(UUID.randomUUID());
            tenantUserEntity.setCreatedAt(Instant.now());
            tenantUserEntity.setUpdatedAt(Instant.now());
            tenantUserEntity.setIsActive(IsActive.ACTIVE);
            tenantUserEntity.setUserId(request.getForUserId());
            this.entityManager.persist(tenantUserEntity);

            if (this.conventionService.isValidEmail(request.getEmail())) {
                UserContactInfoEntity userContactInfoEntity = new UserContactInfoEntity();
                userContactInfoEntity.setUserId(request.getForUserId());
                userContactInfoEntity.setCreatedAt(Instant.now());
                userContactInfoEntity.setUpdatedAt(Instant.now());
                userContactInfoEntity.setIsActive(IsActive.ACTIVE);
                userContactInfoEntity.setType(UserContactType.Email);
                userContactInfoEntity.setValue(request.getEmail());
                this.entityManager.persist(userContactInfoEntity);
            }
            this.notifyForApprove(request);
        } finally {
            this.tenantScope.removeTempTenant(this.globalEntityManager);
        }

        return tenant;
    }

    private void canSetStatusForce(TenantRequestStatus tenantRequestStatus, TenantRequestEntity data) {
        switch (tenantRequestStatus) {
            case SUBMITTED:
            case NEW:
                this.authService.authorizeForce(Permission.CreateTenantRequest);
                break;
            case DELETED:
                this.authService.authorizeForce(Permission.DeleteTenantRequest);
                break;
            case APPROVED:
                this.authService.authorizeForce(Permission.ApproveTenantRequest);
                break;
            case IN_PROCESS:
                this.authService.authorizeForce(Permission.EditTenantRequest);
                break;
            case REJECTED:
                this.authService.authorizeForce(Permission.RejectTenantRequest);
                break;
            case WITHDRAWN:
                if (!this.userScope.getUserIdSafe().equals(data.getForUserId()))
                    throw new MyForbiddenException("Access is denied"); //TODO Owner permission
                break;
            default:
                throw new MyApplicationException("invalid type " + tenantRequestStatus);
        }
    }

    private void canEditStatusForce(TenantRequestEntity data) {
        switch (data.getStatus()) {
            case IN_PROCESS:
            case SUBMITTED:
                this.authService.authorizeForce(Permission.EditTenantRequest);
                break;
            case NEW:
                this.authService.authorizeForce(Permission.CreateTenantRequest);
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

    private void createKeycloakGroupsForTenant(TenantEntity entity, UUID userId) {
        this.keycloakService.createTenantGroups(entity);
        UserQuery query = this.queryFactory.query(UserQuery.class).ids(userId);
        UserEntity user = query.first();
        this.keycloakService.addUserToTenantAuthorityGroup(UUID.fromString(user.getSubjectId()), entity, KeycloakAuthorities.ADMIN);
    }

    private void notifyForApprove(TenantRequestEntity data) throws InvalidApplicationException {
        if (this.conventionService.isValidEmail(data.getEmail())) {
            NotificationIntegrationEvent eventEmail = this.createNotificationForApprove(data);
            eventEmail.setContactTypeHint(NotificationContactType.EMAIL);
            eventHandler.handle(eventEmail);
        }
        NotificationIntegrationEvent eventInApp = this.createNotificationForApprove(data);
        eventInApp.setContactTypeHint(NotificationContactType.IN_APP);
        eventHandler.handle(eventInApp);
    }

    private NotificationIntegrationEvent createNotificationForApprove(TenantRequestEntity data) throws InvalidApplicationException {
        UserEntity user = this.entityManager.find(UserEntity.class, data.getForUserId());
        if (user == null)
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{data.getForUserId(), UserEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));

        NotificationIntegrationEvent event = new NotificationIntegrationEvent();
        event.setTenant(tenantScope.getTenant());
        event.setUserId(data.getForUserId());

        event.setContactTypeHint(NotificationContactType.EMAIL);
        event.setNotificationType(this.config.getTenantRequestApprovedNotificationKey());
        NotificationFieldData fieldData = new NotificationFieldData();
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        fieldInfoList.add(new FieldInfo(this.config.getTenantRequestApprovedTemplate().getName(), DataType.String, user.getLastName() + " " + user.getFirstName()));
        fieldInfoList.add(new FieldInfo(this.config.getTenantRequestApprovedTemplate().getTenantCode(), DataType.String, tenantScope.getTenantCode()));
        fieldInfoList.add(new FieldInfo(this.config.getTenantRequestApprovedTemplate().getTenantRequestId(), DataType.String, data.getId().toString()));
        fieldData.setFields(fieldInfoList);
        event.setData(jsonHandlingService.toJsonSafe(fieldData));
        return event;
    }

    private void notifyForReject(TenantRequestEntity data) {
        //TODO Notify without tenant
//		if (this.conventionService.isValidEmail(data.getEmail())) {
//			NotificationIntegrationEvent eventEmail = this.createNotificationForReject(data);
//			List<ContactPair> contactPairs = new ArrayList<>();
//			contactPairs.add(new ContactPair(ContactInfoType.Email, data.getEmail()));
//			NotificationContactData contactData = new NotificationContactData(contactPairs, null, null);
//			eventEmail.setContactHint(jsonHandlingService.toJsonSafe(contactData));
//			eventEmail.setContactTypeHint(NotificationContactType.EMAIL);
//			eventHandler.handle(eventEmail);
//		}
//
//		NotificationIntegrationEvent eventInApp = this.createNotificationForReject(data);
//		eventInApp.setContactTypeHint(NotificationContactType.IN_APP);
//		eventHandler.handle(eventInApp);
    }

    private NotificationIntegrationEvent createNotificationForReject(TenantRequestEntity data) throws InvalidApplicationException {
        UserEntity user = this.entityManager.find(UserEntity.class, data.getForUserId());
        if (user == null)
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{data.getForUserId(), UserEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));

        NotificationIntegrationEvent event = new NotificationIntegrationEvent();
        event.setUserId(data.getForUserId());

        event.setContactTypeHint(NotificationContactType.EMAIL);
        event.setNotificationType(this.config.getTenantRequestRejectedNotificationKey());
        NotificationFieldData fieldData = new NotificationFieldData();
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        fieldInfoList.add(new FieldInfo(this.config.getTenantRequestRejectedTemplate().getName(), DataType.String, user.getLastName() + " " + user.getFirstName()));
        fieldInfoList.add(new FieldInfo(this.config.getTenantRequestApprovedTemplate().getTenantRequestId(), DataType.String, data.getId().toString()));
        fieldData.setFields(fieldInfoList);
        event.setData(jsonHandlingService.toJsonSafe(fieldData));
        return event;
    }

//    @Override
//    public void deleteAndSave(UUID id) throws MyForbiddenException {
//        logger.debug("deleting tenant request: {}", id);
//        this.authService.authorizeForce(Permission.DeleteTenantRequest);
//        this.deleterFactory.deleter(TenantRequestDeleter.class).deleteAndSaveByIds(List.of(id));
//    }
}
