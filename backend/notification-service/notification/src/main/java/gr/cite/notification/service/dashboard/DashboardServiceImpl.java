package gr.cite.notification.service.dashboard;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.notification.authorization.Permission;
import gr.cite.notification.common.enums.UserSettingsEntityType;
import gr.cite.notification.common.enums.UserSettingsType;
import gr.cite.notification.common.scope.user.UserScope;
import gr.cite.notification.convention.ConventionService;
import gr.cite.notification.data.TenantScopedEntityManager;
import gr.cite.notification.data.UserSettingsEntity;
import gr.cite.notification.model.UserSettings;
import gr.cite.notification.query.UserSettingsQuery;
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

	private final TenantScopedEntityManager entityManager;
	private final QueryFactory queryFactory;
	private final AuthorizationService authorizationService;
	private final UserScope userScope;
	private final ConventionService conventionService;
	private final MessageSource messageSource;

	public DashboardServiceImpl(
			TenantScopedEntityManager entityManager,
			QueryFactory queryFactory,
			AuthorizationService authorizationService,
			UserScope userScope,
			ConventionService conventionService,
			MessageSource messageSource
	) {
		this.entityManager = entityManager;
		this.queryFactory = queryFactory;
		this.authorizationService = authorizationService;
		this.userScope = userScope;
		this.conventionService = conventionService;
		this.messageSource = messageSource;
	}

	@Override
	public String getDashboard(String key) {
		this.authorizationService.authorizeForce(Permission.GetDashboard);
		UserSettingsEntity userSetting = this.queryFactory
				.query(UserSettingsQuery.class)
				.types(UserSettingsType.Dashboard).keys(key)
				.entityTypes(UserSettingsEntityType.Application)
				.firstAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		if (userSetting == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{key, UserSettingsEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		return userSetting.getValue();
	}
}
