package gr.cite.intelcomp.stiviewer.model.builder.dataaccessrequest;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.dataaccessrequest.DataAccessRequestIndicatorConfigEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.builder.IndicatorBuilder;
import gr.cite.intelcomp.stiviewer.query.IndicatorQuery;
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
public class DataAccessRequestIndicatorConfigBuilder extends BaseBuilder<gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequestIndicatorConfig, DataAccessRequestIndicatorConfigEntity> {

	private final QueryFactory queryFactory;
	private final BuilderFactory builderFactory;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);


	@Autowired
	public DataAccessRequestIndicatorConfigBuilder(
			ConventionService conventionService,
			QueryFactory queryFactory, BuilderFactory builderFactory) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(DataAccessRequestIndicatorConfigBuilder.class)));
		this.queryFactory = queryFactory;
		this.builderFactory = builderFactory;

	}

	public DataAccessRequestIndicatorConfigBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequestIndicatorConfig> build(FieldSet fields, List<DataAccessRequestIndicatorConfigEntity> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();


		List<gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequestIndicatorConfig> models = new ArrayList<>();

		FieldSet indicatorFields = fields.extractPrefixed(this.asPrefix(gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequestIndicatorConfig._indicator));
		FieldSet filterColumnsFields = fields.extractPrefixed(this.asPrefix(gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequestIndicatorConfig._filterColumns));
		Map<UUID, Indicator> indicatorItemsMap = this.collectIndicators(indicatorFields, data);

		for (DataAccessRequestIndicatorConfigEntity d : data) {
			gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequestIndicatorConfig m = new gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequestIndicatorConfig();
			if (!indicatorFields.isEmpty() && indicatorItemsMap != null && indicatorItemsMap.containsKey(d.getId())) m.setIndicator(indicatorItemsMap.get(d.getId()));
			if (!filterColumnsFields.isEmpty() && d.getFilterColumns() != null && !d.getFilterColumns().isEmpty()) m.setFilterColumns(this.builderFactory.builder(DataAccessFilterColumnConfigBuilder.class).authorize(this.authorize).build(filterColumnsFields, d.getFilterColumns()));
			models.add(m);
		}

		this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
		return models;
	}

	private Map<UUID, Indicator> collectIndicators(FieldSet fields, List<DataAccessRequestIndicatorConfigEntity> data) throws MyApplicationException {
		if (fields.isEmpty() || data.isEmpty()) return null;
		this.logger.debug("checking related - {}", Indicator.class.getSimpleName());

		Map<UUID, Indicator> itemMap;
		if (!fields.hasOtherField(this.asIndexer(Indicator._id))) {
			itemMap = this.asEmpty(
					data.stream().map(DataAccessRequestIndicatorConfigEntity::getId).distinct().collect(Collectors.toList()),
					x -> {
						Indicator item = new Indicator();
						item.setId(x);
						return item;
					},
					Indicator::getId);
		} else {
			FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(Indicator._id);
			IndicatorQuery q = this.queryFactory.query(IndicatorQuery.class).authorize(this.authorize).ids(data.stream().map(DataAccessRequestIndicatorConfigEntity::getId).distinct().collect(Collectors.toList()));
			itemMap = this.builderFactory.builder(IndicatorBuilder.class).authorize(this.authorize).asForeignKey(q, clone, Indicator::getId);
		}
		if (!fields.hasField(Indicator._id)) {
			itemMap.values().stream().filter(Objects::nonNull).peek(x -> x.setId(null)).collect(Collectors.toList());
		}

		return itemMap;
	}


}
