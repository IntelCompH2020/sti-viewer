package gr.cite.intelcomp.stiviewer.model.censorship.indicatorgroup;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.IndicatorGroup;
import gr.cite.intelcomp.stiviewer.model.censorship.BaseCensor;
import gr.cite.intelcomp.stiviewer.model.censorship.IndicatorCensor;
import gr.cite.intelcomp.stiviewer.model.censorship.IndicatorGroupCensor;
import gr.cite.intelcomp.stiviewer.model.indicatorgroup.IndicatorGroupAccessConfigView;
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
public class IndicatorGroupAccessConfigViewCensor extends BaseCensor {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(IndicatorGroupAccessConfigViewCensor.class));

	protected final AuthorizationService authService;
	protected final CensorFactory censorFactory;

	@Autowired
	public IndicatorGroupAccessConfigViewCensor(
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
		this.authService.authorizeForce(Permission.BrowseIndicator);
		FieldSet groupFields = fields.extractPrefixed(this.asIndexerPrefix(IndicatorGroupAccessConfigView._group));
		this.censorFactory.censor(IndicatorGroupCensor.class).censor(groupFields, userId);
		FieldSet filterColumnsFields = fields.extractPrefixed(this.asIndexerPrefix(IndicatorGroupAccessConfigView._filterColumns));
		this.censorFactory.censor(IndicatorGroupAccessColumnConfigViewCensor.class).censor(filterColumnsFields, userId);
	}

}
