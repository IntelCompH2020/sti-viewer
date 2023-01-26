package gr.cite.intelcomp.stiviewer.web.controllers;

import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.model.censorship.portofolioconfig.PortofolioConfigCensor;
import gr.cite.intelcomp.stiviewer.model.portofolioconfig.PortofolioConfig;
import gr.cite.intelcomp.stiviewer.service.portofolioconfig.PortofolioConfigService;
import gr.cite.tools.auditing.AuditService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.censor.CensorFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.management.InvalidApplicationException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/portofolio-config")
public class PortofolioConfigController {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(PortofolioConfigController.class));

	private final BuilderFactory builderFactory;
	private final AuditService auditService;
	private final PortofolioConfigService portofolioConfigService;
	private final CensorFactory censorFactory;


	public PortofolioConfigController(BuilderFactory builderFactory,
	                                  AuditService auditService,
	                                  PortofolioConfigService portofolioConfigService, 
	                                  CensorFactory censorFactory
	) {
		this.builderFactory = builderFactory;
		this.auditService = auditService;
		this.portofolioConfigService = portofolioConfigService;
		this.censorFactory = censorFactory;
	}

	@GetMapping("my-configs")
	public List<PortofolioConfig> getMyConfigs(FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, InvalidApplicationException {
		logger.debug(new MapLogEntry("get my configs" + PortofolioConfig.class.getSimpleName()).And("fields", fieldSet));
		this.censorFactory.censor(PortofolioConfigCensor.class).censor(fieldSet);

		List<PortofolioConfig> models = this.portofolioConfigService.getMyConfigs(fieldSet);
		this.auditService.track(AuditableAction.PortofolioConfig_GetMyConfigs, "fieldSet", fieldSet);
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return models;
	}

	@GetMapping("my-config/{key}")
	public PortofolioConfig getMyConfigByKey(@PathVariable("key") String key, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, InvalidApplicationException {
		logger.debug(new MapLogEntry("get my config" + PortofolioConfig.class.getSimpleName()).And("key", key).And("fields", fieldSet));
		this.censorFactory.censor(PortofolioConfigCensor.class).censor(fieldSet);

		PortofolioConfig models = this.portofolioConfigService.getMyConfigByKey(key, fieldSet);
		this.auditService.track(AuditableAction.PortofolioConfig_GetMyConfigByKey, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("key", key),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return models;
	}
}
