package gr.cite.notification.web.controllers;

import gr.cite.notification.event.EventBroker;
import gr.cite.notification.service.dashboard.DashboardService;
import gr.cite.tools.auditing.AuditService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.censor.CensorFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/dashboard")
public class DashboardController {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(DashboardController.class));

	private final BuilderFactory builderFactory;
	private final AuditService auditService;
	private final DashboardService dashboardService;
	private final CensorFactory censorFactory;
	private final QueryFactory queryFactory;
	private final MessageSource messageSource;
	private final EventBroker eventBroker;

	private final ApplicationContext applicationContext;

	@Autowired
	public DashboardController(
			BuilderFactory builderFactory,
			AuditService auditService,
			DashboardService dashboardService,
			CensorFactory censorFactory,
			QueryFactory queryFactory,
			MessageSource messageSource,
			EventBroker eventBroker,
			ApplicationContext applicationContext
	) {
		this.builderFactory = builderFactory;
		this.auditService = auditService;
		this.dashboardService = dashboardService;
		this.censorFactory = censorFactory;
		this.queryFactory = queryFactory;
		this.messageSource = messageSource;
		this.eventBroker = eventBroker;
		this.applicationContext = applicationContext;
	}

	/*@GetMapping("by-key/{key}")
	@Transactional
	public String Get(@PathVariable("key") String key) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
		//logger.debug(new MapLogEntry("retrieving" + Indicator.class.getSimpleName()).And("key", key));

		String model = this.dashboardService.getDashboard(key);
		if (model == null || model.isBlank()) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{key, UserSettings.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.Indicator_Lookup, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("key", key)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}*/
}
