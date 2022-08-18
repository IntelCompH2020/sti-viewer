package gr.cite.intelcomp.stiviewer.model.builder.datatreeconfig;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.types.datatreeconfig.DataTreeLevelEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.datatreeconfig.DataTreeLevel;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BrowseDataTreeLevelBuilder extends BaseBuilder<DataTreeLevel, DataTreeLevelEntity> {

	private final QueryFactory queryFactory;
	private final BuilderFactory builderFactory;
	private final JsonHandlingService jsonHandlingService;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public BrowseDataTreeLevelBuilder(QueryFactory queryFactory,
	                                  BuilderFactory builderFactory,
	                                  JsonHandlingService jsonHandlingService,
	                                  ConventionService conventionService) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(BrowseDataTreeLevelBuilder.class)));
		this.queryFactory = queryFactory;
		this.builderFactory = builderFactory;
		this.jsonHandlingService = jsonHandlingService;
	}

	public BrowseDataTreeLevelBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<DataTreeLevel> build(FieldSet fields, List<DataTreeLevelEntity> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();

		List<DataTreeLevel> models = new ArrayList<>();

		FieldSet filedFields = fields.extractPrefixed(this.asPrefix(DataTreeLevel._field));
		FieldSet itemsFields = fields.extractPrefixed(this.asPrefix(DataTreeLevel._items));

		for (DataTreeLevelEntity d : data) {
			DataTreeLevel m = new DataTreeLevel();

			if (fields.hasField(this.asIndexer(DataTreeLevel._order))) m.setOrder(d.getOrder());
			if (fields.hasField(this.asIndexer(DataTreeLevel._supportSubLevel))) m.setSupportSubLevel(d.getSupportSubLevel());
			if (fields.hasField(this.asIndexer(DataTreeLevel._supportedDashboards))) m.setSupportedDashboards(d.getSupportedDashboards());
			if (!filedFields.isEmpty()) m.setField(this.builderFactory.builder(BrowseDataFieldBuilder.class).authorize(this.authorize).build(filedFields, d.getField()));
			if (!itemsFields.isEmpty() && d.getItems() != null) m.setItems(this.builderFactory.builder(BrowseDataTreeLevelItemBuilder.class).authorize(this.authorize).build(itemsFields, d.getItems()));

			models.add(m);
		}
		return models;
	}
}
