package gr.cite.intelcomp.stiviewer.model.builder.indicatorgroup;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupAccessConfigViewEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.IndicatorGroup;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.builder.IndicatorGroupBuilder;
import gr.cite.intelcomp.stiviewer.model.indicatorgroup.IndicatorGroupAccessConfigView;
import gr.cite.intelcomp.stiviewer.service.indicatorgroup.IndicatorGroupService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IndicatorGroupAccessConfigViewBuilder extends BaseBuilder<IndicatorGroupAccessConfigView, IndicatorGroupAccessConfigViewEntity> {

	private final QueryFactory queryFactory;
	private final BuilderFactory builderFactory;
	private final JsonHandlingService jsonHandlingService;
	private final IndicatorGroupService indicatorGroupService;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	@Autowired
	public IndicatorGroupAccessConfigViewBuilder(
			ConventionService conventionService,
			QueryFactory queryFactory,
			BuilderFactory builderFactory,
			JsonHandlingService jsonHandlingService, 
			IndicatorGroupService indicatorGroupService) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(IndicatorGroupAccessConfigViewBuilder.class)));
		this.queryFactory = queryFactory;
		this.builderFactory = builderFactory;
		this.jsonHandlingService = jsonHandlingService;
		this.indicatorGroupService = indicatorGroupService;
	}

	public IndicatorGroupAccessConfigViewBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<IndicatorGroupAccessConfigView> build(FieldSet fields, List<IndicatorGroupAccessConfigViewEntity> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();

		FieldSet indicatorGroupFields = fields.extractPrefixed(this.asPrefix(IndicatorGroupAccessConfigView._group));
		Map<UUID, IndicatorGroup> indicatorGroupItemsMap = this.collectIndicatorGroups(indicatorGroupFields, data.stream().map(x -> x.getId()).distinct().collect(Collectors.toList()));
		
		FieldSet filterColumnsFields = fields.extractPrefixed(this.asPrefix(IndicatorGroupAccessConfigView._filterColumns));

		List<IndicatorGroupAccessConfigView> models = new ArrayList<>();
		for (IndicatorGroupAccessConfigViewEntity d : data) {
			IndicatorGroupAccessConfigView m = new IndicatorGroupAccessConfigView();
			if (!filterColumnsFields.isEmpty() && d.getFilterColumns() != null) m.setFilterColumns(this.builderFactory.builder(IndicatorGroupAccessColumnConfigViewBuilder.class).authorize(this.authorize).build(filterColumnsFields, d.getFilterColumns()));
			if (!indicatorGroupFields.isEmpty() && indicatorGroupItemsMap != null && indicatorGroupItemsMap.containsKey(d.getId()))  m.setGroup(indicatorGroupItemsMap.get(d.getId()));
			models.add(m);
		}

		this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
		return models;
	}

	private Map<UUID, IndicatorGroup> collectIndicatorGroups(FieldSet fields, List<UUID> ids) throws MyApplicationException {
		if (fields.isEmpty() || ids.isEmpty()) return null;
		this.logger.debug("checking related - {}", IndicatorGroup.class.getSimpleName());

		Map<UUID, IndicatorGroup> itemMap;
		if (!fields.hasOtherField(this.asIndexer(IndicatorGroup._id))) {
			itemMap = this.asEmpty(
					ids,
					x -> {
						IndicatorGroup item = new IndicatorGroup();
						item.setId(x);
						return item;
					},
					IndicatorGroup::getId);
		} else {
			FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(IndicatorGroup._id);
			List<IndicatorGroupEntity> items = indicatorGroupService.getIndicatorGroups().stream().filter(x-> ids.contains(x.getId())).collect(Collectors.toList());
			itemMap = this.builderFactory.builder(IndicatorGroupBuilder.class).authorize(this.authorize).asForeignKey(items, clone, IndicatorGroup::getId);
		}
		if (!fields.hasField(Indicator._id)) {
			itemMap.values().stream().filter(Objects::nonNull).peek(x -> x.setId(null)).collect(Collectors.toList());
		}

		return itemMap;
	}

}
