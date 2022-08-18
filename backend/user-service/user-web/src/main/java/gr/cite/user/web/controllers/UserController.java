package gr.cite.user.web.controllers;

import gr.cite.user.audit.AuditableAction;
import gr.cite.user.authorization.AuthorizationFlags;
import gr.cite.user.common.scope.tenant.TenantScope;
import gr.cite.user.data.UserEntity;
import gr.cite.user.model.User;
import gr.cite.user.model.builder.UserBuilder;
import gr.cite.user.model.censorship.UserCensor;
import gr.cite.user.model.persist.*;
import gr.cite.user.query.UserQuery;
import gr.cite.user.query.lookup.UserLookup;
import gr.cite.user.service.user.UserService;
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
@RequestMapping(path = "api/user")
public class UserController {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserController.class));

	private final BuilderFactory builderFactory;
	private final AuditService auditService;
	private final UserService userService;
	private final CensorFactory censorFactory;
	private final QueryFactory queryFactory;
	private final MessageSource messageSource;

	@Autowired
	private TenantScope tenantScope;

	@Autowired
	public UserController(
			BuilderFactory builderFactory,
			AuditService auditService,
			UserService userService,
			CensorFactory censorFactory,
			QueryFactory queryFactory,
			MessageSource messageSource

	) {
		this.builderFactory = builderFactory;
		this.auditService = auditService;
		this.userService = userService;
		this.censorFactory = censorFactory;
		this.queryFactory = queryFactory;
		this.messageSource = messageSource;
	}

	@PostMapping("query")
	public QueryResult<User> Query(@RequestBody UserLookup lookup) throws MyApplicationException, MyForbiddenException {
		logger.debug("querying {}", User.class.getSimpleName());
		this.censorFactory.censor(UserCensor.class).censor(lookup.getProject(), null);
		UserQuery query = lookup.enrich(this.queryFactory).authorize(AuthorizationFlags.OwnerOrPermission);
		List<UserEntity> datas = query.collectAs(lookup.getProject());
		List<User> models = this.builderFactory.builder(UserBuilder.class).authorize(AuthorizationFlags.OwnerOrPermission).build(lookup.getProject(), datas);
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

		this.auditService.track(AuditableAction.User_Query, "lookup", lookup);
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return new QueryResult<User>(models, count);
	}

	@GetMapping("{id}")
	@Transactional
	public User Get(@PathVariable("id") UUID id, FieldSet fieldSet, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		logger.debug(new MapLogEntry("retrieving" + User.class.getSimpleName()).And("id", id).And("fields", fieldSet));

		this.censorFactory.censor(UserCensor.class).censor(fieldSet, id);

		UserQuery query = this.queryFactory.query(UserQuery.class).authorize(AuthorizationFlags.OwnerOrPermission).ids(id);
		User model = this.builderFactory.builder(UserBuilder.class).authorize(AuthorizationFlags.OwnerOrPermission).build(fieldSet, query.firstAs(fieldSet));
		if (model == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, User.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.User_Lookup, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("id", id),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}


	@PostMapping("language")
	@Transactional
	public User updateLanguage(@MyValidate @RequestBody UserProfileLanguagePatch model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("update Language" + User.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		User persisted = this.userService.updateLanguage(model, fieldSet);

		this.auditService.track(AuditableAction.User_Persist, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return persisted;
	}

	@PostMapping("name/update")
	@Transactional
	public User updateName(@MyValidate @RequestBody UserNamePatch model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("update Name" + User.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		User persisted = this.userService.updateName(model, fieldSet);

		this.auditService.track(AuditableAction.User_Persist, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return persisted;
	}

	@PostMapping("profile/update")
	@Transactional
	public User updateUserProfile(@MyValidate @RequestBody UserProfilePatch model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("update userProfile" + User.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		User persisted = this.userService.updateUserProfile(model, fieldSet);

		this.auditService.track(AuditableAction.User_Persist, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return persisted;
	}

	@GetMapping("profile/{id}")
	@Transactional
	public User Get(@PathVariable("id") UUID id, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		logger.debug(new MapLogEntry("retrieving profile" + User.class.getSimpleName()).And("id", id).And("fields", fieldSet));

		this.censorFactory.censor(UserCensor.class).censor(fieldSet, id);

		UserQuery query = this.queryFactory.query(UserQuery.class).authorize(AuthorizationFlags.OwnerOrPermission).ids(id);
		User model = this.builderFactory.builder(UserBuilder.class).authorize(AuthorizationFlags.OwnerOrPermission).build(fieldSet, query.firstAs(fieldSet));
		if (model == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, User.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.User_Lookup, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("id", id),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}

	@PostMapping("contacts/update")
	@Transactional
	public User updateUserContactInfo(@MyValidate @RequestBody UserContactInfoPatch model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("update contact Info" + User.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		User persisted = this.userService.persistUserContactInfo(model, fieldSet);

		this.auditService.track(AuditableAction.User_Persist, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return persisted;
	}

	@PostMapping("persist")
	@Transactional
	public User Persist(@MyValidate @RequestBody UserPersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting" + User.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		User persisted = this.userService.persist(model, fieldSet);

		this.auditService.track(AuditableAction.User_Persist, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return persisted;
	}

	@DeleteMapping("{id}")
	@Transactional
	public void Delete(@PathVariable("id") UUID id) throws MyForbiddenException, InvalidApplicationException {
		logger.debug(new MapLogEntry("retrieving" + User.class.getSimpleName()).And("id", id));

		this.userService.deleteAndSave(id);

		this.auditService.track(AuditableAction.User_Delete, "id", id);
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
	}


}
