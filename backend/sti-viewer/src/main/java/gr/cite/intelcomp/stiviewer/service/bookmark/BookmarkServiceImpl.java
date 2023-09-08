package gr.cite.intelcomp.stiviewer.service.bookmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.authorization.OwnedResource;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.BookmarkType;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.common.types.bookmark.DashboardBookmarkEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.BookmarkEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.model.Bookmark;
import gr.cite.intelcomp.stiviewer.model.builder.BookmarkBuilder;
import gr.cite.intelcomp.stiviewer.model.deleter.BookmarkDeleter;
import gr.cite.intelcomp.stiviewer.model.persist.GetBookmarkByHashParams;
import gr.cite.intelcomp.stiviewer.model.persist.MyBookmarkPersist;
import gr.cite.intelcomp.stiviewer.query.BookmarkQuery;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequestScope
public class BookmarkServiceImpl implements BookmarkService {
    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(BookmarkServiceImpl.class));

    private final TenantEntityManager entityManager;

    private final AuthorizationService authorizationService;
    private final DeleterFactory deleterFactory;
    private final BuilderFactory builderFactory;
    private final ConventionService conventionService;
    private final ErrorThesaurusProperties errors;
    private final MessageSource messageSource;
    private final UserScope userScope;
    private final JsonHandlingService jsonHandlingService;
    private final QueryFactory queryFactory;

    @Autowired
    public BookmarkServiceImpl(
            TenantEntityManager entityManager,
            AuthorizationService authorizationService,
            DeleterFactory deleterFactory,
            BuilderFactory builderFactory,
            ConventionService conventionService,
            ErrorThesaurusProperties errors,
            MessageSource messageSource,
            UserScope userScope,
            JsonHandlingService jsonHandlingService,
            QueryFactory queryFactory) {
        this.entityManager = entityManager;
        this.authorizationService = authorizationService;
        this.deleterFactory = deleterFactory;
        this.builderFactory = builderFactory;
        this.conventionService = conventionService;
        this.errors = errors;
        this.messageSource = messageSource;
        this.userScope = userScope;
        this.jsonHandlingService = jsonHandlingService;
        this.queryFactory = queryFactory;
    }

    public Bookmark persistMine(MyBookmarkPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException, JsonProcessingException {
        logger.debug(new MapLogEntry("persisting bookmark").And("model", model).And("fields", fields));

        Boolean isUpdate = this.conventionService.isValidGuid(model.getId());

        BookmarkEntity data;
        if (isUpdate) {
            data = this.queryFactory.query(BookmarkQuery.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).types(model.getType()).userIds(this.userScope.getUserId()).first();
            if (data == null)
                throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), Bookmark.class.getSimpleName()}, LocaleContextHolder.getLocale()));
            if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash()))
                throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
        } else {
            data = new BookmarkEntity();
            data.setId(UUID.randomUUID());
            data.setIsActive(IsActive.ACTIVE);
            data.setType(model.getType());
            data.setCreatedAt(Instant.now());
            data.setUserId(this.userScope.getUserId());
        }

        this.authorizationService.authorizeAtLeastOneForce(List.of(new OwnedResource(data.getUserId())), Permission.EditBookmark);

        data.setName(model.getName());
        data.setValue(model.getValue());
        data.setUpdatedAt(Instant.now());
        data.setHashCode(this.getHashCode(data.getType(), data.getValue()));

        if (isUpdate)
            this.entityManager.merge(data);
        else this.entityManager.persist(data);

        this.entityManager.flush();


        GetBookmarkByHashParams params = new GetBookmarkByHashParams();
        params.setType(model.getType());
        params.setValue(model.getValue());
        Bookmark existingBookmark = this.getBookmarkByHashMine(params, data.getId(), new BaseFieldSet().ensure(Bookmark._id));
        if (existingBookmark != null)
            throw new MyApplicationException(this.errors.getBookmarkHashConflict().getCode(), this.errors.getBookmarkHashConflict().getMessage());

        return this.builderFactory.builder(BookmarkBuilder.class).build(BaseFieldSet.build(fields, Bookmark._id, Bookmark._hash), data);
    }

    @Override
    public Bookmark getBookmarkByHashMine(GetBookmarkByHashParams model, FieldSet fields) throws JsonProcessingException, InvalidApplicationException, MyForbiddenException {
        return this.getBookmarkByHashMine(model, null, fields);
    }

    private Bookmark getBookmarkByHashMine(GetBookmarkByHashParams model, UUID excludedId, FieldSet fields) throws JsonProcessingException, InvalidApplicationException, MyForbiddenException {
        logger.debug(new MapLogEntry("persisting bookmark").And("model", model).And("excludedId", excludedId));

        this.authorizationService.authorizeAtLeastOneForce(List.of(new OwnedResource(this.userScope.getUserId())), Permission.EditBookmark);

        String hashCode = this.getHashCode(model.getType(), model.getValue());
        BookmarkQuery query = this.queryFactory.query(BookmarkQuery.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).isActive(IsActive.ACTIVE).types(model.getType()).hashCode(hashCode).userIds(this.userScope.getUserId());
        if (excludedId != null)
            query.excludedIds(excludedId);
        List<BookmarkEntity> data = query.collect();

        if (data == null || data.isEmpty())
            return null;
        for (BookmarkEntity d : data) {
            if (this.equals(model.getType(), model.getValue(), d.getValue()))
                return this.builderFactory.builder(BookmarkBuilder.class).build(BaseFieldSet.build(fields, Bookmark._id, Bookmark._hash), d);
        }

        return null;
    }

    private String getHashCode(BookmarkType type, String value) throws JsonProcessingException {
        if (Objects.requireNonNull(type) == BookmarkType.Dashboard) {
            DashboardBookmarkEntity dashboardBookmarkEntity = this.jsonHandlingService.fromJson(DashboardBookmarkEntity.class, value);
            return String.valueOf(dashboardBookmarkEntity.hashCode());
        }
        throw new MyApplicationException("invalid type " + type);
    }

    private boolean equals(BookmarkType type, String value, String value2) throws JsonProcessingException {
        if (Objects.requireNonNull(type) == BookmarkType.Dashboard) {
            DashboardBookmarkEntity dashboardBookmarkEntity = this.jsonHandlingService.fromJson(DashboardBookmarkEntity.class, value);
            DashboardBookmarkEntity dashboardBookmarkEntity2 = this.jsonHandlingService.fromJson(DashboardBookmarkEntity.class, value2);
            return Objects.equals(dashboardBookmarkEntity, dashboardBookmarkEntity2);
        }
        throw new MyApplicationException("invalid type " + type);
    }

    public void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException {
        logger.debug("deleting bookmark: {}", id);

        BookmarkEntity data = this.entityManager.find(BookmarkEntity.class, id);
        if (data == null)
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, Bookmark.class.getSimpleName()}, LocaleContextHolder.getLocale()));
        this.authorizationService.authorizeAtLeastOneForce(List.of(new OwnedResource(data.getUserId())), Permission.DeleteBookmark);

        this.deleterFactory.deleter(BookmarkDeleter.class).deleteAndSaveByIds(Collections.singletonList(id));
    }
}
