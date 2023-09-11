package gr.cite.intelcomp.stiviewer.model.censorship;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.OwnedResource;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.User;
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

import java.util.List;
import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserCensor extends BaseCensor {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserCensor.class));

    protected final AuthorizationService authService;

    protected final CensorFactory censorFactory;

    @Autowired
    public UserCensor(
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
        this.authService.authorizeAtLeastOneForce(userId != null ? List.of(new OwnedResource(userId)) : null, Permission.BrowseUser);
        FieldSet tenantUsersFields = fields.extractPrefixed(this.asIndexerPrefix(User._tenantUsers));
        this.censorFactory.censor(TenantUserCensor.class).censor(tenantUsersFields, userId);
        FieldSet indicatorAccessesFields = fields.extractPrefixed(this.asIndexerPrefix(User._indicatorAccesses));
        this.censorFactory.censor(IndicatorAccessCensor.class).censor(indicatorAccessesFields, userId);
    }
}
