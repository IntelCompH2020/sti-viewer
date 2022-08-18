package gr.cite.intelcomp.stiviewer.web.controllers;

import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractor;
import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.data.UserInvitationEntity;
import gr.cite.intelcomp.stiviewer.model.UserInvitation;
import gr.cite.intelcomp.stiviewer.model.builder.UserInvitationBuilder;
import gr.cite.intelcomp.stiviewer.model.censorship.UserInvitationCensor;
import gr.cite.intelcomp.stiviewer.model.persist.UserInvitationPersist;
import gr.cite.intelcomp.stiviewer.query.UserInvitationQuery;
import gr.cite.intelcomp.stiviewer.query.lookup.UserInvitationLookup;
import gr.cite.intelcomp.stiviewer.service.user.invitation.UserInvitationService;
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
@RequestMapping(path = "api/user-invitation")
public class UserInvitationController {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserInvitationController.class));

	private final BuilderFactory builderFactory;
	private final AuditService auditService;
	private final UserInvitationService userInvitationService;
	private final CensorFactory censorFactory;
	private final QueryFactory queryFactory;
	private final MessageSource messageSource;
	private final ClaimExtractor claimExtractor;

	@Autowired
	public UserInvitationController(
			BuilderFactory builderFactory,
			AuditService auditService,
			UserInvitationService userInvitationService,
			CensorFactory censorFactory,
			QueryFactory queryFactory,
			MessageSource messageSource,
			ClaimExtractor claimExtractor) {
		this.builderFactory = builderFactory;
		this.auditService = auditService;
		this.userInvitationService = userInvitationService;
		this.censorFactory = censorFactory;
		this.queryFactory = queryFactory;
		this.messageSource = messageSource;
		this.claimExtractor = claimExtractor;
	}

	@PostMapping("query")
	public QueryResult<UserInvitation> Query(@RequestBody UserInvitationLookup lookup) throws MyApplicationException, MyForbiddenException {
		logger.debug("querying {}", UserInvitation.class.getSimpleName());
		this.censorFactory.censor(UserInvitationCensor.class).censor(lookup.getProject());
		UserInvitationQuery query = lookup.enrich(this.queryFactory);
		List<UserInvitationEntity> data = query.collectAs(lookup.getProject());
		List<UserInvitation> models = this.builderFactory.builder(UserInvitationBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(lookup.getProject(), data);
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

		this.auditService.track(AuditableAction.User_Invitation_Query, "lookup", lookup);
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return new QueryResult<>(models, count);
	}

	@GetMapping("{id}")
	@Transactional
	public UserInvitation Get(@PathVariable("id") UUID id, FieldSet fieldSet, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		logger.debug(new MapLogEntry("retrieving" + UserInvitation.class.getSimpleName()).And("id", id).And("fields", fieldSet));

		this.censorFactory.censor(UserInvitationCensor.class).censor(fieldSet);

		UserInvitationQuery query = this.queryFactory.query(UserInvitationQuery.class).ids(id);
		UserInvitation model = this.builderFactory.builder(UserInvitationBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fieldSet, query.firstAs(fieldSet));
		if (model == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, UserInvitation.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.User_Invitation_Lookup, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("id", id),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}

	@PostMapping("persist")
	@Transactional
	public UserInvitation Persist(@MyValidate @RequestBody UserInvitationPersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting" + UserInvitation.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		UserInvitation persisted = this.userInvitationService.persist(model, fieldSet);

		this.auditService.track(AuditableAction.User_Invitation_Persist, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return persisted;
	}

}
