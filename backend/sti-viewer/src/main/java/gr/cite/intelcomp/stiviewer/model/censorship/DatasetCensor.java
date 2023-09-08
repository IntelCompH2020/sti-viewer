package gr.cite.intelcomp.stiviewer.model.censorship;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
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
public class DatasetCensor extends BaseCensor {
    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(DatasetCensor.class));
    private final AuthorizationService authService;

    @Autowired
    public DatasetCensor(ConventionService conventionService,
                         AuthorizationService authService
    ) {
        super(conventionService);
        this.authService = authService;
    }

    public void censor(FieldSet fields) throws MyForbiddenException {
        logger.debug(new DataLogEntry("censoring fields", fields));
        if (this.isEmpty(fields))
            return;
        this.authService.authorizeForce(Permission.BrowseDataset);
    }
}
