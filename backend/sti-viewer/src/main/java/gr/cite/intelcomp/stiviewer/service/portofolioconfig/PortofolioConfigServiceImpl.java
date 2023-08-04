package gr.cite.intelcomp.stiviewer.service.portofolioconfig;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationContentResolver;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.UserSettingsEntityType;
import gr.cite.intelcomp.stiviewer.common.enums.UserSettingsType;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.common.types.portofolioconfig.PortofolioConfigEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.data.UserSettingsEntity;
import gr.cite.intelcomp.stiviewer.model.UserSettings;
import gr.cite.intelcomp.stiviewer.model.builder.portofolioconfig.PortofolioConfigBuilder;
import gr.cite.intelcomp.stiviewer.model.portofolioconfig.PortofolioConfig;
import gr.cite.intelcomp.stiviewer.query.UserSettingsQuery;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorConfigService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequestScope
public class PortofolioConfigServiceImpl implements PortofolioConfigService {
	private final QueryFactory queryFactory;
	private final UserScope userScope;
	private final BuilderFactory builderFactory;
	private final ConventionService conventionService;
	private final MessageSource messageSource;
	private final JsonHandlingService jsonHandlingService;
	private final TenantScope tenantScope;

	public PortofolioConfigServiceImpl(
			QueryFactory queryFactory,
			UserScope userScope,
			BuilderFactory builderFactory,
			ConventionService conventionService,
			MessageSource messageSource,
			JsonHandlingService jsonHandlingService,
			TenantScope tenantScope) {
		this.tenantScope = tenantScope;
		this.queryFactory = queryFactory;
		this.userScope = userScope;
		this.builderFactory = builderFactory;
		this.conventionService = conventionService;
		this.messageSource = messageSource;
		this.jsonHandlingService = jsonHandlingService;
	}

	@Override
	public List<PortofolioConfig> getMyConfigs(FieldSet fields) throws InvalidApplicationException {
		List<PortofolioConfigEntity> browsePortofolioConfigEntities = this.getMyConfigs();
		return this.builderFactory.builder(PortofolioConfigBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, PortofolioConfig._code), browsePortofolioConfigEntities);
	}

	@Override
	public PortofolioConfig getMyConfigByKey(String key, FieldSet fields) throws InvalidApplicationException {
		if (this.conventionService.isNullOrEmpty(key)) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{key, UserSettingsEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		
		UserSettingsEntity userSettingsEntity = this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.Portofolio).entityTypes(UserSettingsEntityType.User).entityIds(this.userScope.getUserId()).keys(key).firstAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		if (userSettingsEntity == null&& this.tenantScope.isSet()) userSettingsEntity = this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.Portofolio).entityTypes(UserSettingsEntityType.Tenant).entityIds(this.tenantScope.getTenant()).keys(key).firstAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		if (userSettingsEntity == null) userSettingsEntity =this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.Portofolio).entityTypes(UserSettingsEntityType.Application).keys(key).firstAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		if (userSettingsEntity == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{key, UserSettingsEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		
		PortofolioConfigEntity portofolioConfigEntity = this.jsonHandlingService.fromJsonSafe(PortofolioConfigEntity.class, userSettingsEntity.getValue());
		if (portofolioConfigEntity == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{key, UserSettingsEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		
		return this.builderFactory.builder(PortofolioConfigBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, PortofolioConfig._code), portofolioConfigEntity);
	}

	private List<PortofolioConfigEntity> getMyConfigs() throws InvalidApplicationException {
		List<UserSettingsEntity> userSettingsEntities = this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.Portofolio).entityTypes(UserSettingsEntityType.User).entityIds(this.userScope.getUserId()).collectAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		if ((userSettingsEntities == null || userSettingsEntities.isEmpty()) && this.tenantScope.isSet()) userSettingsEntities = this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.Portofolio).entityTypes(UserSettingsEntityType.Tenant).entityIds(this.tenantScope.getTenant()).collectAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		if (userSettingsEntities == null || userSettingsEntities.isEmpty()) userSettingsEntities = this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.Portofolio).entityTypes(UserSettingsEntityType.Application).collectAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		
		List<PortofolioConfigEntity> portofolioConfigEntities = new ArrayList<>();
		if (userSettingsEntities != null) {
			for (UserSettingsEntity userSettingsEntity : userSettingsEntities) {
				PortofolioConfigEntity portofolioConfigEntity = this.jsonHandlingService.fromJsonSafe(PortofolioConfigEntity.class, userSettingsEntity.getValue());
				if (portofolioConfigEntity != null) {
					portofolioConfigEntities.add(portofolioConfigEntity);
				}
			}
		}
		return portofolioConfigEntities;
	}
}
