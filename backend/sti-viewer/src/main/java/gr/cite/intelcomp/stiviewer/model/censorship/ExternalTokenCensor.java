package gr.cite.intelcomp.stiviewer.model.censorship;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.ExternalToken;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequest;
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

import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExternalTokenCensor extends BaseCensor {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(ExternalTokenCensor.class));
	private final AuthorizationService authService;
	private final CensorFactory censorFactory;

	@Autowired
	public ExternalTokenCensor(
			ConventionService conventionService,
			AuthorizationService authService,
			CensorFactory censorFactory
	) {
		super(conventionService);
		this.authService = authService;
		this.censorFactory = censorFactory;
	}

	public void censor(FieldSet fields, UUID userId) throws MyForbiddenException {
		logger.debug(new DataLogEntry("censoring fields", fields));
		if (this.isEmpty(fields)) return;
		this.authService.authorizeForce(Permission.BrowseExternalToken);
		FieldSet tenantFields = fields.extractPrefixed(this.asIndexerPrefix(ExternalToken._tenant));
		this.censorFactory.censor(TenantCensor.class).censor(tenantFields);
		FieldSet ownerFields = fields.extractPrefixed(this.asIndexerPrefix(ExternalToken._owner));
		this.censorFactory.censor(UserCensor.class).censor(ownerFields, userId);
	}
}