package gr.cite.intelcomp.stiviewer.web.controllers;

import gr.cite.commons.web.oidc.principal.MyPrincipal;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractor;
import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.enums.UserContactType;
import gr.cite.intelcomp.stiviewer.data.UserContactInfoEntity;
import gr.cite.intelcomp.stiviewer.model.User;
import gr.cite.intelcomp.stiviewer.model.UserContactInfo;
import gr.cite.intelcomp.stiviewer.model.builder.UserContactInfoBuilder;
import gr.cite.intelcomp.stiviewer.model.censorship.UserContactInfoCensor;
import gr.cite.intelcomp.stiviewer.model.persist.UserContactInfoPersist;
import gr.cite.intelcomp.stiviewer.query.UserContactInfoQuery;
import gr.cite.intelcomp.stiviewer.query.lookup.UserContactInfoLookup;
import gr.cite.intelcomp.stiviewer.service.user.contactinfo.UserContactInfoService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.management.InvalidApplicationException;
import javax.transaction.Transactional;
import java.util.AbstractMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping(path = "api/user-contact-info")
public class UserContactInfoController {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserContactInfoController.class));

    private final BuilderFactory builderFactory;

    private final AuditService auditService;

    private final UserContactInfoService contactInfoService;

    private final CensorFactory censorFactory;

    private final QueryFactory queryFactory;

    private final MessageSource messageSource;

    private final ClaimExtractor claimExtractor;

    @Autowired
    public UserContactInfoController(
            BuilderFactory builderFactory,
            AuditService auditService,
            UserContactInfoService contactInfoService,
            CensorFactory censorFactory,
            QueryFactory queryFactory,
            MessageSource messageSource,
            ClaimExtractor claimExtractor) {
        this.builderFactory = builderFactory;
        this.auditService = auditService;
        this.contactInfoService = contactInfoService;
        this.censorFactory = censorFactory;
        this.queryFactory = queryFactory;
        this.messageSource = messageSource;
        this.claimExtractor = claimExtractor;
    }

    @PostMapping("query")
    public QueryResult<UserContactInfo> query(@RequestBody UserContactInfoLookup lookup) throws MyApplicationException, MyForbiddenException {
        logger.debug("querying {}", UserContactInfo.class.getSimpleName());
        this.censorFactory.censor(UserContactInfoCensor.class).censor(lookup.getProject(), null);
        UserContactInfoQuery query = lookup.enrich(this.queryFactory).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator);
        List<UserContactInfoEntity> data = query.collectAs(lookup.getProject());
        List<UserContactInfo> models = this.builderFactory.builder(UserContactInfoBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(lookup.getProject(), data);
        long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

        this.auditService.track(AuditableAction.User_Contact_Info_Query, "lookup", lookup);

        return new QueryResult<>(models, count);
    }

    @GetMapping("{type}")
    @Transactional
    public UserContactInfo get(@PathVariable("type") UserContactType type, FieldSet fieldSet, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
        logger.debug(new MapLogEntry("retrieving" + User.class.getSimpleName()).And("type", type).And("fields", fieldSet));

        this.censorFactory.censor(UserContactInfoCensor.class).censor(fieldSet, null);

        UserContactInfoPersist.ID id = new UserContactInfoPersist.ID();
        id.setUserId(claimExtractor.subjectUUID((MyPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        id.setType(type);
        UserContactInfoQuery query = this.queryFactory.query(UserContactInfoQuery.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).ids(id);
        UserContactInfo model = this.builderFactory.builder(UserContactInfoBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fieldSet, query.firstAs(fieldSet));
        if (model == null)
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, UserContactInfo.class.getSimpleName()}, LocaleContextHolder.getLocale()));

        this.auditService.track(AuditableAction.User_Contact_Info_Lookup, Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("id", id),
                new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
        ));

        return model;
    }

    @PostMapping("persist")
    @Transactional
    public UserContactInfo persist(@MyValidate @RequestBody UserContactInfoPersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting" + UserContactInfo.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

        UserContactInfo persisted = this.contactInfoService.persist(model, fieldSet);

        this.auditService.track(AuditableAction.User_Contact_Info_Persist, Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("model", model),
                new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
        ));
        return persisted;
    }

    @DeleteMapping("{type}")
    @Transactional
    public void delete(@PathVariable("type") UserContactType type) throws MyForbiddenException, InvalidApplicationException {
        logger.debug(new MapLogEntry("retrieving" + UserContactInfo.class.getSimpleName()).And("type", type));

        UserContactInfoPersist.ID id = new UserContactInfoPersist.ID();
        id.setType(type);
        this.contactInfoService.deleteAndSave(id);

        this.auditService.track(AuditableAction.User_Delete, "id", id);
    }

}
