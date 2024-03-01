package gr.cite.intelcomp.stiviewer.service.indicatorgroup;


import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.common.types.datatreeconfig.DataTreeLevelConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicator.FilterColumnConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicator.IndicatorConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.FilterColumnEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.IndicatorEntity;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.query.IndicatorQuery;
import gr.cite.intelcomp.stiviewer.service.user.UserServiceImpl;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.validation.ValidationService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.HashSet;
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

	@Override
	public List<IndicatorGroupEntity> getIndicatorGroups() {
		List<IndicatorGroupEntity> indicatorGroupEntities = new ArrayList<>();
		if (this.config.getGroups() != null && !this.config.getGroups().isEmpty()) {
			for (IndicatorGroup indicatorGroup : this.config.getGroups()) {
				indicatorGroupEntities.add(this.toEntity(indicatorGroup));
			}
		} else {
			List<IndicatorEntity> indicators = this.queryFactory.query(IndicatorQuery.class).isActive(IsActive.ACTIVE).collectAs(new BaseFieldSet().ensure(Indicator._id, Indicator._code, Indicator._code));
			for (IndicatorEntity indicator : indicators) {
				List<FilterColumnEntity> indicatorFieldCodes = this.applyFilterColumnEntity(indicator, new ArrayList<>());
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

	@Override
	public IndicatorGroupEntity getIndicatorGroupByCode(String code) {
		if (this.config.getGroups() == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{code, IndicatorGroup .class.getSimpleName()}, LocaleContextHolder.getLocale()));
		IndicatorGroup indicatorGroup = this.config.getGroups().stream().filter(x-> x.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
		if (indicatorGroup == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{code, IndicatorGroup .class.getSimpleName()}, LocaleContextHolder.getLocale()));
		return this.toEntity(indicatorGroup);
	}
	
	private IndicatorGroupEntity toEntity(IndicatorGroup indicatorGroup){
		List<IndicatorEntity> indicators = this.queryFactory.query(IndicatorQuery.class).codes(indicatorGroup.getIndicatorCodes().stream().distinct().collect(Collectors.toList())).isActive(IsActive.ACTIVE).collectAs(new BaseFieldSet().ensure(Indicator._id, Indicator._code, Indicator._config));
		List<FilterColumnEntity> fieldCodes = new ArrayList<>();
		for (IndicatorEntity indicator : indicators) {
			fieldCodes = this.applyFilterColumnEntity(indicator, fieldCodes);
		}
		IndicatorGroupEntity indicatorGroupEntity = new IndicatorGroupEntity();
		indicatorGroupEntity.setName(indicatorGroup.getName());
		indicatorGroupEntity.setId(indicatorGroup.getGroupId());
		indicatorGroupEntity.setCode(indicatorGroup.getCode());
		indicatorGroupEntity.setIndicatorIds(indicators.stream().map(x -> x.getId()).collect(Collectors.toList()));
		indicatorGroupEntity.setFilterColumns(fieldCodes);
		return indicatorGroupEntity;
	}
	
	private List<FilterColumnEntity> applyFilterColumnEntity(IndicatorEntity indicator, List<FilterColumnEntity> indicatorFieldCodes ){
		if (indicator.getConfig() != null) {
			IndicatorConfigEntity accessRequestConfigEntity = this.jsonHandlingService.fromJsonSafe(IndicatorConfigEntity.class, indicator.getConfig());
			if (accessRequestConfigEntity != null && accessRequestConfigEntity.getAccessRequestConfig() != null && accessRequestConfigEntity.getAccessRequestConfig().getFilterColumns() != null) {
				for (FilterColumnConfigEntity filterColumnConfig: accessRequestConfigEntity.getAccessRequestConfig().getFilterColumns()) {
					if (this.conventionService.isNullOrEmpty(filterColumnConfig.getCode())) continue;
					FilterColumnEntity filterColumnEntity = indicatorFieldCodes.stream().filter(x-> x.getCode().equals(filterColumnConfig.getCode())).findFirst().orElse(null);
					if (filterColumnEntity == null){
						filterColumnEntity = new FilterColumnEntity();
						filterColumnEntity.setCode(filterColumnConfig.getCode());
						filterColumnEntity.setDependsOnCode(filterColumnConfig.getDependsOnCode());
						indicatorFieldCodes.add(filterColumnEntity);
					}
				}
			}
		}
		return indicatorFieldCodes;
	}
}
