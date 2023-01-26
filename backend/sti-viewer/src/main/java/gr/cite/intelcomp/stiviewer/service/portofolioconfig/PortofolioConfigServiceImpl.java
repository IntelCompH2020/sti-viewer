package gr.cite.intelcomp.stiviewer.service.portofolioconfig;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationContentResolver;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.UserSettingsEntityType;
import gr.cite.intelcomp.stiviewer.common.enums.UserSettingsType;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.common.types.portofolioconfig.PortofolioConfigEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.data.UserSettingsEntity;
import gr.cite.intelcomp.stiviewer.model.MasterItem;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequestScope
public class PortofolioConfigServiceImpl implements PortofolioConfigService {
	private final TenantEntityManager entityManager;
	private final QueryFactory queryFactory;
	private final ElasticsearchRestTemplate elasticsearchTemplate;
	private final AuthorizationService authorizationService;
	private final AuthorizationContentResolver authorizationContentResolver;
	private final UserScope userScope;
	private final BuilderFactory builderFactory;
	private final ConventionService conventionService;
	private final MessageSource messageSource;
	private final IndicatorConfigService indicatorConfigService;
	private final JsonHandlingService jsonHandlingService;

	public PortofolioConfigServiceImpl(
			TenantEntityManager entityManager,
			QueryFactory queryFactory,
			ElasticsearchRestTemplate elasticsearchTemplate,
			AuthorizationService authorizationService,
			AuthorizationContentResolver authorizationContentResolver,
			UserScope userScope,
			BuilderFactory builderFactory,
			ConventionService conventionService,
			MessageSource messageSource,
			IndicatorConfigService indicatorConfigService,
			JsonHandlingService jsonHandlingService
	) {
		this.entityManager = entityManager;
		this.queryFactory = queryFactory;
		this.elasticsearchTemplate = elasticsearchTemplate;
		this.authorizationService = authorizationService;
		this.authorizationContentResolver = authorizationContentResolver;
		this.userScope = userScope;
		this.builderFactory = builderFactory;
		this.conventionService = conventionService;
		this.messageSource = messageSource;
		this.indicatorConfigService = indicatorConfigService;
		this.jsonHandlingService = jsonHandlingService;
	}

	@Override
	public List<PortofolioConfig> getMyConfigs(FieldSet fields) {
		List<PortofolioConfigEntity> browsePortofolioConfigEntities = this.getMyConfigs();
		return this.builderFactory.builder(PortofolioConfigBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, PortofolioConfig._code), browsePortofolioConfigEntities);
	}

	@Override
	public PortofolioConfig getMyConfigByKey(String key, FieldSet fields) {
		if (this.conventionService.isNullOrEmpty(key)) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{key, UserSettingsEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		UserSettingsEntity userSettingsEntity = this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.Portofolio).keys(key).entityTypes(UserSettingsEntityType.Application).firstAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
		if (userSettingsEntity == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{key, UserSettingsEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		PortofolioConfigEntity portofolioConfigEntity = this.jsonHandlingService.fromJsonSafe(PortofolioConfigEntity.class, userSettingsEntity.getValue());
		if (portofolioConfigEntity == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{key, UserSettingsEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		
		return this.builderFactory.builder(PortofolioConfigBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(BaseFieldSet.build(fields, PortofolioConfig._code), portofolioConfigEntity);
	}

	private List<PortofolioConfigEntity> getMyConfigs() {
		List<UserSettingsEntity> userSettingsEntities = this.queryFactory.query(UserSettingsQuery.class).types(UserSettingsType.Portofolio).entityTypes(UserSettingsEntityType.Application).collectAs(new BaseFieldSet().ensure(UserSettings._key).ensure(UserSettings._value));
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
