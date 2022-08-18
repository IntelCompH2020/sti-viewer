package gr.cite.intelcomp.stiviewer.service.notification;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.IndicatorAccessEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.data.notification.NotificationEntity;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.model.IndicatorAccess;
import gr.cite.intelcomp.stiviewer.model.builder.IndicatorAccessBuilder;
import gr.cite.intelcomp.stiviewer.model.builder.notification.NotificationBuilder;
import gr.cite.intelcomp.stiviewer.model.notification.Notification;
import gr.cite.intelcomp.stiviewer.model.persist.notification.NotificationPersist;
import gr.cite.intelcomp.stiviewer.service.indicatoraccess.IndicatorAccessServiceImpl;
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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import java.time.Instant;
import java.util.UUID;

@Service
@RequestScope
public class NotificationServiceImpl implements NotificationService {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(NotificationServiceImpl.class));
    private final TenantEntityManager entityManager;
    private final AuthorizationService authorizationService;
    private final DeleterFactory deleterFactory;
    private final BuilderFactory builderFactory;
    private final ConventionService conventionService;
    private final ErrorThesaurusProperties errors;
    private final MessageSource messageSource;

    @Autowired
    public NotificationServiceImpl(
            TenantEntityManager entityManager,
            AuthorizationService authorizationService,
            DeleterFactory deleterFactory,
            BuilderFactory builderFactory,
            ConventionService conventionService,
            ErrorThesaurusProperties errors,
            MessageSource messageSource
    ) {
        this.entityManager = entityManager;
        this.authorizationService = authorizationService;
        this.deleterFactory = deleterFactory;
        this.builderFactory = builderFactory;
        this.conventionService = conventionService;
        this.errors = errors;
        this.messageSource = messageSource;
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
        data.setData(model.getData());
        data.setNotifyState(model.getNotifyState());
        data.setNotifiedWith(model.getNotifiedWith());
        data.setRetryCount(model.getRetryCount());
        data.setTrackingState(model.getTrackingState());
        data.setTrackingProcess(model.getTrackingProcess());
        data.setTrackingData(model.getTrackingData());
        data.setProvenanceRef(model.getProvenanceRef());
        data.setUpdatedAt(Instant.now());

        if (isUpdate) this.entityManager.merge(data);
        else this.entityManager.persist(data);

        this.entityManager.flush();

        Notification persisted = this.builderFactory.builder(NotificationBuilder.class).authorize(AuthorizationFlags.OwnerOrPermission).build(BaseFieldSet.build(fields, Notification._id, Notification._hash), data);
        return persisted;
    }

}
