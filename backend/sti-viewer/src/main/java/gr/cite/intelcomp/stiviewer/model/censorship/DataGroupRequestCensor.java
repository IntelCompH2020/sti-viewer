package gr.cite.intelcomp.stiviewer.model.censorship;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.datagrouprequest.DataGroupRequest;
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
public class DataGroupRequestCensor extends BaseCensor {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(DataGroupRequestCensor.class));

	protected final AuthorizationService authService;
	protected final CensorFactory censorFactory;

	@Autowired
	public DataGroupRequestCensor(
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
		this.authService.authorizeForce(Permission.BrowseDataGroupRequest);
		FieldSet tenantFields = fields.extractPrefixed(this.asIndexerPrefix(DataGroupRequest._tenant));
		this.censorFactory.censor(TenantCensor.class).censor(tenantFields);
		FieldSet userFields = fields.extractPrefixed(this.asIndexerPrefix(DataGroupRequest._user));
		this.censorFactory.censor(UserCensor.class).censor(userFields, userId);
		FieldSet configFields = fields.extractPrefixed(this.asIndexerPrefix(DataGroupRequest._config));
		this.censorFactory.censor(DataGroupRequestConfigCensor.class).censor(configFields, userId);
	}

}
