package gr.cite.notification.model.censorship.datatreeconfig;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.notification.authorization.Permission;
import gr.cite.notification.convention.ConventionService;
import gr.cite.notification.model.censorship.BaseCensor;
import gr.cite.notification.model.datatreeconfig.BrowseDataTreeConfigModel;
import gr.cite.tools.data.censor.CensorFactory;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BrowseDataTreeConfigCensor extends BaseCensor {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(BrowseDataTreeConfigCensor.class));

	protected final AuthorizationService authService;
	protected final CensorFactory censorFactory;

	@Autowired
	public BrowseDataTreeConfigCensor(
			ConventionService conventionService,
			AuthorizationService authService,
			CensorFactory censorFactory
	) {
		super(conventionService);
		this.authService = authService;
		this.censorFactory = censorFactory;
	}

	public void censor(FieldSet fields) throws MyForbiddenException {
		logger.debug(new DataLogEntry("censoring fields", fields));
		if (this.isEmpty(fields)) return;
		this.authService.authorizeForce(Permission.BrowseBrowseDataTreeConfig);
		FieldSet levelConfigFields = fields.extractPrefixed(this.asIndexerPrefix(BrowseDataTreeConfigModel._levelConfigs));
		this.censorFactory.censor(BrowseDataTreeLevelConfigCensor.class).censor(levelConfigFields);
	}

}
