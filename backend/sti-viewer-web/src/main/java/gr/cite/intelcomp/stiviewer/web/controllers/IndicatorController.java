package gr.cite.intelcomp.stiviewer.web.controllers;

import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.data.IndicatorEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorElasticEntity;
import gr.cite.intelcomp.stiviewer.elastic.query.indicator.IndicatorElasticQuery;
import gr.cite.intelcomp.stiviewer.elastic.query.lookup.IndicatorElasticLookup;
import gr.cite.intelcomp.stiviewer.event.EventBroker;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.IndicatorAccess;
import gr.cite.intelcomp.stiviewer.model.IndicatorElastic;
import gr.cite.intelcomp.stiviewer.model.builder.IndicatorBuilder;
import gr.cite.intelcomp.stiviewer.model.builder.IndicatorElasticBuilder;
import gr.cite.intelcomp.stiviewer.model.censorship.IndicatorCensor;
import gr.cite.intelcomp.stiviewer.model.censorship.indicatorelastic.IndicatorElasticCensor;
import gr.cite.intelcomp.stiviewer.model.persist.IndicatorElasticPersist;
import gr.cite.intelcomp.stiviewer.model.persist.IndicatorPersist;
import gr.cite.intelcomp.stiviewer.model.persist.ResetIndicatorElasticPersist;
import gr.cite.intelcomp.stiviewer.model.persist.UserAddAccessToIndicatorColumn;
import gr.cite.intelcomp.stiviewer.query.IndicatorQuery;
import gr.cite.intelcomp.stiviewer.query.lookup.IndicatorLookup;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorService;
import gr.cite.intelcomp.stiviewer.service.indicatoraccess.IndicatorAccessService;
import gr.cite.intelcomp.stiviewer.service.indicatorelastic.ElasticIndicatorService;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.management.InvalidApplicationException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(path = "api/indicator")
public class IndicatorController {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(IndicatorController.class));

	private final BuilderFactory builderFactory;
	private final AuditService auditService;
	private final IndicatorService indicatorService;
	private final CensorFactory censorFactory;
	private final QueryFactory queryFactory;
	private final MessageSource messageSource;
	private final ElasticIndicatorService elasticIndicatorService;

	@Autowired
	public IndicatorController(
			BuilderFactory builderFactory,
			AuditService auditService,
			IndicatorService indicatorService,
			CensorFactory censorFactory,
			QueryFactory queryFactory,
			MessageSource messageSource,
			ElasticIndicatorService elasticIndicatorService) {
		this.builderFactory = builderFactory;
		this.auditService = auditService;
		this.indicatorService = indicatorService;
		this.censorFactory = censorFactory;
		this.queryFactory = queryFactory;
		this.messageSource = messageSource;
		this.elasticIndicatorService = elasticIndicatorService;
	}

	@PostMapping("query")
	public QueryResult<Indicator> Query(@RequestBody IndicatorLookup lookup) throws MyApplicationException, MyForbiddenException {
		logger.debug("querying {}", Indicator.class.getSimpleName());

		this.censorFactory.censor(IndicatorCensor.class).censor(lookup.getProject(), null);

		IndicatorQuery query = lookup.enrich(this.queryFactory).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator);
		List<IndicatorEntity> data = query.collectAs(lookup.getProject());
		List<Indicator> models = this.builderFactory.builder(IndicatorBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(lookup.getProject(), data);
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

		this.auditService.track(AuditableAction.Indicator_Query, "lookup", lookup);
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return new QueryResult<>(models, count);
	}

	@GetMapping("{id}")
	public Indicator Get(@PathVariable("id") UUID id, FieldSet fieldSet, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		logger.debug(new MapLogEntry("retrieving" + Indicator.class.getSimpleName()).And("id", id).And("fields", fieldSet));

		this.censorFactory.censor(IndicatorCensor.class).censor(fieldSet, null);

		IndicatorQuery query = this.queryFactory.query(IndicatorQuery.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).ids(id);
		Indicator model = this.builderFactory.builder(IndicatorBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fieldSet, query.firstAs(fieldSet));
		if (model == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, Indicator.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.Indicator_Lookup, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("id", id),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}

	@PostMapping("query/es")
	public QueryResult<IndicatorElastic> QueryElastic(@RequestBody IndicatorElasticLookup lookup) throws MyApplicationException, MyForbiddenException {
		logger.debug(new MapLogEntry("querying {}" + IndicatorElastic.class.getSimpleName()));

		this.censorFactory.censor(IndicatorElasticCensor.class).censor(lookup.getProject());


		IndicatorElasticQuery query = lookup.enrich(this.queryFactory).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator);
		List<IndicatorElasticEntity> data = query.collectAs(lookup.getProject());
		List<IndicatorElastic> models = this.builderFactory.builder(IndicatorElasticBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(lookup.getProject(), data);
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

		this.auditService.track(AuditableAction.Indicator_Elastic_Query, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", models)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return new QueryResult<>(models, count);
	}

	@PostMapping("persist/es")
	@Transactional
	public IndicatorElastic PersistElastic(@MyValidate @RequestBody IndicatorElasticPersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, IOException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting" + IndicatorElastic.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		IndicatorElastic indicatorElastic = this.elasticIndicatorService.persist(model, fieldSet);

		this.auditService.track(AuditableAction.Indicator_ElasticPersist, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return indicatorElastic;
	}

	@PostMapping("reset/es")
	@Transactional
	public IndicatorElastic ResetElastic(@MyValidate @RequestBody ResetIndicatorElasticPersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, IOException, InvalidApplicationException {
		logger.debug(new MapLogEntry("reset" + IndicatorElastic.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		IndicatorElastic indicatorElastic = this.elasticIndicatorService.reset(model, fieldSet);

		this.auditService.track(AuditableAction.Indicator_ElasticReset, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return indicatorElastic;
	}

	@DeleteMapping("delete/es/{id}")
	@Transactional
	public void DeleteElastic(@PathVariable("id") UUID id) throws MyForbiddenException, InvalidApplicationException, IOException {
		logger.debug(new MapLogEntry("retrieving" + Indicator.class.getSimpleName()).And("id", id));

		this.elasticIndicatorService.deleteAndSave(id);

		this.auditService.track(AuditableAction.Indicator_ElasticDelete, "id", id);
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
	}

	@PostMapping("persist")
	@Transactional
	public Indicator Persist(@MyValidate @RequestBody IndicatorPersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting" + Indicator.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));
		Indicator persisted = this.indicatorService.persist(model, fieldSet);

		this.auditService.track(AuditableAction.Indicator_Persist, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return persisted;
	}

	@DeleteMapping("{id}")
	@Transactional
	public void Delete(@PathVariable("id") UUID id) throws MyForbiddenException, InvalidApplicationException {
		logger.debug(new MapLogEntry("retrieving" + Indicator.class.getSimpleName()).And("id", id));

		this.indicatorService.deleteAndSave(id);

		this.auditService.track(AuditableAction.Indicator_Delete, "id", id);
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
	}
}
