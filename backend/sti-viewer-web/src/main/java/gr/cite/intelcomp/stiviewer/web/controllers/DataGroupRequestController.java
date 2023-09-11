package gr.cite.intelcomp.stiviewer.web.controllers;

import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.data.DataGroupRequestEntity;
import gr.cite.intelcomp.stiviewer.model.builder.datagrouprequest.DataGroupRequestBuilder;
import gr.cite.intelcomp.stiviewer.model.censorship.DataGroupRequestCensor;
import gr.cite.intelcomp.stiviewer.model.datagrouprequest.DataGroupRequest;
import gr.cite.intelcomp.stiviewer.model.persist.datagrouprequest.DataGroupRequestNamePersist;
import gr.cite.intelcomp.stiviewer.model.persist.datagrouprequest.DataGroupRequestPersist;
import gr.cite.intelcomp.stiviewer.query.DataGroupRequestQuery;
import gr.cite.intelcomp.stiviewer.query.lookup.DataGroupRequestLookup;
import gr.cite.intelcomp.stiviewer.service.datagrouprequest.DataGroupRequestService;
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
import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/data-group-request")
public class DataGroupRequestController {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(DataGroupRequestController.class));

    private final BuilderFactory builderFactory;

    private final AuditService auditService;

    private final DataGroupRequestService dataGroupRequestService;

    private final CensorFactory censorFactory;

    private final QueryFactory queryFactory;

    private final MessageSource messageSource;

    @Autowired
    public DataGroupRequestController(
            BuilderFactory builderFactory,
            AuditService auditService,
            DataGroupRequestService dataGroupRequestService,
            CensorFactory censorFactory,
            QueryFactory queryFactory,
            MessageSource messageSource

    ) {
        this.builderFactory = builderFactory;
        this.auditService = auditService;
        this.dataGroupRequestService = dataGroupRequestService;
        this.censorFactory = censorFactory;
        this.queryFactory = queryFactory;
        this.messageSource = messageSource;
    }

    @PostMapping("query")
    public QueryResult<DataGroupRequest> query(@RequestBody DataGroupRequestLookup lookup) throws MyApplicationException, MyForbiddenException {
        logger.debug("querying {}", DataGroupRequest.class.getSimpleName());
        this.censorFactory.censor(DataGroupRequestCensor.class).censor(lookup.getProject(), null);
        DataGroupRequestQuery query = lookup.enrich(this.queryFactory).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator);
        List<DataGroupRequestEntity> data = query.collectAs(lookup.getProject());
        List<DataGroupRequest> models = this.builderFactory.builder(DataGroupRequestBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(lookup.getProject(), data);
        long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

        this.auditService.track(AuditableAction.Data_Group_Request_Query, "lookup", lookup);

        return new QueryResult<>(models, count);
    }

    @GetMapping("{id}")
    @Transactional
    public DataGroupRequest get(@PathVariable("id") UUID id, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
        logger.debug(new MapLogEntry("retrieving" + DataGroupRequest.class.getSimpleName()).And("id", id).And("fields", fieldSet));

        this.censorFactory.censor(DataGroupRequestCensor.class).censor(fieldSet, null);

        DataGroupRequestQuery query = this.queryFactory.query(DataGroupRequestQuery.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).ids(id);
        DataGroupRequest model = this.builderFactory.builder(DataGroupRequestBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fieldSet, query.firstAs(fieldSet));
        if (model == null)
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, DataGroupRequest.class.getSimpleName()}, LocaleContextHolder.getLocale()));

        this.auditService.track(AuditableAction.Data_Group_Request_Lookup, Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("id", id),
                new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
        ));

        return model;
    }

    @PostMapping("persist-name")
    @Transactional
    public DataGroupRequest persistName(@MyValidate @RequestBody DataGroupRequestNamePersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException, NoSuchAlgorithmException {
        logger.debug(new MapLogEntry("persisting name" + DataGroupRequestNamePersist.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

        DataGroupRequest persisted = this.dataGroupRequestService.persist(model, fieldSet);

        this.auditService.track(AuditableAction.Data_Group_Request_PersistName, Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("model", model),
                new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
        ));
        return persisted;
    }

    @PostMapping("persist")
    @Transactional
    public DataGroupRequest persist(@MyValidate @RequestBody DataGroupRequestPersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException, NoSuchAlgorithmException {
        logger.debug(new MapLogEntry("persisting" + DataGroupRequest.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

        DataGroupRequest persisted = this.dataGroupRequestService.persist(model, fieldSet);

        this.auditService.track(AuditableAction.Data_Group_Request_Persist, Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("model", model),
                new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
        ));
        return persisted;
    }

    @DeleteMapping("{id}")
    @Transactional
    public void delete(@PathVariable("id") UUID id) throws MyForbiddenException, InvalidApplicationException {
        logger.debug(new MapLogEntry("deleting" + DataGroupRequest.class.getSimpleName()).And("id", id));

        this.dataGroupRequestService.deleteAndSave(id);

        this.auditService.track(AuditableAction.Data_Group_Request_Delete, "id", id);
    }

}
