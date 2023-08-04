package gr.cite.intelcomp.stiviewer.web.controllers;

import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.model.UserSettings;
import gr.cite.intelcomp.stiviewer.model.elasticreport.AggregateResponseModel;
import gr.cite.intelcomp.stiviewer.model.shared.DashboardLookup;
import gr.cite.intelcomp.stiviewer.model.shared.PublicIndicatorPointReportLookup;
import gr.cite.intelcomp.stiviewer.model.indicatorpoint.IndicatorPoint;
import gr.cite.intelcomp.stiviewer.service.dashboard.DashboardService;
import gr.cite.intelcomp.stiviewer.service.indicatorpoint.IndicatorPointService;
import gr.cite.tools.auditing.AuditService;
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
import javax.naming.OperationNotSupportedException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@RestController
@RequestMapping(path = "api/public")
public class PublicController {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(PublicController.class));

	private final AuditService auditService;
	private final IndicatorPointService indicatorPointService;
	private final DashboardService dashboardService;
	private final MessageSource messageSource;

	@Autowired
	public PublicController(
			AuditService auditService,
			IndicatorPointService indicatorPointService,
			DashboardService dashboardService, 
			MessageSource messageSource
	) {
		this.auditService = auditService;
		this.indicatorPointService = indicatorPointService;
		this.dashboardService = dashboardService;
		this.messageSource = messageSource;
	}

	@PostMapping("indicator-point/report")
	public AggregateResponseModel reportPublic(@MyValidate @RequestBody PublicIndicatorPointReportLookup model, FieldSet fieldSet) throws InvalidApplicationException, NoSuchAlgorithmException, OperationNotSupportedException {
		logger.debug(new MapLogEntry("persisting" + IndicatorPoint.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));

		AggregateResponseModel report = this.indicatorPointService.reportPublic(model.getIndicatorId(), model, fieldSet);

		this.auditService.track(AuditableAction.Public_Indicator_Point_Report, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return report;
	}


	@PostMapping("dashboard/lookup")
	public String dashboardLookup(@MyValidate @RequestBody DashboardLookup lookup) throws MyApplicationException, MyForbiddenException, MyNotFoundException, NoSuchAlgorithmException, OperationNotSupportedException {
		logger.debug(new MapLogEntry("retrieving" + DashboardLookup.class.getSimpleName()).And("lookup", lookup));

		String model = this.dashboardService.getPublicDashboard(lookup);
		if (model == null || model.isBlank()) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{lookup.getDashboardId(), UserSettings.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		this.auditService.track(AuditableAction.Indicator_Lookup, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("lookup", lookup)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return model;
	}
}
