package gr.cite.intelcomp.stiviewer.web.controllers;

import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupAccessConfigViewEntity;
import gr.cite.intelcomp.stiviewer.data.DataAccessRequestEntity;
import gr.cite.intelcomp.stiviewer.model.builder.dataaccessrequest.DataAccessRequestBuilder;
import gr.cite.intelcomp.stiviewer.model.builder.indicatorgroup.IndicatorGroupAccessConfigViewBuilder;
import gr.cite.intelcomp.stiviewer.model.censorship.DataAccessRequestCensor;
import gr.cite.intelcomp.stiviewer.model.censorship.indicatorgroup.IndicatorGroupAccessConfigViewCensor;
import gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequest;
import gr.cite.intelcomp.stiviewer.model.indicatorgroup.IndicatorGroupAccessConfigView;
import gr.cite.intelcomp.stiviewer.model.persist.dataaccessrequest.DataAccessRequestPersist;
import gr.cite.intelcomp.stiviewer.model.persist.dataaccessrequest.DataAccessRequestStatusPersist;
import gr.cite.intelcomp.stiviewer.query.DataAccessRequestQuery;
import gr.cite.intelcomp.stiviewer.query.lookup.DataAccessRequestLookup;
import gr.cite.intelcomp.stiviewer.service.dataaccessrequest.DataAccessRequestService;
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
@RequestMapping(path = "api/data-access-request")
public class DataAccessRequestController {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(DataAccessRequestController.class));

	private final BuilderFactory builderFactory;
	private final AuditService auditService;
	private final DataAccessRequestService dataAccessRequestService;
	private final CensorFactory censorFactory;
	private final QueryFactory queryFactory;
	private final MessageSource messageSource;

	@Autowired
	public DataAccessRequestController(
			BuilderFactory builderFactory,
			AuditService auditService,
			DataAccessRequestService dataAccessRequestService,
			CensorFactory censorFactory,
			QueryFactory queryFactory,
			MessageSource messageSource

	) {
		this.builderFactory = builderFactory;
		this.auditService = auditService;
		this.dataAccessRequestService = dataAccessRequestService;
		this.censorFactory = censorFactory;
		this.queryFactory = queryFactory;
		this.messageSource = messageSource;
	}

	@PostMapping("query")
	public QueryResult<DataAccessRequest> Query(@RequestBody DataAccessRequestLookup lookup) throws MyApplicationException, MyForbiddenException {
		logger.debug("querying {}", DataAccessRequest.class.getSimpleName());
		this.censorFactory.censor(DataAccessRequestCensor.class).censor(lookup.getProject(), null);
		DataAccessRequestQuery query = lookup.enrich(this.queryFactory).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator);
		List<DataAccessRequestEntity> data = query.collectAs(lookup.getProject());
		List<DataAccessRequest> models = this.builderFactory.builder(DataAccessRequestBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(lookup.getProject(), data);
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

		this.auditService.track(AuditableAction.Data_Access_Request_Query, "lookup", lookup);
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return new QueryResult<>(models, count);
	}

	@GetMapping("{id}")
	public DataAccessRequest Get(@PathVariable("id") UUID id, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		logger.debug(new MapLogEntry("retrieving" + DataAccessRequest.class.getSimpleName()).And("id", id).And("fields", fieldSet));

		this.censorFactory.censor(DataAccessRequestCensor.class).censor(fieldSet, null);

		DataAccessRequestQuery query = this.queryFactory.query(DataAccessRequestQuery.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).ids(id);
		DataAccessRequest model = this.builderFactory.builder(DataAccessRequestBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fieldSet, query.firstAs(fieldSet));
		if (model == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, DataAccessRequest.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.Data_Access_Request_Lookup, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("id", id),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}

	@PostMapping("persist")
	@Transactional
	public DataAccessRequest Persist(@MyValidate @RequestBody DataAccessRequestPersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting" + DataAccessRequest.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		DataAccessRequest persisted = this.dataAccessRequestService.persist(model, fieldSet);

		this.auditService.track(AuditableAction.Data_Access_Request_Persist, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return persisted;
	}

	@PostMapping("status")
	@Transactional
	public DataAccessRequest Persist(@MyValidate @RequestBody DataAccessRequestStatusPersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting" + DataAccessRequestStatusPersist.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		DataAccessRequest persisted = this.dataAccessRequestService.persist(model, fieldSet);

		this.auditService.track(AuditableAction.Data_Access_Request_PersistStatus, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return persisted;
	}
	@GetMapping("indicator-group-access-config-view/{code}")
	public IndicatorGroupAccessConfigView GetIndicatorGroupAccessConfigView(@PathVariable("code") String code, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		logger.debug(new MapLogEntry("retrieving" + DataAccessRequest.class.getSimpleName()).And("code", code).And("fields", fieldSet));

		this.censorFactory.censor(IndicatorGroupAccessConfigViewCensor.class).censor(fieldSet, null);

		IndicatorGroupAccessConfigViewEntity data = this.dataAccessRequestService.getIndicatorGroupAccessConfigViewEntity(code);

		IndicatorGroupAccessConfigView model = this.builderFactory.builder(IndicatorGroupAccessConfigViewBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fieldSet, data);
		if (model == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{code, IndicatorGroupAccessConfigView.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.Data_Access_Request_GetIndicatorGroupAccessConfigView, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("code", code),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}
}
