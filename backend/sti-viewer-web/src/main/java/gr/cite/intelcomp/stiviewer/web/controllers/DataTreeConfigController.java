package gr.cite.intelcomp.stiviewer.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.common.types.datatreeconfig.DataTreeLevelItemEntity;
import gr.cite.intelcomp.stiviewer.model.censorship.datatreeconfig.BrowseDataTreeConfigCensor;
import gr.cite.intelcomp.stiviewer.model.censorship.datatreeconfig.BrowseDataTreeLevelItemCensor;
import gr.cite.intelcomp.stiviewer.model.censorship.portofolioconfig.PortofolioConfigCensor;
import gr.cite.intelcomp.stiviewer.model.datatreeconfig.DataTreeConfig;
import gr.cite.intelcomp.stiviewer.model.datatreeconfig.DataTreeLevel;
import gr.cite.intelcomp.stiviewer.model.persist.MasterItemPersist;
import gr.cite.intelcomp.stiviewer.model.persist.UpdateDataTreeLastAccess;
import gr.cite.intelcomp.stiviewer.model.portofolioconfig.PortofolioConfig;
import gr.cite.intelcomp.stiviewer.query.lookup.IndicatorReportLevelLookup;
import gr.cite.intelcomp.stiviewer.service.datatreeconfig.DataTreeConfigService;
import gr.cite.intelcomp.stiviewer.web.model.QueryResult;
import gr.cite.tools.auditing.AuditService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.censor.CensorFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import gr.cite.tools.validation.MyValidate;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.management.InvalidApplicationException;
import javax.transaction.Transactional;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/data-tree-config")
public class DataTreeConfigController {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(DataTreeConfigController.class));

	private final BuilderFactory builderFactory;
	private final AuditService auditService;
	private final DataTreeConfigService dataTreeConfigService;
	private final CensorFactory censorFactory;


	public DataTreeConfigController(BuilderFactory builderFactory,
	                                AuditService auditService,
	                                DataTreeConfigService dataTreeConfigService,
	                                CensorFactory censorFactory
	) {
		this.builderFactory = builderFactory;
		this.auditService = auditService;
		this.dataTreeConfigService = dataTreeConfigService;
		this.censorFactory = censorFactory;
	}

	@GetMapping("my-configs")
	public List<DataTreeConfig> getMyConfigs(FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, InvalidApplicationException {
		logger.debug(new MapLogEntry("get my configs" + DataTreeConfig.class.getSimpleName()).And("fields", fieldSet));
		this.censorFactory.censor(BrowseDataTreeConfigCensor.class).censor(fieldSet);

		List<DataTreeConfig> models = dataTreeConfigService.getMyConfigs(fieldSet);
		this.auditService.track(AuditableAction.DataTreeConfig_MyConfigs, "fieldSet", fieldSet);
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return models;
	}

	@GetMapping("my-config/{key}")
	public List<DataTreeConfig> getMyConfigByKey(@PathVariable("key") String key, FieldSet fieldSet) throws MyApplicationException, MyForbiddenException, InvalidApplicationException {
		logger.debug(new MapLogEntry("get my config" + DataTreeConfig.class.getSimpleName()).And("key", key).And("fields", fieldSet));
		this.censorFactory.censor(BrowseDataTreeConfigCensor.class).censor(fieldSet);

		List<DataTreeConfig> models = this.dataTreeConfigService.getMyConfigByKey(key, fieldSet);
		this.auditService.track(AuditableAction.DataTreeConfig_GetMyConfigByKey, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("key", key),
				new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return models;
	}

	@PostMapping("update-last-access")
	@Transactional
	public void updateLastAccess(@MyValidate @RequestBody UpdateDataTreeLastAccess model) throws InvalidApplicationException, JsonProcessingException {
		logger.debug(new MapLogEntry("get update last access" + DataTreeConfig.class.getSimpleName()).And("model", model));

		this.dataTreeConfigService.updateLastAccess(model.getConfigId());
		this.auditService.track(AuditableAction.DataTreeConfig_UpdateLastAccess, Map.ofEntries(
				new AbstractMap.SimpleEntry<String, Object>("model", model)
		));
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);
		return;
	}

	@PostMapping("query-level")
	public QueryResult<DataTreeLevel> QueryLevel(@RequestBody IndicatorReportLevelLookup lookup) throws MyApplicationException, MyForbiddenException, InvalidApplicationException {
		logger.debug("querying {}", DataTreeLevelItemEntity.class.getSimpleName());
		this.censorFactory.censor(BrowseDataTreeLevelItemCensor.class).censor(lookup.getProject());

		DataTreeLevel models = dataTreeConfigService.getIndicatorReportLevel(lookup, lookup.getProject());
		this.auditService.track(AuditableAction.DataTreeConfig_QueryLevel, "lookup", lookup);
		//this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

		return new QueryResult<>(List.of(models), 1);
	}

}
