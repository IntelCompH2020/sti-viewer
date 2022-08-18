package gr.cite.intelcomp.stiviewer.service.dashboard;


import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationContentResolver;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.enums.UserSettingsEntityType;
import gr.cite.intelcomp.stiviewer.common.enums.UserSettingsType;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.data.UserSettingsEntity;
import gr.cite.intelcomp.stiviewer.model.UserSettings;
import gr.cite.intelcomp.stiviewer.query.UserSettingsQuery;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.fieldset.BaseFieldSet;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequestScope
public class DashboardServiceImpl implements DashboardService {

	private final TenantEntityManager entityManager;
	private final QueryFactory queryFactory;
	private final AuthorizationService authorizationService;
	private final AuthorizationContentResolver authorizationContentResolver;
	private final UserScope userScope;
	private final ConventionService conventionService;
	private final MessageSource messageSource;

	public DashboardServiceImpl(
			TenantEntityManager entityManager,
			QueryFactory queryFactory,
			AuthorizationService authorizationService,
			AuthorizationContentResolver authorizationContentResolver,
			UserScope userScope,
			ConventionService conventionService,
			MessageSource messageSource
	) {
		this.entityManager = entityManager;
		this.queryFactory = queryFactory;
		this.authorizationService = authorizationService;
		this.authorizationContentResolver = authorizationContentResolver;
		this.userScope = userScope;
		this.conventionService = conventionService;
		this.messageSource = messageSource;
	}

	@Override
	public String getDashboard(String key) {
		this.authorizationService.authorizeForce(Permission.GetDashboard);
		UserSettingsEntity userSetting = this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.Dashboard).keys(key).entityTypes(UserSettingsEntityType.Application).firstAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		if (userSetting == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{key, UserSettingsEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		return userSetting.getValue();
	}
}
