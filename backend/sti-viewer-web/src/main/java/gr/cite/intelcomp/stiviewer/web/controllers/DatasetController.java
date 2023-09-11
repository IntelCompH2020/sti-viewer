package gr.cite.intelcomp.stiviewer.web.controllers;

import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.data.DatasetEntity;
import gr.cite.intelcomp.stiviewer.model.Dataset;
import gr.cite.intelcomp.stiviewer.model.builder.DatasetBuilder;
import gr.cite.intelcomp.stiviewer.model.censorship.DatasetCensor;
import gr.cite.intelcomp.stiviewer.model.persist.DatasetPersist;
import gr.cite.intelcomp.stiviewer.query.DatasetQuery;
import gr.cite.intelcomp.stiviewer.query.lookup.DatasetLookup;
import gr.cite.intelcomp.stiviewer.service.dataset.DatasetService;
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
import javax.transaction.Transactional;
import java.util.*;

@RestController
@RequestMapping(path = "api/dataset")
@Hidden
public class DatasetController {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(DatasetController.class));

    private final BuilderFactory builderFactory;

    private final AuditService auditService;

    private final DatasetService datasetService;

    private final CensorFactory censorFactory;

    private final QueryFactory queryFactory;

    private final MessageSource messageSource;

    @Autowired
    public DatasetController(
            BuilderFactory builderFactory,
            AuditService auditService,
            DatasetService datasetService,
            CensorFactory censorFactory,
            QueryFactory queryFactory,
            MessageSource messageSource

    ) {
        this.builderFactory = builderFactory;
        this.auditService = auditService;
        this.datasetService = datasetService;
        this.censorFactory = censorFactory;
        this.queryFactory = queryFactory;
        this.messageSource = messageSource;
    }

    @PostMapping("query")
    public QueryResult<Dataset> query(@RequestBody DatasetLookup lookup) throws MyApplicationException, MyForbiddenException {
        logger.debug("querying {}", Dataset.class.getSimpleName());

        this.censorFactory.censor(DatasetCensor.class).censor(lookup.getProject());

        DatasetQuery query = lookup.enrich(this.queryFactory);
        List<DatasetEntity> data = query.collectAs(lookup.getProject());
        List<Dataset> models = this.builderFactory.builder(DatasetBuilder.class).build(lookup.getProject(), data);
        long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

        this.auditService.track(AuditableAction.Dataset_Query, "lookup", lookup);

        return new QueryResult<>(models, count);
    }

    @GetMapping("{id}")
    @Transactional
    public Dataset get(@PathVariable("id") UUID id, FieldSet fieldSet, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
        logger.debug(new MapLogEntry("retrieving" + Dataset.class.getSimpleName()).And("id", id).And("fields", fieldSet));

        this.censorFactory.censor(DatasetCensor.class).censor(fieldSet);

        DatasetQuery query = this.queryFactory.query(DatasetQuery.class).ids(id);
        Dataset model = this.builderFactory.builder(DatasetBuilder.class).build(fieldSet, query.firstAs(fieldSet));
        if (model == null)
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, Dataset.class.getSimpleName()}, LocaleContextHolder.getLocale()));

        this.auditService.track(AuditableAction.Dataset_Lookup, Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("id", id),
                new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
        ));

        return model;
    }

    @PostMapping("persist")
    @Transactional
    public Dataset persist(@MyValidate @RequestBody DatasetPersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting" + Dataset.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

        Dataset persisted = this.datasetService.persist(model, fieldSet);

        this.auditService.track(AuditableAction.Dataset_Persist, Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("model", model),
                new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
        ));
        return persisted;
    }

    @DeleteMapping("{id}")
    @Transactional
    public void delete(@PathVariable("id") UUID id) throws MyForbiddenException, InvalidApplicationException {
        logger.debug(new MapLogEntry("retrieving" + Dataset.class.getSimpleName()).And("id", id));

        this.datasetService.deleteAndSave(id);

        this.auditService.track(AuditableAction.Dataset_Delete, "id", id);
    }

}
