package gr.cite.intelcomp.stiviewer.web.controllers;

import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.data.MasterItemEntity;
import gr.cite.intelcomp.stiviewer.model.MasterItem;
import gr.cite.intelcomp.stiviewer.model.builder.MasterItemBuilder;
import gr.cite.intelcomp.stiviewer.model.censorship.MasterItemCensor;
import gr.cite.intelcomp.stiviewer.model.persist.MasterItemPersist;
import gr.cite.intelcomp.stiviewer.query.MasterItemQuery;
import gr.cite.intelcomp.stiviewer.query.lookup.MasterItemLookup;
import gr.cite.intelcomp.stiviewer.service.masteritem.MasterItemService;
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
@RequestMapping(path = "api/master-item")
public class MasterItemController {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(MasterItemController.class));

	private final BuilderFactory builderFactory;
	private final AuditService auditService;
	private final MasterItemService masterItemService;
	private final CensorFactory censorFactory;
	private final QueryFactory queryFactory;
	private final MessageSource messageSource;

	@Autowired
	public MasterItemController(
			BuilderFactory builderFactory,
			AuditService auditService,
			MasterItemService masterItemService,
			CensorFactory censorFactory,
			QueryFactory queryFactory,
			MessageSource messageSource

	) {
		this.builderFactory = builderFactory;
		this.auditService = auditService;
		this.masterItemService = masterItemService;
		this.censorFactory = censorFactory;
		this.queryFactory = queryFactory;
		this.messageSource = messageSource;
	}

	@PostMapping("query")
	public QueryResult<MasterItem> Query(@RequestBody MasterItemLookup lookup) throws MyApplicationException, MyForbiddenException {
		logger.debug("querying {}", MasterItem.class.getSimpleName());

		this.censorFactory.censor(MasterItemCensor.class).censor(lookup.getProject());

		MasterItemQuery query = lookup.enrich(this.queryFactory);
		List<MasterItemEntity> datas = query.collectAs(lookup.getProject());
		List<MasterItem> models = this.builderFactory.builder(MasterItemBuilder.class).build(lookup.getProject(), datas);
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();

		this.auditService.track(AuditableAction.MasterItem_Query, "lookup", lookup);
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return new QueryResult(models, count);
	}

	@GetMapping("{id}")
	public MasterItem Get(@PathVariable("id") UUID id, FieldSet fieldSet, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		logger.debug(new MapLogEntry("retrieving" + MasterItem.class.getSimpleName()).And("id", id).And("fields", fieldSet));

		this.censorFactory.censor(MasterItemCensor.class).censor(fieldSet);

		MasterItemQuery query = this.queryFactory.query(MasterItemQuery.class).ids(id);
		MasterItem model = this.builderFactory.builder(MasterItemBuilder.class).build(fieldSet, query.firstAs(fieldSet));
		if (model == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, MasterItem.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.MasterItem_Lookup, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("id", id),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}

	@PostMapping("persist")
	@Transactional
	public MasterItem Persist(@MyValidate @RequestBody MasterItemPersist model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting" + MasterItem.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		MasterItem persisted = this.masterItemService.persist(model, fieldSet);

		this.auditService.track(AuditableAction.MasterItem_Persist, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return persisted;
	}

	@DeleteMapping("{id}")
	@Transactional
	public void Delete(@PathVariable("id") UUID id) throws MyForbiddenException, InvalidApplicationException {
		logger.debug(new MapLogEntry("retrieving" + MasterItem.class.getSimpleName()).And("id", id));

		this.masterItemService.deleteAndSave(id);

		this.auditService.track(AuditableAction.MasterItem_Delete, "id", id);
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
	}


}
