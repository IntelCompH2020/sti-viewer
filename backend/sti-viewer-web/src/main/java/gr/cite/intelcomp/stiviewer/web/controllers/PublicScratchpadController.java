package gr.cite.intelcomp.stiviewer.web.controllers;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.commons.web.oidc.apikey.events.ApiKeyStaleEvent;
import gr.cite.intelcomp.stiviewer.data.DetailItemEntity;
import gr.cite.intelcomp.stiviewer.data.MasterItemEntity;
import gr.cite.intelcomp.stiviewer.event.EventBroker;
import gr.cite.intelcomp.stiviewer.model.persist.DatasetPersist;
import gr.cite.intelcomp.stiviewer.query.DetailItemQuery;
import gr.cite.intelcomp.stiviewer.query.MasterItemQuery;
import gr.cite.intelcomp.stiviewer.query.lookup.DetailItemLookup;
import gr.cite.intelcomp.stiviewer.query.lookup.MasterItemLookup;
import gr.cite.intelcomp.stiviewer.service.dataset.DatasetService;
import gr.cite.intelcomp.stiviewer.web.model.ParentClassTest;
import gr.cite.intelcomp.stiviewer.web.model.QueryResult;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.censor.CensorFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.validation.MyValidate;
import gr.cite.tools.validation.ValidationService;
import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Tuple;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;

@RestController
@RequestMapping(path = "public")
@Hidden
public class PublicScratchpadController {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(PublicScratchpadController.class));

	@Autowired
	private BuilderFactory builderFactory;

	@Autowired
	private DatasetService datasetService;

	@Autowired
	private CensorFactory censorFactory;

	@Autowired
	private QueryFactory queryFactory;

	@Autowired
	private AuthorizationService authorizationService;

	public PublicScratchpadController(BuilderFactory builderFactory) {
		this.builderFactory = builderFactory;
	}

	@GetMapping("/hello-there")
	public String sayHello(@RequestParam(value = "myName", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	@GetMapping("/hello")
	public String sayHello() {

		logger.debug("log test");
		return "Hello!";
	}

	@GetMapping("/fieldset")
	public String sayHello(FieldSet fieldSet) {
		return String.join(",", fieldSet.getFields());
	}

	@PostMapping("/master/query")
	public QueryResult<MasterItemEntity> queryMaster(@RequestBody MasterItemLookup lookup) throws MyApplicationException {

		logger.debug(new DataLogEntry("requesting master items with lookup:", lookup));

		MasterItemQuery query = lookup.enrich(this.queryFactory);
		List<MasterItemEntity> models = query.collect();
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();
		return new QueryResult<>(models, count);
	}

	@PostMapping("/master/query-projected")
	public QueryResult<Map<String, Object>> queryMasterProjected(@RequestBody MasterItemLookup lookup) throws MyApplicationException {
		MasterItemQuery query = lookup.enrich(this.queryFactory);
		List<Tuple> items = query.collectProjected(lookup.getProject());

		//This is not entirely correct because the tupple named will be based on the data not on the model
		List<Map<String, Object>> models = new ArrayList<>();
		for (Tuple t : items) {
			Map<String, Object> d = new HashMap<>();
			for (String field : lookup.getProject().getFields()) {
				d.put(field, t.get(field));
			}
			models.add(d);
		}

		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();
		return new QueryResult<>(models, count);
	}

	@PostMapping("/master/query-as")
	public QueryResult<MasterItemEntity> queryMasterAs(@RequestBody MasterItemLookup lookup) throws MyApplicationException {
		MasterItemQuery query = lookup.enrich(this.queryFactory);
		List<MasterItemEntity> models = query.collectAs(lookup.getProject());
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();
		return new QueryResult<>(models, count);
	}

	@PostMapping("/detail/query")
	public QueryResult<DetailItemEntity> queryDetail(@RequestBody DetailItemLookup lookup) throws MyApplicationException {
		DetailItemQuery query = lookup.enrich(this.queryFactory);
		List<DetailItemEntity> models = query.collect();
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();
		return new QueryResult<>(models, count);
	}

	@PostMapping("/detail/query-as")
	public QueryResult<DetailItemEntity> queryDetailAs(@RequestBody DetailItemLookup lookup) throws MyApplicationException {
		DetailItemQuery query = lookup.enrich(this.queryFactory);
		List<DetailItemEntity> models = query.collectAs(lookup.getProject());
		long count = (lookup.getMetadata() != null && lookup.getMetadata().getCountAll()) ? query.count() : models.size();
		return new QueryResult<>(models, count);
	}

	@PostMapping("/validate/example")
	public Boolean validationTest(@MyValidate @RequestBody ParentClassTest item, FieldSet fieldSet) {
		System.out.println(item.getId());
		return true;
	}

	@Autowired
	private EventBroker eventBroker;

	@GetMapping("/apikey/stale")
	public Boolean staleApiKey() {
		eventBroker.emit(new ApiKeyStaleEvent("5d804531d24e42eb95b81e532f2906c2"));
		return true;
	}

	@Autowired
	ValidationService validation;

	@GetMapping("/exception/test")
	public Boolean exceptionTest() {
		//throw new MyApplicationException(5, "test message");
		DatasetPersist model = new DatasetPersist();
		model.setId(UUID.randomUUID());
		validation.validateForce(model);
		return true;
	}

	@GetMapping("/authorization/example-with-service")
	public Boolean authorizationExampleUsingService() {
		return authorizationService.authorize(
				"browseDataset"
		);
	}

	@GetMapping("/authorization/example-with-service-and-dynamic-requirement")
	public Boolean authorizationExampleUsingServiceAndDynamicRequirement() {
//		return authorizationService.authorize(
//				List.of(new TimeOfDayAuthorizationRequirement(new TimeOfDay("19:00","21:00"), true)),
//				"browseDataset"
//		);
		return false;
	}

	//SECURITY ANNOTATION EXAMPLE ----------------------------------------------------------------------------

	/**
	 * You should not get authorized here if your user roles do not yield the 'browseDataset' permission.
	 * It should return code 401 if decorated with the security annotation {@link CanBrowseDataset}
	 */
	@GetMapping("/authorization/example-with-annotation")
	@CanBrowseDataset
	public String authorizationExample() {
		return "Authorized!!";
	}

	/**
	 * This is a helper annotation wrapping a security annotation inside<br>
	 * This is a test and should be implemented on different files if needed<br>
	 * It is useful if the same annotations are getting used on multiple places
	 */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("@authorizationService.authorizeForce(\"browseDataset\")")
	public @interface CanBrowseDataset {
	}
}
