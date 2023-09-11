package gr.cite.intelcomp.stiviewer.web.controllers;

import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.IndicatorAccess;
import gr.cite.intelcomp.stiviewer.model.persist.UserAddAccessToIndicatorColumn;
import gr.cite.intelcomp.stiviewer.service.indicatoraccess.IndicatorAccessService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InvalidApplicationException;
import javax.transaction.Transactional;
import java.util.AbstractMap;
import java.util.Map;

@RestController
@RequestMapping(path = "api/indicator-access")
public class IndicatorAccessController {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(IndicatorAccessController.class));

    private final AuditService auditService;

    private final IndicatorAccessService indicatorAccessService;

    @Autowired
    public IndicatorAccessController(
            AuditService auditService,
            IndicatorAccessService indicatorAccessService) {
        this.auditService = auditService;
        this.indicatorAccessService = indicatorAccessService;
    }

    @PostMapping("user-add-access-to-indicator-column")
    @Transactional
    public IndicatorAccess persist(@MyValidate @RequestBody UserAddAccessToIndicatorColumn model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
        logger.debug(new MapLogEntry("persisting" + Indicator.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));
        IndicatorAccess persisted = this.indicatorAccessService.persist(model, fieldSet);

        this.auditService.track(AuditableAction.Indicator_Access_UserAddAccessToIndicatorColumn, Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("model", model),
                new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
        ));
        return persisted;
    }
}
