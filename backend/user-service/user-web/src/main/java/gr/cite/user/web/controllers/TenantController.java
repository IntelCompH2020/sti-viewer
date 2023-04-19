package gr.cite.user.web.controllers;

import gr.cite.user.audit.AuditableAction;
import gr.cite.user.authorization.AuthorizationFlags;
import gr.cite.user.data.TenantEntity;
import gr.cite.user.model.Tenant;
import gr.cite.user.model.builder.TenantBuilder;
import gr.cite.user.model.censorship.TenantCensor;
import gr.cite.user.model.persist.TenantPersist;
import gr.cite.user.query.TenantQuery;
import gr.cite.user.query.lookup.TenantLookup;
import gr.cite.user.web.model.QueryResult;
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
@RequestMapping(path = "api/tenant")
public class TenantController {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantController.class));

	private final BuilderFactory builderFactory;
	private final AuditService auditService;
	private final CensorFactory censorFactory;
	private final QueryFactory queryFactory;
	private final MessageSource messageSource;

	@Autowired
	public TenantController(BuilderFactory builderFactory,
	                        AuditService auditService,
	                        CensorFactory censorFactory,
	                        QueryFactory queryFactory,
	                        MessageSource messageSource) {
		this.builderFactory = builderFactory;
		this.auditService = auditService;
		this.censorFactory = censorFactory;
		this.queryFactory = queryFactory;
		this.messageSource = messageSource;
	}

	@PostMapping("gr.cite.user.query")
	public QueryResult<Tenant> Query(@RequestBody TenantLookup lookup) throws MyApplicationException, MyForbiddenException {
		logger.debug("querying {}", Tenant.class.getSimpleName());

		this.censorFactory.censor(TenantCensor.class).censor(lookup.getProject());

		TenantQuery query = lookup.enrich(this.queryFactory).authorize(AuthorizationFlags.OwnerOrPermission);
		List<TenantEntity> data = query.collectAs(lookup.getProject());
		List<Tenant> models = this.builderFactory.builder(TenantBuilder.class).authorize(AuthorizationFlags.OwnerOrPermission).build(lookup.getProject(), data);
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

		this.auditService.track(AuditableAction.Tenant_Query, "lookup", lookup);

		return new QueryResult<>(models, count);
	}

	@GetMapping("{id}")
	@Transactional
	public Tenant Get(@PathVariable UUID id, FieldSet fieldSet, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		logger.debug(new MapLogEntry("retrieving" + Tenant.class.getSimpleName()).And("id", id).And("fields", fieldSet));

		this.censorFactory.censor(TenantCensor.class).censor(fieldSet);

		TenantQuery query = this.queryFactory.query(TenantQuery.class).authorize(AuthorizationFlags.OwnerOrPermission).ids(id);
		Tenant model = this.builderFactory.builder(TenantBuilder.class).authorize(AuthorizationFlags.OwnerOrPermission).build(fieldSet, query.firstAs(fieldSet));
		if (model == null)
			throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, Tenant.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.Tenant_Lookup, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("id", id),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}
}
