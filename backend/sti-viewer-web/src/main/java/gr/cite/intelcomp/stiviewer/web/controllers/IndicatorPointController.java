package gr.cite.intelcomp.stiviewer.web.controllers;

import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.IndicatorPointEntity;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpoint.IndicatorPointQuery;
import gr.cite.intelcomp.stiviewer.elastic.query.lookup.IndicatorPointDistinctLookup;
import gr.cite.intelcomp.stiviewer.elastic.query.lookup.IndicatorPointLookup;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.UserSettings;
import gr.cite.intelcomp.stiviewer.model.builder.indicatorpoint.IndicatorPointBuilder;
import gr.cite.intelcomp.stiviewer.model.censorship.IndicatorPointCensor;
import gr.cite.intelcomp.stiviewer.model.elasticreport.AggregateResponseModel;
import gr.cite.intelcomp.stiviewer.model.elasticreport.IndicatorPointReportLookup;
import gr.cite.intelcomp.stiviewer.model.indicatorpoint.IndicatorPoint;
import gr.cite.intelcomp.stiviewer.model.persist.indicatorpoint.IndicatorPointPersist;
import gr.cite.intelcomp.stiviewer.query.IndicatorQuery;
import gr.cite.intelcomp.stiviewer.service.indicatorpoint.IndicatorPointService;
import gr.cite.intelcomp.stiviewer.web.model.ElasticValuesResponse;
import gr.cite.intelcomp.stiviewer.web.model.QueryResult;
import gr.cite.tools.auditing.AuditService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.censor.CensorFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.query.DistinctValuesResponse;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import gr.cite.tools.validation.MyValidate;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.management.InvalidApplicationException;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/indicator-point")
public class IndicatorPointController {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(IndicatorPointController.class));

	private final BuilderFactory builderFactory;
	private final AuditService auditService;
	private final IndicatorPointService indicatorPointService;
	private final CensorFactory censorFactory;
	private final QueryFactory queryFactory;
	private final MessageSource messageSource;

	@Autowired
	public IndicatorPointController(
			BuilderFactory builderFactory,
			AuditService auditService,
			IndicatorPointService indicatorPointService,
			CensorFactory censorFactory,
			QueryFactory queryFactory,
			MessageSource messageSource

	) {
		this.builderFactory = builderFactory;
		this.auditService = auditService;
		this.indicatorPointService = indicatorPointService;
		this.censorFactory = censorFactory;
		this.queryFactory = queryFactory;
		this.messageSource = messageSource;
	}

	@PostMapping("query")
	public QueryResult<IndicatorPoint> query(@RequestBody IndicatorPointLookup lookup) throws MyApplicationException, MyForbiddenException {
		logger.debug("querying {}", IndicatorPoint.class.getSimpleName());

		this.censorFactory.censor(IndicatorPointCensor.class).censor(lookup.getProject());

		IndicatorPointQuery query = lookup.enrich(this.queryFactory).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator);
		List<IndicatorPointEntity> data = query.collectAs(lookup.getProject());
		List<IndicatorPoint> models = this.builderFactory.builder(IndicatorPointBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(lookup.getProject(), data);
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

		this.auditService.track(AuditableAction.Indicator_Point_Query, "lookup", lookup);
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return new QueryResult<>(models, count);
	}

	@PostMapping("query-distinct")
	public ElasticValuesResponse<String> queryDistinct(@RequestBody IndicatorPointDistinctLookup lookup) throws MyApplicationException, MyForbiddenException {
		logger.debug("querying {}", IndicatorPoint.class.getSimpleName());

		this.censorFactory.censor(IndicatorPointCensor.class).censor(new BaseFieldSet().ensure(IndicatorPoint._id));

		IndicatorQuery indicatorQuery = this.queryFactory.query(IndicatorQuery.class).ids(lookup.getIndicatorIds()).authorize(lookup.isViewNotApprovedValues() ? EnumSet.of(AuthorizationFlags.None) : AuthorizationFlags.OwnerOrPermissionOrIndicatorOrIndicatorAccess);
		if (indicatorQuery.count() != lookup.getIndicatorIds().stream().distinct().collect(Collectors.toList()).size()) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{String.join(",", lookup.getIndicatorIds().stream().map(x -> x.toString()).collect(Collectors.toList())), Indicator.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		IndicatorPointQuery query = lookup.enrich(this.queryFactory).indicatorIds(lookup.getIndicatorIds());
		if (!lookup.isViewNotApprovedValues()) {
			query = query.authorize(AuthorizationFlags.OwnerOrPermissionOrIndicatorOrIndicatorAccess);
		}

		DistinctValuesResponse<String> data = query.collectDistinct(lookup.getField(), lookup.getOrder(), (x) -> x, lookup.getAfterKey(), lookup.getBatchSize());
		long count = data.getTotal();

		this.auditService.track(AuditableAction.Indicator_Point_QueryDistinct, "lookup", lookup);
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return new ElasticValuesResponse<>(data.getItems(), count, data.getAfterKey());
	}

	@GetMapping("{indicatorId}/{id}")
	public IndicatorPoint get(@PathVariable("indicatorId") UUID indicatorId, @PathVariable("id") UUID id, FieldSet fieldSet, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		logger.debug(new MapLogEntry("retrieving" + IndicatorPoint.class.getSimpleName()).And("id", id).And("fields", fieldSet));

		this.censorFactory.censor(IndicatorPointCensor.class).censor(fieldSet);

		IndicatorQuery indicatorQuery = this.queryFactory.query(IndicatorQuery.class).ids(indicatorId);
		if (indicatorQuery.count() == 0) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{indicatorId, Indicator.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		IndicatorPointQuery query = this.queryFactory.query(IndicatorPointQuery.class).indicatorIds(indicatorId).ids(id).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator);
		IndicatorPoint model = this.builderFactory.builder(IndicatorPointBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fieldSet, query.firstAs(fieldSet));
		if (model == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, IndicatorPoint.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.Indicator_Point_Lookup, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("id", id),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}

	@PostMapping("{indicatorId}/persist")
	@Transactional
	public IndicatorPoint persist(@PathVariable("indicatorId") UUID indicatorId, @MyValidate @RequestBody IndicatorPointPersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException, IOException {
		logger.debug(new MapLogEntry("persisting" + IndicatorPoint.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		IndicatorPoint persisted = this.indicatorPointService.persist(indicatorId, model, fieldSet);

		this.auditService.track(AuditableAction.Indicator_Point_Persist, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return persisted;
	}

	@PostMapping("{indicatorId}/report")
	public AggregateResponseModel report(@PathVariable("indicatorId") UUID indicatorId, @MyValidate @RequestBody IndicatorPointReportLookup model, FieldSet fieldSet) throws InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting" + IndicatorPoint.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		AggregateResponseModel report = this.indicatorPointService.report(indicatorId, model, fieldSet);

		this.auditService.track(AuditableAction.Indicator_Point_Report, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return report;
	}

	@PostMapping("{indicatorId}/export-xlsx")
	public ResponseEntity<?> exportXlsx(@PathVariable("indicatorId") UUID indicatorId, @MyValidate @RequestBody IndicatorPointReportLookup model, HttpServletResponse response) throws InvalidApplicationException, IOException {
		logger.debug(new MapLogEntry("persisting" + IndicatorPoint.class.getSimpleName()).And("model", model));

		byte[] report = this.indicatorPointService.export(indicatorId, model);

		this.auditService.track(AuditableAction.Indicator_Point_ExportXlsx, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE); // (3) Content-Type: application/octet-stream
		httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("report.xlsx").build().toString()); // (4) Content-Disposition: attachment; filename="demo-file.txt"
		return new ResponseEntity(report, httpHeaders, HttpStatus.OK);
	}

	@PostMapping("{indicatorId}/bulk-persist")
	@Transactional
	public void bulkPersist(@PathVariable("indicatorId") UUID indicatorId, @MyValidate @RequestBody List<IndicatorPointPersist> models) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException, IOException {
		logger.debug(new MapLogEntry("persisting" + IndicatorPoint.class.getSimpleName()).And("model", models));

		this.indicatorPointService.persist(indicatorId, models);

		this.auditService.track(AuditableAction.Indicator_Point_Bulk_Persist, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", models)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return;
	}


	@GetMapping("global-search-config-by-key/{key}")
	public String getGlobalSearchConfig(@PathVariable("key") String key) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		logger.debug(new MapLogEntry("retrieving" + Indicator.class.getSimpleName()).And("key", key));

		String model = this.indicatorPointService.getGlobalSearchConfig(key);
		if (model == null || model.isBlank()) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{key, UserSettings.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.Indicator_Point_GetGlobalSearchConfig, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("key", key)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}

}
