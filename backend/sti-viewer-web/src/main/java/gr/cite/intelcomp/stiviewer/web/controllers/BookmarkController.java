package gr.cite.intelcomp.stiviewer.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.data.BookmarkEntity;
import gr.cite.intelcomp.stiviewer.model.Bookmark;
import gr.cite.intelcomp.stiviewer.model.builder.BookmarkBuilder;
import gr.cite.intelcomp.stiviewer.model.censorship.BookmarkCensor;
import gr.cite.intelcomp.stiviewer.model.persist.GetBookmarkByHashParams;
import gr.cite.intelcomp.stiviewer.model.persist.MyBookmarkPersist;
import gr.cite.intelcomp.stiviewer.query.BookmarkQuery;
import gr.cite.intelcomp.stiviewer.query.lookup.BookmarkLookup;
import gr.cite.intelcomp.stiviewer.service.bookmark.BookmarkService;
import gr.cite.intelcomp.stiviewer.web.model.QueryResult;
import gr.cite.tools.auditing.AuditService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.censor.CensorFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import gr.cite.tools.validation.MyValidate;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.management.InvalidApplicationException;
import javax.transaction.Transactional;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/bookmark")
public class BookmarkController {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(BookmarkController.class));

    private final BuilderFactory builderFactory;

    private final AuditService auditService;

    private final BookmarkService bookmarkService;

    private final CensorFactory censorFactory;

    private final QueryFactory queryFactory;

    private final MessageSource messageSource;

    private final UserScope userScope;

    @Autowired
    public BookmarkController(
            BuilderFactory builderFactory,
            AuditService auditService,
            BookmarkService bookmarkService,
            CensorFactory censorFactory,
            QueryFactory queryFactory,
            MessageSource messageSource,
            UserScope userScope

    ) {
        this.builderFactory = builderFactory;
        this.auditService = auditService;
        this.bookmarkService = bookmarkService;
        this.censorFactory = censorFactory;
        this.queryFactory = queryFactory;
        this.messageSource = messageSource;
        this.userScope = userScope;
    }

    @PostMapping("query/mine")
    public QueryResult<Bookmark> query(@RequestBody BookmarkLookup lookup) throws MyApplicationException, MyForbiddenException, InvalidApplicationException {
        logger.debug("querying {}", Bookmark.class.getSimpleName());

        this.censorFactory.censor(BookmarkCensor.class).censor(lookup.getProject(), this.userScope.getUserId());
        BookmarkQuery query = lookup.enrich(this.queryFactory).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).userIds(this.userScope.getUserId());
        List<BookmarkEntity> data = query.collectAs(lookup.getProject());
        List<Bookmark> models = this.builderFactory.builder(BookmarkBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(lookup.getProject(), data);
        long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

        this.auditService.track(AuditableAction.Bookmark_QueryMine, "lookup", lookup);

        return new QueryResult<>(models, count);
    }

    @PostMapping("get-by-hash/mine")
    public Bookmark getBookmarkByHashMine(@RequestBody GetBookmarkByHashParams model, FieldSet fieldSet) throws InvalidApplicationException, JsonProcessingException {
        logger.debug(new MapLogEntry("persisting" + Bookmark.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

        Bookmark persisted = this.bookmarkService.getBookmarkByHashMine(model, fieldSet);

        this.auditService.track(AuditableAction.Bookmark_GetBookmarkByHash, Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("model", model),
                new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
        ));

        return persisted;
    }

    @GetMapping("{id}/mine")
    @Transactional
    public Bookmark get(@PathVariable("id") UUID id, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("retrieving" + Bookmark.class.getSimpleName()).And("id", id).And("fields", fieldSet));

        this.censorFactory.censor(BookmarkCensor.class).censor(fieldSet, this.userScope.getUserId());

        BookmarkQuery query = this.queryFactory.query(BookmarkQuery.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).ids(id).userIds(this.userScope.getUserId());
        Bookmark model = this.builderFactory.builder(BookmarkBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fieldSet, query.firstAs(fieldSet));
        if (model == null)
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, Bookmark.class.getSimpleName()}, LocaleContextHolder.getLocale()));

        this.auditService.track(AuditableAction.Bookmark_LookupMine, Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("id", id),
                new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
        ));

        return model;
    }

    @PostMapping("persist/mine")
    @Transactional
    public Bookmark persist(@MyValidate @RequestBody MyBookmarkPersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException, JsonProcessingException {
        logger.debug(new MapLogEntry("persisting" + Bookmark.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

        Bookmark persisted = this.bookmarkService.persistMine(model, fieldSet);

        this.auditService.track(AuditableAction.Bookmark_PersistMine, Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("model", model),
                new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
        ));
        return persisted;
    }

    @DeleteMapping("{id}")
    @Transactional
    public void delete(@PathVariable("id") UUID id) throws MyForbiddenException, InvalidApplicationException {
        logger.debug(new MapLogEntry("retrieving" + Bookmark.class.getSimpleName()).And("id", id));

        this.bookmarkService.deleteAndSave(id);

        this.auditService.track(AuditableAction.Bookmark_DeleteMine, "id", id);
    }

}
