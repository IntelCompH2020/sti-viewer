package gr.cite.intelcomp.stiviewer.service.indicator;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.types.indicator.AccessRequestConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicator.FilterColumnConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicator.IndicatorConfigEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.IndicatorEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.event.EventBroker;
import gr.cite.intelcomp.stiviewer.event.IndicatorTouchedEvent;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.builder.IndicatorBuilder;
import gr.cite.intelcomp.stiviewer.model.deleter.IndicatorDeleter;
import gr.cite.intelcomp.stiviewer.model.persist.IndicatorPersist;
import gr.cite.intelcomp.stiviewer.model.persist.indicator.IndicatorConfigPersist;
import gr.cite.intelcomp.stiviewer.query.IndicatorQuery;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.deleter.DeleterFactory;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequestScope
public class IndicatorServiceImpl implements IndicatorService {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(IndicatorServiceImpl.class));

    private final TenantEntityManager entityManager;
    private final AuthorizationService authorizationService;
    private final DeleterFactory deleterFactory;
    private final BuilderFactory builderFactory;
    private final ConventionService conventionService;
    private final ErrorThesaurusProperties errors;
    private final MessageSource messageSource;
    private final EventBroker eventBroker;
    private final QueryFactory queryFactory;
    private final JsonHandlingService jsonHandlingService;

    @Autowired
    public IndicatorServiceImpl(
            TenantEntityManager entityManager,
            AuthorizationService authorizationService,
            DeleterFactory deleterFactory,
            BuilderFactory builderFactory,
            ConventionService conventionService,
            ErrorThesaurusProperties errors,
            MessageSource messageSource,
            EventBroker eventBroker,
            QueryFactory queryFactory,
            JsonHandlingService jsonHandlingService) {
        this.entityManager = entityManager;
        this.authorizationService = authorizationService;
        this.deleterFactory = deleterFactory;
        this.builderFactory = builderFactory;
        this.conventionService = conventionService;
        this.errors = errors;
        this.messageSource = messageSource;
        this.eventBroker = eventBroker;
        this.queryFactory = queryFactory;
        this.jsonHandlingService = jsonHandlingService;
    }

    public Indicator persist(IndicatorPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        return this.persist(model, fields, null);
    }

    public Indicator persist(IndicatorPersist model, FieldSet fields, UUID newItemId) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting data indicator").And("model", model).And("fields", fields));

        this.authorizationService.authorizeForce(Permission.EditIndicator);

        boolean isUpdate = this.conventionService.isValidGuid(model.getId());

        IndicatorEntity data;
        if (isUpdate) {
            data = this.entityManager.find(IndicatorEntity.class, model.getId());
            if (data == null)
                throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), Indicator.class.getSimpleName()}, LocaleContextHolder.getLocale()));
        } else {
            data = new IndicatorEntity();
            data.setId(newItemId == null ? UUID.randomUUID() : newItemId);
            data.setIsActive(IsActive.ACTIVE);
            data.setCreatedAt(Instant.now());
        }

        data.setCode(model.getCode());
        data.setName(model.getName());
        data.setDescription(model.getDescription());
        data.setUpdatedAt(Instant.now());
        data.setConfig(jsonHandlingService.toJsonSafe(this.mapToAccessRequestConfig(model.getConfig())));
        if (isUpdate)
            this.entityManager.merge(data);
        else this.entityManager.persist(data);

        this.entityManager.flush();

        if (this.queryFactory.query(IndicatorQuery.class).codes(data.getCode()).count() > 1)
            throw new MyApplicationException(this.errors.getIndexAlreadyExists().getCode(), this.errors.getIndexAlreadyExists().getMessage());

        this.eventBroker.emit(new IndicatorTouchedEvent(data.getId()));
        return this.builderFactory.builder(IndicatorBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, Indicator._id), data);
    }

    private IndicatorConfigEntity mapToAccessRequestConfig(IndicatorConfigPersist config) {
        if (config == null)
            return null;
        IndicatorConfigEntity persistConfig = new IndicatorConfigEntity();
        if (config.getAccessRequestConfig() != null && config.getAccessRequestConfig().getFilterColumns() != null) {
            List<FilterColumnConfigEntity> filterColumns = new ArrayList<>();
            AccessRequestConfigEntity accessRequestConfig = new AccessRequestConfigEntity();
            config.getAccessRequestConfig().getFilterColumns().forEach(x -> {
                FilterColumnConfigEntity newConfig = new FilterColumnConfigEntity();
                newConfig.setCode(x.getCode());
                newConfig.setDependsOnCode(x.getDependsOnCode());
                filterColumns.add(newConfig);
            });
            accessRequestConfig.setFilterColumns(filterColumns);
            persistConfig.setAccessRequestConfig(accessRequestConfig);
        }

        return persistConfig;
    }

    public void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException {
        logger.debug("deleting dataset: {}", id);

        this.authorizationService.authorizeForce(Permission.DeleteIndicator);

        this.deleterFactory.deleter(IndicatorDeleter.class).deleteAndSaveByIds(List.of(id));
    }
}

