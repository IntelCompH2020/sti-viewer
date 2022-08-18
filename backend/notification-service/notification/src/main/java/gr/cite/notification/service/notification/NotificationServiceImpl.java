package gr.cite.notification.service.notification;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.authorization.Permission;
import gr.cite.notification.common.enums.IsActive;
import gr.cite.notification.common.enums.NotificationContactType;
import gr.cite.notification.common.scope.tenant.TenantScope;
import gr.cite.notification.common.scope.tenant.TenantScoped;
import gr.cite.notification.convention.ConventionService;
import gr.cite.notification.data.TenantEntity;
import gr.cite.notification.data.TenantScopedEntityManager;
import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.errorcode.ErrorThesaurusProperties;
import gr.cite.notification.model.SendNotificationResult;
import gr.cite.notification.model.Tenant;
import gr.cite.notification.model.builder.NotificationBuilder;
import gr.cite.notification.model.Notification;
import gr.cite.notification.model.deleter.NotificationDeleter;
import gr.cite.notification.model.persist.NotificationPersist;
import gr.cite.notification.service.channelResolution.ChannelResolutionService;
import gr.cite.notification.service.contact.extractor.ContactExtractorFactory;
import gr.cite.notification.service.contact.model.Contact;
import gr.cite.notification.service.message.builder.MessageBuilderFactory;
import gr.cite.notification.service.message.model.Message;
import gr.cite.notification.service.notify.NotifierFactory;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.deleter.DeleterFactory;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequestScope
public class NotificationServiceImpl implements NotificationService {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(NotificationServiceImpl.class));
    private final TenantScopedEntityManager entityManager;
    private final AuthorizationService authService;
    private final AuthorizationService authorizationService;
    private final DeleterFactory deleterFactory;
    private final BuilderFactory builderFactory;
    private final ConventionService conventionService;
    private final ErrorThesaurusProperties errors;
    private final MessageSource messageSource;
    private final ChannelResolutionService channelResolutionService;
    private final MessageBuilderFactory messageBuilderFactory;
    private final ContactExtractorFactory contactExtractorFactory;
    private final NotifierFactory notifierFactory;
    private final ApplicationContext applicationContext;

    @Autowired
    public NotificationServiceImpl(
            TenantScopedEntityManager entityManager,
            AuthorizationService authService, AuthorizationService authorizationService,
            DeleterFactory deleterFactory,
            BuilderFactory builderFactory,
            ConventionService conventionService,
            ErrorThesaurusProperties errors,
            MessageSource messageSource,
            ChannelResolutionService channelResolutionService, MessageBuilderFactory messageBuilderFactory, ContactExtractorFactory contactExtractorFactory, NotifierFactory notifierFactory, ApplicationContext applicationContext) {
        this.entityManager = entityManager;
        this.authService = authService;
        this.authorizationService = authorizationService;
        this.deleterFactory = deleterFactory;
        this.builderFactory = builderFactory;
        this.conventionService = conventionService;
        this.errors = errors;
        this.messageSource = messageSource;
        this.channelResolutionService = channelResolutionService;
        this.messageBuilderFactory = messageBuilderFactory;
        this.contactExtractorFactory = contactExtractorFactory;
        this.notifierFactory = notifierFactory;
        this.applicationContext = applicationContext;
    }

    @Override
    public Notification persist(NotificationPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting notification").And("model", model).And("fields", fields));

        this.authorizationService.authorizeForce(Permission.EditNotification);

        Boolean isUpdate = this.conventionService.isValidGuid(model.getId());

        NotificationEntity data = null;
        if (isUpdate) {
            data = this.entityManager.find(NotificationEntity.class, model.getId());
            if (data == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), Notification.class.getSimpleName()}, LocaleContextHolder.getLocale()));
            if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash())) throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
        } else {
            data = new NotificationEntity();
            data.setId(UUID.randomUUID());
            data.setIsActive(IsActive.ACTIVE);
            data.setCreatedAt(Instant.now());
        }

        data.setNotifiedAt(model.getNotifiedAt());
        data.setContactHint(model.getContactHint());
        data.setContactTypeHint(model.getContactTypeHint());
        data.setType(model.getType());
        data.setUserId(model.getUserId());
        data.setUpdatedAt(Instant.now());

        if (isUpdate) this.entityManager.merge(data);
        else this.entityManager.persist(data);

        this.entityManager.flush();

        return this.builderFactory.builder(NotificationBuilder.class).authorize(AuthorizationFlags.OwnerOrPermission).build(BaseFieldSet.build(fields, Notification._id, Notification._hash), data);
    }

    public SendNotificationResult doNotify(NotificationEntity notification) {
        List<NotificationContactType> contactTypes = this.orderContactTypes(notification);
        for (NotificationContactType contactType: contactTypes) {
            SendNotificationResult result = this.sendNotification(notification, contactType);
            if (result.getSuccess()) return result;
        }
        return null;
    }

    private List<NotificationContactType> orderContactTypes(NotificationEntity notification) {
        List<NotificationContactType> contactTypes = this.channelResolutionService.resolve(notification.getType(), notification.getUserId());
        if (notification.getContactTypeHint() == null) return contactTypes;

        List<NotificationContactType> ordered = new ArrayList<>(Collections.singleton(notification.getContactTypeHint()));
        for (NotificationContactType type: contactTypes)
        {
            if (type == notification.getContactTypeHint()) continue;
            ordered.add(type);
        }

        return ordered;
    }

    private SendNotificationResult sendNotification(NotificationEntity notification, NotificationContactType contactType) {
        String tracking = null;
        try {
            Message message = this.messageBuilderFactory.getFromType(contactType).buildMessage(notification);
            if (message == null) {
                return new SendNotificationResult(false, contactType);
            }
            Contact contact = contactExtractorFactory.fromContactType(contactType).extract(notification);
            if (contact == null) {
                return new SendNotificationResult(false, contactType);
            }
            TenantScope tenantScoped = applicationContext.getBean(TenantScope.class);
            TenantEntity tenant = this.entityManager.find(TenantEntity.class, notification.getTenantId());
            tenantScoped.setTenant(tenant.getId(), tenant.getCode());
            tracking = notifierFactory.fromContactType(contactType).notify(contact, message);
        }catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return new SendNotificationResult(false, contactType);
        }

        return new SendNotificationResult(true, contactType, tracking);
    }

    @Override
    public void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException {
        logger.debug("deleting tenant: {}", id);
        this.authService.authorizeForce(Permission.DeleteNotification);
        this.deleterFactory.deleter(NotificationDeleter.class).deleteAndSaveByIds(List.of(id));
    }

}
