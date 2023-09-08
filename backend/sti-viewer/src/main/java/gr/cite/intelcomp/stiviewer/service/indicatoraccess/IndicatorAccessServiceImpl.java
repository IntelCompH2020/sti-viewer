package gr.cite.intelcomp.stiviewer.service.indicatoraccess;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.common.types.indicatoraccess.FilterColumnConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatoraccess.IndicatorAccessConfigEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.IndicatorAccessEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.model.IndicatorAccess;
import gr.cite.intelcomp.stiviewer.model.builder.IndicatorAccessBuilder;
import gr.cite.intelcomp.stiviewer.model.deleter.IndicatorAccessDeleter;
import gr.cite.intelcomp.stiviewer.model.persist.IndicatorAccessPersist;
import gr.cite.intelcomp.stiviewer.model.persist.UserAddAccessToIndicatorColumn;
import gr.cite.intelcomp.stiviewer.model.persist.indicatoraccess.FilterColumnConfigPersist;
import gr.cite.intelcomp.stiviewer.model.persist.indicatoraccess.IndicatorAccessConfigPersist;
import gr.cite.intelcomp.stiviewer.query.IndicatorAccessQuery;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequestScope
public class IndicatorAccessServiceImpl implements IndicatorAccessService {
    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(IndicatorAccessServiceImpl.class));
    private final TenantEntityManager entityManager;
    private final AuthorizationService authorizationService;
    private final DeleterFactory deleterFactory;
    private final BuilderFactory builderFactory;
    private final QueryFactory queryFactory;
    private final ConventionService conventionService;
    private final ErrorThesaurusProperties errors;
    private final MessageSource messageSource;
    private final JsonHandlingService jsonHandlingService;
    private final TenantScope tenantScope;

    @PersistenceContext
    private EntityManager globalEntityManager;

    @Autowired
    public IndicatorAccessServiceImpl(
            TenantEntityManager entityManager,
            AuthorizationService authorizationService,
            DeleterFactory deleterFactory,
            BuilderFactory builderFactory,
            QueryFactory queryFactory,
            ConventionService conventionService,
            ErrorThesaurusProperties errors,
            MessageSource messageSource,
            JsonHandlingService jsonHandlingService,
            TenantScope tenantScope) {
        this.entityManager = entityManager;
        this.authorizationService = authorizationService;
        this.deleterFactory = deleterFactory;
        this.builderFactory = builderFactory;
        this.queryFactory = queryFactory;
        this.conventionService = conventionService;
        this.errors = errors;
        this.messageSource = messageSource;
        this.jsonHandlingService = jsonHandlingService;
        this.tenantScope = tenantScope;
    }

    public IndicatorAccess persist(IndicatorAccessPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting dataset").And("model", model).And("fields", fields));

        this.authorizationService.authorizeForce(Permission.EditIndicatorAccess);

        Boolean isUpdate = this.conventionService.isValidGuid(model.getId());

        IndicatorAccessEntity data;
        if (isUpdate) {
            data = this.entityManager.find(IndicatorAccessEntity.class, model.getId());
            if (data == null)
                throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), IndicatorAccess.class.getSimpleName()}, LocaleContextHolder.getLocale()));
            if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash()))
                throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
        } else {
            data = new IndicatorAccessEntity();
            data.setId(UUID.randomUUID());
            data.setIsActive(IsActive.ACTIVE);
            data.setCreatedAt(Instant.now());
        }

        data.setIndicatorId(model.getIndicatorId());
        data.setUserId(model.getUserId());
        data.setConfig(this.jsonHandlingService.toJsonSafe(this.buildIndicatorAccessConfig(data, model.getConfig())));
        data.setUpdatedAt(Instant.now());

        if (isUpdate)
            this.entityManager.merge(data);
        else
            this.entityManager.persist(data);

        this.entityManager.flush();

        return this.builderFactory.builder(IndicatorAccessBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, IndicatorAccess._id, IndicatorAccess._hash), data);
    }

    public IndicatorAccess persist(UserAddAccessToIndicatorColumn model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting dataset").And("model", model).And("fields", fields));

        this.authorizationService.authorizeForce(Permission.EditIndicatorAccess);

        if (tenantScope.isSet() && !model.getTenantId().equals(tenantScope.getTenant()))
            throw new MyForbiddenException("tenant is not allowed by user");
        boolean shouldChangeTenant = !tenantScope.isSet();

        IndicatorAccess persisted;
        try {

            if (shouldChangeTenant) {
                this.authorizationService.authorizeForce(Permission.AllowNoTenant);
                TenantEntity tenant = this.entityManager.find(TenantEntity.class, model.getTenantId());
                this.tenantScope.setTempTenant(this.globalEntityManager, tenant.getId());
            }

            IndicatorAccessEntity data = this.queryFactory.query(IndicatorAccessQuery.class).indicatorIds(model.getIndicatorId()).userIds(model.getUserId()).isActive(IsActive.ACTIVE).first();

            boolean isUpdate = data != null;

            if (!isUpdate) {
                data = new IndicatorAccessEntity();
                data.setId(UUID.randomUUID());
                data.setUserId(model.getUserId());
                data.setIndicatorId(model.getIndicatorId());
                data.setIsActive(IsActive.ACTIVE);
                data.setCreatedAt(Instant.now());
            }

            data.setConfig(this.jsonHandlingService.toJsonSafe(this.buildIndicatorAccessConfig(data, model.getColumn())));
            data.setUpdatedAt(Instant.now());

            if (isUpdate)
                this.entityManager.merge(data);
            else
                this.entityManager.persist(data);

            this.entityManager.flush();
            persisted = this.builderFactory.builder(IndicatorAccessBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, IndicatorAccess._id, IndicatorAccess._hash), data);
        } finally {
            if (shouldChangeTenant) this.tenantScope.removeTempTenant(this.globalEntityManager);
        }

        return persisted;
    }

    private IndicatorAccessConfigEntity buildIndicatorAccessConfig(IndicatorAccessEntity data, IndicatorAccessConfigPersist persist) {
        if (persist == null)
            return null;
        IndicatorAccessConfigEntity config = this.jsonHandlingService.fromJsonSafe(IndicatorAccessConfigEntity.class, data.getConfig());
        if (config == null) {
            config = new IndicatorAccessConfigEntity();
        }

        if (persist.getGlobalFilterColumns() != null && !persist.getGroupFilterColumns().isEmpty()) {
            List<FilterColumnConfigEntity> filterColumnConfigEntities = new ArrayList<>(100);
            for (FilterColumnConfigPersist filterColumnConfigPersist : persist.getGlobalFilterColumns()) {
                FilterColumnConfigEntity columnConfig = new FilterColumnConfigEntity();
                columnConfig.setColumn(filterColumnConfigPersist.getColumn());
                columnConfig.setValues(filterColumnConfigPersist.getValues());
                filterColumnConfigEntities.add(columnConfig);
            }
            config.setGlobalFilterColumns(filterColumnConfigEntities);
        }
        if (persist.getGroupFilterColumns() != null && !persist.getGroupFilterColumns().isEmpty()) {
            Map<UUID, List<FilterColumnConfigEntity>> filterColumnConfigEntitiesMap = new HashMap<>();
            for (UUID groupId : persist.getGroupFilterColumns().keySet()) {
                List<FilterColumnConfigPersist> filterColumnConfigEntitiesToPersist = persist.getGroupFilterColumns().get(groupId);
                if (filterColumnConfigEntitiesToPersist != null && !filterColumnConfigEntitiesToPersist.isEmpty()) {
                    List<FilterColumnConfigEntity> filterColumnConfigEntities = new ArrayList<>(100);
                    for (FilterColumnConfigPersist filterColumnConfigPersist : filterColumnConfigEntitiesToPersist) {
                        FilterColumnConfigEntity columnConfig = new FilterColumnConfigEntity();
                        columnConfig.setColumn(filterColumnConfigPersist.getColumn());
                        columnConfig.setValues(filterColumnConfigPersist.getValues());
                        filterColumnConfigEntities.add(columnConfig);
                    }
                    filterColumnConfigEntitiesMap.put(groupId, filterColumnConfigEntities);
                }
            }
            config.setGroupFilterColumns(filterColumnConfigEntitiesMap.isEmpty() ? null : filterColumnConfigEntitiesMap);
        }

        return config;
    }

    private IndicatorAccessConfigEntity buildIndicatorAccessConfig(IndicatorAccessEntity data, FilterColumnConfigPersist persist) {
        IndicatorAccessConfigEntity config = this.jsonHandlingService.fromJsonSafe(IndicatorAccessConfigEntity.class, data.getConfig());
        if (persist == null || this.conventionService.isNullOrEmpty(persist.getColumn()) || conventionService.isListNullOrEmpty(persist.getValues()))
            throw new MyApplicationException("Required at least one column");
        persist.setValues(persist.getValues().stream().filter(x -> !this.conventionService.isNullOrEmpty(x)).distinct().collect(Collectors.toList()));
        if (conventionService.isListNullOrEmpty(persist.getValues()))
            throw new MyApplicationException("Required at least one column");

        if (config == null)
            config = new IndicatorAccessConfigEntity();
        if (config.getGlobalFilterColumns() == null)
            config.setGlobalFilterColumns(new ArrayList<>(100));

        List<FilterColumnConfigEntity> filterColumnConfigEntities = new ArrayList<>(100);

        boolean added = false;
        for (FilterColumnConfigEntity columnConfigEntity : config.getGlobalFilterColumns()) {
            FilterColumnConfigEntity columnConfig = new FilterColumnConfigEntity();
            columnConfig.setColumn(columnConfigEntity.getColumn());
            columnConfig.setValues(columnConfigEntity.getValues());
            if (columnConfig.getColumn().equals(persist.getColumn())) {
                if (columnConfig.getValues() == null)
                    columnConfig.setValues(new ArrayList<>());
                columnConfig.getValues().addAll(persist.getValues());
                columnConfig.setValues(columnConfigEntity.getValues().stream().distinct().collect(Collectors.toList()));
                added = true;
            }
            filterColumnConfigEntities.add(columnConfig);
        }
        if (!added) {
            FilterColumnConfigEntity columnConfig = new FilterColumnConfigEntity();
            columnConfig.setColumn(persist.getColumn());
            columnConfig.setValues(persist.getValues());
            filterColumnConfigEntities.add(columnConfig);
        }
        config.setGlobalFilterColumns(filterColumnConfigEntities);

        return config;
    }

    public void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException {
        logger.debug("deleting dataset: {}", id);

        this.authorizationService.authorizeForce(Permission.DeleteIndicatorAccess);

        this.deleterFactory.deleter(IndicatorAccessDeleter.class).deleteAndSaveByIds(Collections.singletonList(id));
    }
}
