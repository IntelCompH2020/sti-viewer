package gr.cite.intelcomp.stiviewer.service.user.invitation;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.data.UserInvitationEntity;
import gr.cite.intelcomp.stiviewer.model.UserContactInfo;
import gr.cite.intelcomp.stiviewer.model.UserInvitation;
import gr.cite.intelcomp.stiviewer.model.builder.UserInvitationBuilder;
import gr.cite.intelcomp.stiviewer.model.persist.UserInvitationPersist;
import gr.cite.tools.data.builder.BuilderFactory;
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
import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

@Service
@RequestScope
public class UserInvitationServiceImpl implements UserInvitationService {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserInvitationServiceImpl.class));
    private final TenantEntityManager entityManager;
    private final AuthorizationService authorizationService;
    private final BuilderFactory builderFactory;
    private final ConventionService conventionService;
    private final MessageSource messageSource;

    @Autowired
    public UserInvitationServiceImpl(
            TenantEntityManager entityManager,
            AuthorizationService authorizationService,
            BuilderFactory builderFactory,
            ConventionService conventionService,
            MessageSource messageSource
    ) {
        this.entityManager = entityManager;
        this.authorizationService = authorizationService;
        this.builderFactory = builderFactory;
        this.conventionService = conventionService;
        this.messageSource = messageSource;
    }

    @Override
    public UserInvitation persist(UserInvitationPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting data access request").And("model", model).And("fields", fields));

        this.authorizationService.authorizeForce(Permission.EditUserInvitation);

        boolean isUpdate = this.conventionService.isValidGuid(model.getId());

        UserInvitationEntity data;
        if (isUpdate) {
            data = this.entityManager.find(UserInvitationEntity.class, model.getId());
            if (data == null)
                throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), UserInvitation.class.getSimpleName()}, LocaleContextHolder.getLocale()));
        } else {
            data = new UserInvitationEntity();
            data.setCreatedAt(Instant.now());
        }

        data.setConsumed(model.getConsumed());
        data.setExpiresAt(model.getExpiresAt());
        data.setEmail(model.getEmail());
        data.setToken(UUID.randomUUID().toString());
        data.setTenantId(model.getTenantId());
        data.setUpdatedAt(BigInteger.valueOf(Instant.now().toEpochMilli()));

        if (isUpdate)
            this.entityManager.merge(data);
        else this.entityManager.persist(data);

        this.entityManager.flush();

        return this.builderFactory.builder(UserInvitationBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, UserInvitation._id, UserContactInfo._hash), data);
    }

}
