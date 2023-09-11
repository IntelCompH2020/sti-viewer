package gr.cite.intelcomp.stiviewer.web.controllers;

import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.UserSettings;
import gr.cite.intelcomp.stiviewer.service.dashboard.DashboardService;
import gr.cite.tools.auditing.AuditService;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InvalidApplicationException;
import java.util.AbstractMap;
import java.util.Map;

@RestController
@RequestMapping(path = "api/dashboard")
public class DashboardController {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(DashboardController.class));

    private final AuditService auditService;

    private final DashboardService dashboardService;

    private final MessageSource messageSource;

    @Autowired
    public DashboardController(
            AuditService auditService,
            DashboardService dashboardService,
            MessageSource messageSource
    ) {
        this.auditService = auditService;
        this.dashboardService = dashboardService;
        this.messageSource = messageSource;
    }

    @GetMapping("by-key/{key}")
    public String get(@PathVariable("key") String key) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("retrieving" + Indicator.class.getSimpleName()).And("key", key));

        String model = this.dashboardService.getPublicDashboard(key);
        if (model == null || model.isBlank())
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{key, UserSettings.class.getSimpleName()}, LocaleContextHolder.getLocale()));

        this.auditService.track(AuditableAction.Indicator_Lookup, Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("key", key)
        ));

        return model;
    }
}
