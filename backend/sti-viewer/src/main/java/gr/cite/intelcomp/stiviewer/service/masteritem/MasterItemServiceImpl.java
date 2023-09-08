package gr.cite.intelcomp.stiviewer.service.masteritem;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.DetailItemEntity;
import gr.cite.intelcomp.stiviewer.data.MasterItemEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.model.DetailItem;
import gr.cite.intelcomp.stiviewer.model.MasterItem;
import gr.cite.intelcomp.stiviewer.model.builder.MasterItemBuilder;
import gr.cite.intelcomp.stiviewer.model.deleter.DetailItemDeleter;
import gr.cite.intelcomp.stiviewer.model.deleter.MasterItemDeleter;
import gr.cite.intelcomp.stiviewer.model.persist.DetailItemPersist;
import gr.cite.intelcomp.stiviewer.model.persist.MasterItemDetailsPatch;
import gr.cite.intelcomp.stiviewer.model.persist.MasterItemPersist;
import gr.cite.intelcomp.stiviewer.query.DetailItemQuery;
import gr.cite.intelcomp.stiviewer.query.MasterItemQuery;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequestScope
public class MasterItemServiceImpl implements MasterItemService {
    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(MasterItemServiceImpl.class));
    private final TenantEntityManager entityManager;
    private final AuthorizationService authorizationService;
    private final DeleterFactory deleterFactory;
    private final BuilderFactory builderFactory;
    private final ConventionService conventionService;
    private final ErrorThesaurusProperties errors;
    private final MessageSource messageSource;
    private final QueryFactory queryFactory;

    @Autowired
    public MasterItemServiceImpl(
            TenantEntityManager entityManager, AuthorizationService authorizationService,
            DeleterFactory deleterFactory,
            BuilderFactory builderFactory,
            ConventionService conventionService,
            ErrorThesaurusProperties errors,
            MessageSource messageSource,
            QueryFactory queryFactory
    ) {
        this.entityManager = entityManager;
        this.authorizationService = authorizationService;
        this.deleterFactory = deleterFactory;
        this.builderFactory = builderFactory;
        this.conventionService = conventionService;
        this.errors = errors;
        this.messageSource = messageSource;
        this.queryFactory = queryFactory;
    }

    public MasterItem persist(MasterItemPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting dataset").And("model", model).And("fields", fields));

        this.authorizationService.authorizeForce(Permission.EditMasterItem);

        //Persist MasterItem
        MasterItemEntity data = this.patchAndSave(model);

        MasterItemDetailsPatch detailsPatch = new MasterItemDetailsPatch();
        detailsPatch.setId(data.getId());
        detailsPatch.setHash(this.conventionService.hashValue(data.getUpdatedAt()));
        detailsPatch.setDetails(model.getDetails().stream().peek(x -> x.setMasterItemId(data.getId())
        ).collect(Collectors.toList()));

        this.patchAndSave(List.of(detailsPatch));

        this.entityManager.flush();

        return this.builderFactory.builder(MasterItemBuilder.class).build(BaseFieldSet.build(fields, MasterItem._id, MasterItem._hash), data);
    }

    private MasterItemEntity patchAndSave(MasterItemPersist model) throws MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        Boolean isUpdate = this.conventionService.isValidGuid(model.getId());

        MasterItemEntity data;
        if (isUpdate) {
            data = this.entityManager.find(MasterItemEntity.class, model.getId());
            if (data == null)
                throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), MasterItem.class.getSimpleName()}, LocaleContextHolder.getLocale()));
            if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash()))
                throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
        } else {
            data = new MasterItemEntity();
            data.setId(UUID.randomUUID());
            data.setIsActive(IsActive.ACTIVE);
            data.setCreatedAt(Instant.now());
        }

        data.setName(model.getName());
        data.setUpdatedAt(Instant.now());

        if (isUpdate)
            this.entityManager.merge(data);
        else this.entityManager.persist(data);

        this.entityManager.flush();

        return data;
    }

    private void patchAndSave(List<MasterItemDetailsPatch> models) throws MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
        if (models == null || models.isEmpty())
            return;
        List<UUID> masterItemIds = models.stream().map(MasterItemDetailsPatch::getId).filter(this.conventionService::isValidGuid).distinct().collect(Collectors.toList());

        List<MasterItemEntity> masterItems = this.queryFactory.query(MasterItemQuery.class).ids(masterItemIds)
                .collectAs(new BaseFieldSet().ensure(MasterItem._id).ensure(MasterItem._hash));
        Map<UUID, MasterItemEntity> masterItemsLookup = masterItems.stream().collect(Collectors.toMap(MasterItemEntity::getId, x -> x));

        List<DetailItemEntity> details = this.queryFactory.query(DetailItemQuery.class)
                .masterItemIds(masterItemIds).collect();
        Map<UUID, List<DetailItemEntity>> detailsLookup = this.conventionService.toDictionaryOfList(details, DetailItemEntity::getMasterId);

        for (MasterItemDetailsPatch model : models) {
            if (!masterItemsLookup.containsKey(model.getId()))
                throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), MasterItem.class.getSimpleName()}, LocaleContextHolder.getLocale()));
            MasterItemEntity masterItem = masterItemsLookup.get(model.getId());
            if (masterItem == null)
                throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), MasterItem.class.getSimpleName()}, LocaleContextHolder.getLocale()));
            if (!this.conventionService.hashValue(masterItem.getUpdatedAt()).equals(model.getHash()))
                throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());

            List<DetailItemEntity> existingDetails;
            if (detailsLookup.containsKey(model.getId())) existingDetails = detailsLookup.get(model.getId());
            else existingDetails = new ArrayList<>();

            List<UUID> updatedDetailIds = model.getDetails().stream().map(DetailItemPersist::getId).filter(this.conventionService::isValidGuid).distinct().collect(Collectors.toList());
            List<DetailItemEntity> toDelete = existingDetails.stream().filter(x -> !updatedDetailIds.contains(x.getId())).collect(Collectors.toList());
            this.deleterFactory.deleter(DetailItemDeleter.class).delete(toDelete);

            Map<UUID, DetailItemEntity> existingDetailsLookup = existingDetails.stream().collect(Collectors.toMap(DetailItemEntity::getId, x -> x));

            for (DetailItemPersist detail : model.getDetails()) {
                Boolean isUpdate = this.conventionService.isValidGuid(detail.getId());

                DetailItemEntity data;
                if (isUpdate) {
                    if (!existingDetailsLookup.containsKey(detail.getId()))
                        throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{detail.getId(), DetailItem.class.getSimpleName()}, LocaleContextHolder.getLocale()));
                    data = existingDetailsLookup.get(detail.getId());
                    if (data == null)
                        throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{detail.getId(), DetailItem.class.getSimpleName()}, LocaleContextHolder.getLocale()));
                    if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(detail.getHash()))
                        throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
                } else {
                    data = new DetailItemEntity();
                    data.setId(UUID.randomUUID());
                    data.setIsActive(IsActive.ACTIVE);
                    data.setCreatedAt(Instant.now());
                }

                data.setName(detail.getName());
                data.setMasterId(masterItem.getId());

                data.setUpdatedAt(Instant.now());

                if (isUpdate)
                    this.entityManager.merge(data);
                else this.entityManager.persist(data);
            }
        }
        this.entityManager.flush();
    }

    public void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException {
        logger.debug("deleting dataset: {}", id);

        this.authorizationService.authorizeForce(Permission.DeleteMasterItem);

        this.deleterFactory.deleter(MasterItemDeleter.class).deleteAndSaveByIds(Arrays.asList(new UUID[]{id}));
    }
}
