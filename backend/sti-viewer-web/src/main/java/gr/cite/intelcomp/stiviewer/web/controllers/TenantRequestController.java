package gr.cite.intelcomp.stiviewer.web.controllers;

import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.data.TenantRequestEntity;
import gr.cite.intelcomp.stiviewer.model.TenantRequest;
import gr.cite.intelcomp.stiviewer.model.builder.TenantRequestBuilder;
import gr.cite.intelcomp.stiviewer.model.censorship.TenantRequestCensor;
import gr.cite.intelcomp.stiviewer.model.persist.TenantRequestPersist;
import gr.cite.intelcomp.stiviewer.model.persist.TenantRequestStatusPersist;
import gr.cite.intelcomp.stiviewer.query.TenantRequestQuery;
import gr.cite.intelcomp.stiviewer.query.lookup.TenantRequestLookup;
import gr.cite.intelcomp.stiviewer.service.tenantrequest.TenantRequestService;
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
import java.util.*;

@RestController
@RequestMapping(path = "api/tenant-request")
public class TenantRequestController {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantRequestController.class));

	private final BuilderFactory builderFactory;
	private final AuditService auditService;
	private final TenantRequestService tenantRequestService;
	private final CensorFactory censorFactory;
	private final QueryFactory queryFactory;
	private final MessageSource messageSource;
	private final UserScope userScope;

	@Autowired
	public TenantRequestController(BuilderFactory builderFactory,
	                               AuditService auditService,
	                               TenantRequestService tenantRequestService,
	                               CensorFactory censorFactory,
	                               QueryFactory queryFactory,
	                               MessageSource messageSource,
	                               UserScope userScope) {
		this.builderFactory = builderFactory;
		this.auditService = auditService;
		this.tenantRequestService = tenantRequestService;
		this.censorFactory = censorFactory;
		this.queryFactory = queryFactory;
		this.messageSource = messageSource;
		this.userScope = userScope;
	}

	@PostMapping("query")
	public QueryResult<TenantRequest> Query(@RequestBody TenantRequestLookup lookup) throws MyApplicationException, MyForbiddenException {
		logger.debug("querying {}", TenantRequest.class.getSimpleName());

		this.censorFactory.censor(TenantRequestCensor.class).censor(lookup.getProject(), null);

		TenantRequestQuery query = lookup.enrich(this.queryFactory).authorize(AuthorizationFlags.OwnerOrPermission);
		List<TenantRequestEntity> data = query.collectAs(lookup.getProject());
		List<TenantRequest> models = this.builderFactory.builder(TenantRequestBuilder.class).authorize(AuthorizationFlags.OwnerOrPermission).build(lookup.getProject(), data);
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

		this.auditService.track(AuditableAction.TenantRequest_Query, "lookup", lookup);

		return new QueryResult<>(models, count);
	}

	@GetMapping("{id}")
	@Transactional
	public TenantRequest Get(@PathVariable UUID id, FieldSet fieldSet, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("retrieving" + TenantRequest.class.getSimpleName()).And("id", id).And("fields", fieldSet));

		this.censorFactory.censor(TenantRequestCensor.class).censor(fieldSet, this.userScope.getUserId()); //TODO: hack find other solution

		TenantRequestQuery query = this.queryFactory.query(TenantRequestQuery.class).authorize(AuthorizationFlags.OwnerOrPermission).ids(id);
		TenantRequest model = this.builderFactory.builder(TenantRequestBuilder.class).authorize(AuthorizationFlags.OwnerOrPermission).build(fieldSet, query.firstAs(fieldSet));
		if (model == null)
			throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, TenantRequest.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.TenantRequest_Lookup, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("id", id),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}

	@PostMapping("persist")
	@Transactional
	public TenantRequest Persist(@MyValidate @RequestBody TenantRequestPersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting" + TenantRequest.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		TenantRequest persisted = this.tenantRequestService.persist(model, fieldSet);

		this.auditService.track(AuditableAction.TenantRequest_Persist, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return persisted;
	}

	@PostMapping("status")
	@Transactional
	public TenantRequest status(@MyValidate @RequestBody TenantRequestStatusPersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting" + TenantRequestStatusPersist.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		TenantRequest persisted = this.tenantRequestService.persist(model, fieldSet);

		this.auditService.track(AuditableAction.TenantRequest_PersistStatus, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return persisted;
	}

//    @DeleteMapping("{id}")
//    @Transactional
//    public void Delete(@PathVariable("id") UUID id) throws MyForbiddenException {
//        logger.debug(new MapLogEntry("deleting"+ TenantRequest.class.getSimpleName()).And("id",id));
//
//        this.tenantRequestService.deleteAndSave(id);
//
//        this.auditService.track(AuditableAction.TenantRequest_Delete,"id",id);
//
//        //this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
//    }
}
