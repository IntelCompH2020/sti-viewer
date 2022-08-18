package gr.cite.intelcomp.stiviewer.model.censorship;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.tools.data.censor.CensorFactory;
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
public class IndicatorPointCensor extends BaseCensor {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(IndicatorPointCensor.class));
	private final AuthorizationService authService;
	private final CensorFactory censorFactory;

	@Autowired
	public IndicatorPointCensor(ConventionService conventionService, AuthorizationService authService, CensorFactory censorFactory) {
		super(conventionService);
		this.authService = authService;
		this.censorFactory = censorFactory;
	}

	public void censor(FieldSet fields) {
		logger.debug(new DataLogEntry("censoring fields", fields));
		if (this.isEmpty(fields)) return;
		this.authService.authorizeForce(Permission.BrowseIndicatorPoint, Permission.DeferredAffiliation);
	}
}
