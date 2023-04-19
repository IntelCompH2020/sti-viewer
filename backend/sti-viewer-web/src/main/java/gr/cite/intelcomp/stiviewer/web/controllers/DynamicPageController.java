package gr.cite.intelcomp.stiviewer.web.controllers;

import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.data.DynamicPageEntity;
import gr.cite.intelcomp.stiviewer.model.DynamicPage;
import gr.cite.intelcomp.stiviewer.model.DynamicPageContentData;
import gr.cite.intelcomp.stiviewer.model.PageContentRequest;
import gr.cite.intelcomp.stiviewer.model.DynamicPageMenuItem;
import gr.cite.intelcomp.stiviewer.model.builder.DynamicPageBuilder;
import gr.cite.intelcomp.stiviewer.model.censorship.DynamicPageCensor;
import gr.cite.intelcomp.stiviewer.model.persist.PagePersist;
import gr.cite.intelcomp.stiviewer.query.DynamicPageQuery;
import gr.cite.intelcomp.stiviewer.query.lookup.DynamicPageLookup;
import gr.cite.intelcomp.stiviewer.service.dynamicpage.DynamicPageService;
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
@RequestMapping(path = "api/dynamic-page")
public class DynamicPageController {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(DynamicPageController.class));

	private final BuilderFactory builderFactory;
	private final AuditService auditService;
	private final DynamicPageService dynamicPageService;
	private final CensorFactory censorFactory;
	private final QueryFactory queryFactory;
	private final MessageSource messageSource;

	@Autowired
	public DynamicPageController(
			BuilderFactory builderFactory,
			AuditService auditService,
			DynamicPageService dynamicPageService,
			CensorFactory censorFactory,
			QueryFactory queryFactory,
			MessageSource messageSource

	) {
		this.builderFactory = builderFactory;
		this.auditService = auditService;
		this.dynamicPageService = dynamicPageService;
		this.censorFactory = censorFactory;
		this.queryFactory = queryFactory;
		this.messageSource = messageSource;
	}

	@PostMapping("query")
	public QueryResult<DynamicPage> Query(@RequestBody DynamicPageLookup lookup) throws MyApplicationException, MyForbiddenException {
		logger.debug("querying {}", DynamicPage.class.getSimpleName());

		this.censorFactory.censor(DynamicPageCensor.class).censor(lookup.getProject(), null);

		DynamicPageQuery query = lookup.enrich(this.queryFactory);
		List<DynamicPageEntity> datas = query.collectAs(lookup.getProject());
		List<DynamicPage> models = this.builderFactory.builder(DynamicPageBuilder.class).build(lookup.getProject(), datas);
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

		this.auditService.track(AuditableAction.DynamicPage_Query, "lookup", lookup);
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return new QueryResult(models, count);
	}

	@GetMapping("{id}")
	public DynamicPage Get(@PathVariable("id") UUID id, FieldSet fieldSet, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		logger.debug(new MapLogEntry("retrieving" + DynamicPage.class.getSimpleName()).And("id", id).And("fields", fieldSet));

		this.censorFactory.censor(DynamicPageCensor.class).censor(fieldSet, null);

		DynamicPageQuery query = this.queryFactory.query(DynamicPageQuery.class).ids(id);
		DynamicPage model = this.builderFactory.builder(DynamicPageBuilder.class).build(fieldSet, query.firstAs(fieldSet));
		if (model == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, DynamicPage.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.DynamicPage_Lookup, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("id", id),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}

	@GetMapping("allowed-menu/{language}")
	public List<DynamicPageMenuItem> getAllowedPageMenuItems(@PathVariable("language") String language, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		logger.debug(new MapLogEntry("retrieving" + DynamicPageMenuItem.class.getSimpleName()).And("language", language));

		List<DynamicPageMenuItem> model = this.dynamicPageService.getAllowedPageMenuItems(language);

		this.auditService.track(AuditableAction.DynamicPage_AllowedPageMenuItems, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("language", language)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}

	@PostMapping("content")
	public DynamicPageContentData getPageContent(@MyValidate @RequestBody  PageContentRequest model, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		logger.debug(new MapLogEntry("retrieving" + DynamicPageContentData.class.getSimpleName()).And("model", model));

		DynamicPageContentData contentData = this.dynamicPageService.getPageContent(model);

		this.auditService.track(AuditableAction.DynamicPage_GetPageContent, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return contentData;
	}

	@PostMapping("persist")
	@Transactional
	public DynamicPage Persist(@MyValidate @RequestBody PagePersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting" + DynamicPage.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		DynamicPage persisted = this.dynamicPageService.persist(model, fieldSet);

		this.auditService.track(AuditableAction.DynamicPage_Persist, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return persisted;
	}

	@DeleteMapping("{id}")
	@Transactional
	public void Delete(@PathVariable("id") UUID id) throws MyForbiddenException, InvalidApplicationException {
		logger.debug(new MapLogEntry("retrieving" + DynamicPage.class.getSimpleName()).And("id", id));

		this.dynamicPageService.deleteAndSave(id);

		this.auditService.track(AuditableAction.DynamicPage_Delete, "id", id);
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
	}


}
