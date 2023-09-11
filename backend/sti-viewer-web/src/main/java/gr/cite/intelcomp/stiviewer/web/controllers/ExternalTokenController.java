package gr.cite.intelcomp.stiviewer.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.data.ExternalTokenEntity;
import gr.cite.intelcomp.stiviewer.model.ExternalToken;
import gr.cite.intelcomp.stiviewer.model.ExternalTokenCreateResponse;
import gr.cite.intelcomp.stiviewer.model.builder.ExternalTokenBuilder;
import gr.cite.intelcomp.stiviewer.model.censorship.ExternalTokenCensor;
import gr.cite.intelcomp.stiviewer.model.persist.externaltoken.ExternalTokenChangePersist;
import gr.cite.intelcomp.stiviewer.model.persist.externaltoken.ExternalTokenExpirationPersist;
import gr.cite.intelcomp.stiviewer.model.persist.externaltoken.IndicatorPointReportExternalTokenPersist;
import gr.cite.intelcomp.stiviewer.query.ExternalTokenQuery;
import gr.cite.intelcomp.stiviewer.query.lookup.ExternalTokenLookup;
import gr.cite.intelcomp.stiviewer.service.externaltoken.ExternalTokenService;
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
import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.management.InvalidApplicationException;
import javax.naming.OperationNotSupportedException;
import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@RestController
@RequestMapping(path = "api/external-token")
@Hidden
public class ExternalTokenController {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(ExternalTokenController.class));

    private final BuilderFactory builderFactory;

    private final AuditService auditService;

    private final ExternalTokenService externalTokenService;

    private final CensorFactory censorFactory;

    private final QueryFactory queryFactory;

    private final MessageSource messageSource;

    @Autowired
    public ExternalTokenController(
            BuilderFactory builderFactory,
            AuditService auditService,
            ExternalTokenService externalTokenService,
            CensorFactory censorFactory,
            QueryFactory queryFactory,
            MessageSource messageSource

    ) {
        this.builderFactory = builderFactory;
        this.auditService = auditService;
        this.externalTokenService = externalTokenService;
        this.censorFactory = censorFactory;
        this.queryFactory = queryFactory;
        this.messageSource = messageSource;
    }

    @PostMapping("query")
    public QueryResult<ExternalToken> query(@RequestBody ExternalTokenLookup lookup) throws MyApplicationException, MyForbiddenException {
        logger.debug("querying {}", ExternalToken.class.getSimpleName());

        this.censorFactory.censor(ExternalTokenCensor.class).censor(lookup.getProject(), null);

        ExternalTokenQuery query = lookup.enrich(this.queryFactory).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicatorOrIndicatorAccess);
        List<ExternalTokenEntity> data = query.collectAs(lookup.getProject());
        List<ExternalToken> models = this.builderFactory.builder(ExternalTokenBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicatorOrIndicatorAccess).build(lookup.getProject(), data);
        long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

        this.auditService.track(AuditableAction.ExternalToken_Query, "lookup", lookup);

        return new QueryResult<>(models, count);
    }

    @GetMapping("{id}")
    @Transactional
    public ExternalToken get(@PathVariable("id") UUID id, FieldSet fieldSet, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
        logger.debug(new MapLogEntry("retrieving" + ExternalToken.class.getSimpleName()).And("id", id).And("fields", fieldSet));

        this.censorFactory.censor(ExternalTokenCensor.class).censor(fieldSet, null);

        ExternalTokenQuery query = this.queryFactory.query(ExternalTokenQuery.class).ids(id).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicatorOrIndicatorAccess);
        ExternalToken model = this.builderFactory.builder(ExternalTokenBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicatorOrIndicatorAccess).build(fieldSet, query.firstAs(fieldSet));
        if (model == null)
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, ExternalToken.class.getSimpleName()}, LocaleContextHolder.getLocale()));

        this.auditService.track(AuditableAction.ExternalToken_Lookup, Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("id", id),
                new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
        ));

        return model;
    }

    @PostMapping("persist-expiration")
    @Transactional
    public ExternalToken persistExpiration(@MyValidate @RequestBody ExternalTokenExpirationPersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException, OperationNotSupportedException {
        logger.debug(new MapLogEntry("persist expiration" + ExternalToken.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

        ExternalToken persisted = this.externalTokenService.persist(model, fieldSet);

        this.auditService.track(AuditableAction.ExternalToken_Persist, Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("model", model),
                new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
        ));
        return persisted;
    }

    @PostMapping("token-change")
    @Transactional
    public String tokenChange(@MyValidate @RequestBody ExternalTokenChangePersist model) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException, OperationNotSupportedException, NoSuchAlgorithmException {
        logger.debug(new MapLogEntry("token change" + ExternalToken.class.getSimpleName()).And("model", model));

        String token = this.externalTokenService.persist(model);

        this.auditService.track(AuditableAction.ExternalToken_Persist, Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("model", model)
        ));
        return token;
    }

    @PostMapping("persist/for-indicator-point-report")
    @Transactional
    public ExternalTokenCreateResponse persistIndicatorPointReport(@MyValidate @RequestBody IndicatorPointReportExternalTokenPersist model) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException, OperationNotSupportedException, JsonProcessingException, NoSuchAlgorithmException {
        logger.debug(new MapLogEntry("token change" + ExternalToken.class.getSimpleName()).And("model", model));

        ExternalTokenCreateResponse response = this.externalTokenService.persist(model);

        this.auditService.track(AuditableAction.ExternalToken_Persist, Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("model", model)
        ));
        return response;
    }

    @DeleteMapping("{id}")
    @Transactional
    public void delete(@PathVariable("id") UUID id) throws MyForbiddenException, InvalidApplicationException, OperationNotSupportedException {
        logger.debug(new MapLogEntry("retrieving" + ExternalToken.class.getSimpleName()).And("id", id));

        this.externalTokenService.deleteAndSave(id);

        this.auditService.track(AuditableAction.ExternalToken_Delete, "id", id);
    }

}
