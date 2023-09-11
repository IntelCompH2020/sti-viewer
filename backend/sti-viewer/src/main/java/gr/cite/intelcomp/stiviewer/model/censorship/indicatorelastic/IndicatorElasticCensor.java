package gr.cite.intelcomp.stiviewer.model.censorship.indicatorelastic;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.IndicatorElastic;
import gr.cite.intelcomp.stiviewer.model.censorship.BaseCensor;
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
public class IndicatorElasticCensor extends BaseCensor {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(IndicatorElasticCensor.class));

    protected AuthorizationService authService;

    protected final CensorFactory censorFactory;

    @Autowired
    public IndicatorElasticCensor(ConventionService conventionService, CensorFactory censorFactory, AuthorizationService authService) {
        super(conventionService);
        this.censorFactory = censorFactory;
        this.authService = authService;
    }

    public void censor(FieldSet fields) throws MyForbiddenException {
        logger.debug(new DataLogEntry("censoring fields", fields));
        if (fields.isEmpty())
            return;
        this.authService.authorizeForce(Permission.BrowseIndicatorElastic);
        FieldSet schemaFields = fields.extractPrefixed(this.asIndexerPrefix(IndicatorElastic._schema));
        this.censorFactory.censor(SchemaCensor.class).censor(schemaFields);
        FieldSet metadataFields = fields.extractPrefixed(this.asIndexerPrefix(IndicatorElastic._metadata));
        this.censorFactory.censor(MetadataCensor.class).censor(metadataFields);
    }

}
