package gr.cite.intelcomp.stiviewer.service.dashboard;


import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.UserSettingsEntityType;
import gr.cite.intelcomp.stiviewer.common.enums.UserSettingsType;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.common.types.externaltoken.DefinitionEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.ExternalTokenEntity;
import gr.cite.intelcomp.stiviewer.data.UserSettingsEntity;
import gr.cite.intelcomp.stiviewer.model.UserSettings;
import gr.cite.intelcomp.stiviewer.model.shared.DashboardLookup;
import gr.cite.intelcomp.stiviewer.query.UserSettingsQuery;
import gr.cite.intelcomp.stiviewer.service.externaltoken.ExternalTokenService;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.fieldset.BaseFieldSet;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import javax.naming.OperationNotSupportedException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequestScope
public class DashboardServiceImpl implements DashboardService {

	private final QueryFactory queryFactory;
	private final AuthorizationService authorizationService;
	private final UserScope userScope;
	private final MessageSource messageSource;
	private final TenantScope tenantScope;
	private final ExternalTokenService externalTokenService;
	private final ConventionService conventionService;
	private final JsonHandlingService jsonHandlingService;
	public DashboardServiceImpl(
			QueryFactory queryFactory,
			AuthorizationService authorizationService,
			UserScope userScope,
			MessageSource messageSource,
			TenantScope tenantScope,
			ExternalTokenService externalTokenService,
			ConventionService conventionService, 
			JsonHandlingService jsonHandlingService) {
		this.queryFactory = queryFactory;
		this.authorizationService = authorizationService;
		this.userScope = userScope;
		this.messageSource = messageSource;
		this.tenantScope = tenantScope;
		this.externalTokenService = externalTokenService;
		this.conventionService = conventionService;
		this.jsonHandlingService = jsonHandlingService;
	}

	@Override
	public String getPublicDashboard(String key) throws InvalidApplicationException {
		this.authorizationService.authorizeForce(Permission.GetDashboard);
		return this.getDashboard(key, this.tenantScope.isSet() ? this.tenantScope.getTenant() : null, this.userScope.getUserId());
	}

	@Override
	public String getPublicDashboard(DashboardLookup lookup) throws NoSuchAlgorithmException, OperationNotSupportedException {
		ExternalTokenEntity externalTokenEntity = this.externalTokenService.getValidForce(lookup.getToken());
		DefinitionEntity definitionEntity = this.jsonHandlingService.fromJsonSafe(DefinitionEntity.class, externalTokenEntity.getDefinition());
		if (definitionEntity == null)  throw new MyForbiddenException("Access is denied");
		if (this.conventionService.isListNullOrEmpty(definitionEntity.getMappers()))  throw new MyForbiddenException("Access is denied");
		boolean hasDashboardId = definitionEntity.getMappers().stream().anyMatch(x-> x.containsExternalId(lookup.getDashboardId()));
		if (!hasDashboardId)  throw new MyForbiddenException("Access is denied");

		return this.getDashboard(lookup.getDashboardId(), externalTokenEntity.getTenantId(), externalTokenEntity.getOwnerId());
	}

	private String getDashboard(String key, UUID tenantId, UUID userId) {
		UserSettingsEntity userSetting = this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.Dashboard).keys(key).entityTypes(UserSettingsEntityType.User).entityIds(userId).firstAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		if (userSetting == null && this.tenantScope.isSet()) userSetting = this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.Dashboard).keys(key).entityTypes(UserSettingsEntityType.Tenant).entityIds(tenantId).firstAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		if (userSetting == null) userSetting = this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.Dashboard).keys(key).entityTypes(UserSettingsEntityType.Application).firstAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		if (userSetting == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{key, UserSettingsEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		return userSetting.getValue();
	}
}
