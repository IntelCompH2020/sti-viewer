package gr.cite.intelcomp.stiviewer.service.tenant;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.TenantEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.event.EventBroker;
import gr.cite.intelcomp.stiviewer.event.TenantTouchedEvent;
import gr.cite.intelcomp.stiviewer.model.Tenant;
import gr.cite.intelcomp.stiviewer.model.builder.TenantBuilder;
import gr.cite.intelcomp.stiviewer.model.deleter.TenantDeleter;
import gr.cite.intelcomp.stiviewer.model.persist.TenantPersist;
import gr.cite.intelcomp.stiviewer.service.keycloak.KeycloakService;
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
import java.util.List;
import java.util.UUID;

@Service
@RequestScope
public class TenantServiceImpl implements TenantService {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantServiceImpl.class));
    private final TenantEntityManager entityManager;
    private final AuthorizationService authService;
    private final DeleterFactory deleterFactory;
    private final BuilderFactory builderFactory;
    private final ConventionService conventionService;
    private final MessageSource messageSource;
    private final EventBroker eventBroker;
    private final KeycloakService keycloakService;

    @Autowired
    public TenantServiceImpl(TenantEntityManager entityManager,
                             AuthorizationService authService,
                             DeleterFactory deleterFactory,
                             BuilderFactory builderFactory,
                             ConventionService conventionService,
                             MessageSource messageSource,
                             EventBroker eventBroker, KeycloakService keycloakService) {
        this.entityManager = entityManager;
        this.authService = authService;
        this.deleterFactory = deleterFactory;
        this.builderFactory = builderFactory;
        this.conventionService = conventionService;
        this.messageSource = messageSource;
        this.eventBroker = eventBroker;
        this.keycloakService = keycloakService;
    }

    @Override
    public Tenant persist(TenantPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting data access request").And("model", model).And("fields", fields));

        this.authService.authorizeForce(Permission.EditTenant);

        Boolean isUpdate = this.conventionService.isValidGuid(model.getId());
        TenantEntity data;
        if (isUpdate) {
            data = this.entityManager.find(TenantEntity.class, model.getId());
            if (data == null)
                throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), Tenant.class}, LocaleContextHolder.getLocale()));
        } else {
            data = new TenantEntity();
            data.setId(UUID.randomUUID());
            data.setIsActive(IsActive.ACTIVE);
            data.setCreatedAt(Instant.now());
        }
        String previousCode = data.getCode();

        data.setCode(model.getCode());
        data.setName(model.getName());
        data.setConfig(model.getConfig());
        data.setUpdatedAt(Instant.now());

        if (isUpdate)
            this.entityManager.merge(data);
        else this.entityManager.persist(data);

        this.entityManager.flush();

        if (!isUpdate)
            createKeycloakGroupsForTenant(data);

        this.eventBroker.emit(new TenantTouchedEvent(data.getId(), data.getCode(), previousCode));

        return this.builderFactory.builder(TenantBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, Tenant._id), data);

    }

    @Override
    public void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException {
        logger.debug("deleting tenant: {}", id);
        this.authService.authorizeForce(Permission.DeleteTenant);
        this.deleterFactory.deleter(TenantDeleter.class).deleteAndSaveByIds(List.of(id));
    }

    private void createKeycloakGroupsForTenant(TenantEntity entity) {
        this.keycloakService.createTenantGroups(entity);
    }

}
