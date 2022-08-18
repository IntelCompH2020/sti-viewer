package gr.cite.intelcomp.stiviewer.service.indicatorgroup;


import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.common.types.accessrequestconfig.AccessRequestConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.IndicatorEntity;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.query.IndicatorQuery;
import gr.cite.intelcomp.stiviewer.service.user.UserServiceImpl;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.validation.ValidationService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequestScope
public class IndicatorGroupServiceImpl implements IndicatorGroupService {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserServiceImpl.class));

	private final AuthorizationService authorizationService;
	private final BuilderFactory builderFactory;
	private final ConventionService conventionService;
	private final ErrorThesaurusProperties errors;
	private final MessageSource messageSource;
	private final UserScope userScope;
	private final TenantScope tenantScope;
	private final JsonHandlingService jsonHandlingService;
	private final ValidationService validation;
	private final QueryFactory queryFactory;
	private final IndicatorGroupsProperties config;

	@Autowired
	public IndicatorGroupServiceImpl(
			AuthorizationService authorizationService,
			BuilderFactory builderFactory,
			ConventionService conventionService,
			ErrorThesaurusProperties errors,
			MessageSource messageSource,
			UserScope userScope,
			TenantScope tenantScope,
			JsonHandlingService jsonHandlingService,
			ValidationService validation,
			QueryFactory queryFactory,
			IndicatorGroupsProperties config
	) {
		this.authorizationService = authorizationService;
		this.builderFactory = builderFactory;
		this.conventionService = conventionService;
		this.errors = errors;
		this.messageSource = messageSource;
		this.userScope = userScope;
		this.tenantScope = tenantScope;
		this.jsonHandlingService = jsonHandlingService;
		this.validation = validation;
		this.queryFactory = queryFactory;
		this.config = config;
	}

	public List<IndicatorGroupEntity> getIndicatorGroups() {
		List<IndicatorGroupEntity> indicatorGroupEntities = new ArrayList<>();
		if (this.config.getGroups() != null && !this.config.getGroups().isEmpty()) {
			for (IndicatorGroupsProperties.IndicatorGroup indicatorGroup : this.config.getGroups()) {
				List<IndicatorEntity> indicators = this.queryFactory.query(IndicatorQuery.class).codes(indicatorGroup.getIndicatorCodes().stream().distinct().collect(Collectors.toList())).isActive(IsActive.ACTIVE).collectAs(new BaseFieldSet().ensure(Indicator._id, Indicator._code, Indicator._config));
				List<String> fieldCodes = null;
				for (IndicatorEntity indicator : indicators) {
					List<String> indicatorFieldCodes = new ArrayList<>();
					if (indicator.getConfig() != null) {
						AccessRequestConfigEntity accessRequestConfigEntity = this.jsonHandlingService.fromJsonSafe(AccessRequestConfigEntity.class, indicator.getConfig());
						if (accessRequestConfigEntity != null && accessRequestConfigEntity.getFilterColumns() != null) {
							indicatorFieldCodes.addAll(accessRequestConfigEntity.getFilterColumns().stream().map(x -> x.getCode()).filter(x -> x != null && !x.isBlank()).distinct().collect(Collectors.toList()));
						}
					}
					if (fieldCodes == null) {
						fieldCodes = new ArrayList<>();
						fieldCodes.addAll(indicatorFieldCodes);
					} else {
						fieldCodes = indicatorFieldCodes.stream().filter(fieldCodes::contains).collect(Collectors.toList());
					}
				}
				IndicatorGroupEntity indicatorGroupEntity = new IndicatorGroupEntity();
				indicatorGroupEntity.setName(indicatorGroup.getName());
				indicatorGroupEntity.setDashboardKey(indicatorGroup.getDashboardKey());
				indicatorGroupEntity.setId(indicatorGroup.getGroupId());
				indicatorGroupEntity.setIndicatorIds(indicators.stream().map(x -> x.getId()).collect(Collectors.toList()));
				indicatorGroupEntity.setFilterColumns(fieldCodes);
				indicatorGroupEntities.add(indicatorGroupEntity);
			}
		} else {
			List<IndicatorEntity> indicators = this.queryFactory.query(IndicatorQuery.class).isActive(IsActive.ACTIVE).collectAs(new BaseFieldSet().ensure(Indicator._id, Indicator._code, Indicator._code));
			for (IndicatorEntity indicator : indicators) {
				List<String> indicatorFieldCodes = new ArrayList<>();
				if (indicator.getConfig() != null) {
					AccessRequestConfigEntity accessRequestConfigEntity = this.jsonHandlingService.fromJsonSafe(AccessRequestConfigEntity.class, indicator.getConfig());
					if (accessRequestConfigEntity != null && accessRequestConfigEntity.getFilterColumns() != null) {
						indicatorFieldCodes.addAll(accessRequestConfigEntity.getFilterColumns().stream().map(x -> x.getCode()).filter(x -> x != null && !x.isBlank()).distinct().collect(Collectors.toList()));
					}
				}
				if (!indicatorFieldCodes.isEmpty()) {
					IndicatorGroupEntity indicatorGroupEntity = new IndicatorGroupEntity();
					indicatorGroupEntity.setName(indicator.getName());
					indicatorGroupEntity.setIndicatorIds(List.of(indicator.getId()));
					indicatorGroupEntity.setFilterColumns(indicatorFieldCodes);
					indicatorGroupEntities.add(indicatorGroupEntity);
				}
			}
		}
		return indicatorGroupEntities;
	}
}
