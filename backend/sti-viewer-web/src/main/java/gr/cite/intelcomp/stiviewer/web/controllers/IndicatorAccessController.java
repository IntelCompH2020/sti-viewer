package gr.cite.intelcomp.stiviewer.web.controllers;

import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.data.IndicatorEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorElasticEntity;
import gr.cite.intelcomp.stiviewer.elastic.query.indicator.IndicatorElasticQuery;
import gr.cite.intelcomp.stiviewer.elastic.query.lookup.IndicatorElasticLookup;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.IndicatorAccess;
import gr.cite.intelcomp.stiviewer.model.IndicatorElastic;
import gr.cite.intelcomp.stiviewer.model.builder.IndicatorBuilder;
import gr.cite.intelcomp.stiviewer.model.builder.IndicatorElasticBuilder;
import gr.cite.intelcomp.stiviewer.model.censorship.IndicatorCensor;
import gr.cite.intelcomp.stiviewer.model.censorship.indicatorelastic.IndicatorElasticCensor;
import gr.cite.intelcomp.stiviewer.model.persist.IndicatorElasticPersist;
import gr.cite.intelcomp.stiviewer.model.persist.IndicatorPersist;
import gr.cite.intelcomp.stiviewer.model.persist.ResetIndicatorElasticPersist;
import gr.cite.intelcomp.stiviewer.model.persist.UserAddAccessToIndicatorColumn;
import gr.cite.intelcomp.stiviewer.query.IndicatorQuery;
import gr.cite.intelcomp.stiviewer.query.lookup.IndicatorLookup;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorService;
import gr.cite.intelcomp.stiviewer.service.indicatoraccess.IndicatorAccessService;
import gr.cite.intelcomp.stiviewer.service.indicatorelastic.ElasticIndicatorService;
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
import java.io.IOException;
import java.util.*;

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
	public IndicatorAccess Persist(@MyValidate @RequestBody UserAddAccessToIndicatorColumn model, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting" + Indicator.class.getSimpleName()).And("model", model).And("fieldSet", fieldSet));
		IndicatorAccess persisted = this.indicatorAccessService.persist(model, fieldSet);

		this.auditService.track(AuditableAction.Indicator_Access_UserAddAccessToIndicatorColumn, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return persisted;
	}
}
