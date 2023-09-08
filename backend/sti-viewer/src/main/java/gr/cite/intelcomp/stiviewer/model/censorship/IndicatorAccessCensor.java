package gr.cite.intelcomp.stiviewer.model.censorship;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.IndicatorAccess;
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
public class IndicatorAccessCensor extends BaseCensor {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(IndicatorAccessCensor.class));

    protected final AuthorizationService authService;
    protected final CensorFactory censorFactory;

    @Autowired
    public IndicatorAccessCensor(
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
        if (this.isEmpty(fields))
            return;
        this.authService.authorizeForce(Permission.BrowseIndicatorAccess);
        FieldSet tenantFields = fields.extractPrefixed(this.asIndexerPrefix(IndicatorAccess._tenant));
        this.censorFactory.censor(TenantCensor.class).censor(tenantFields);
        FieldSet indicatorFields = fields.extractPrefixed(this.asIndexerPrefix(IndicatorAccess._indicator));
        this.censorFactory.censor(IndicatorCensor.class).censor(indicatorFields, userId);
        FieldSet userFields = fields.extractPrefixed(this.asIndexerPrefix(IndicatorAccess._user));
        this.censorFactory.censor(UserCensor.class).censor(userFields, userId);
    }

}
