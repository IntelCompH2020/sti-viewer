package gr.cite.intelcomp.stiviewer.model.builder.datatreeconfig;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.types.datatreeconfig.DataTreeLevelItemEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.datatreeconfig.DataTreeLevelItem;
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
public class BrowseDataTreeLevelItemBuilder extends BaseBuilder<DataTreeLevelItem, DataTreeLevelItemEntity> {

	private final QueryFactory queryFactory;
	private final BuilderFactory builderFactory;
	private final JsonHandlingService jsonHandlingService;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public BrowseDataTreeLevelItemBuilder(QueryFactory queryFactory,
	                                      BuilderFactory builderFactory,
	                                      JsonHandlingService jsonHandlingService,
	                                      ConventionService conventionService) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(BrowseDataTreeLevelItemBuilder.class)));
		this.queryFactory = queryFactory;
		this.builderFactory = builderFactory;
		this.jsonHandlingService = jsonHandlingService;
	}

	public BrowseDataTreeLevelItemBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<DataTreeLevelItem> build(FieldSet fields, List<DataTreeLevelItemEntity> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();

		List<DataTreeLevelItem> IndicatorReportLevelConfigs = new ArrayList<>();

		for (DataTreeLevelItemEntity d : data) {
			DataTreeLevelItem m = new DataTreeLevelItem();

			if (fields.hasField(this.asIndexer(DataTreeLevelItem._value))) m.setValue(d.getValue());
			if (fields.hasField(this.asIndexer(DataTreeLevelItem._supportedDashboards))) m.setSupportedDashboards(d.getSupportedDashboards());
			if (fields.hasField(this.asIndexer(DataTreeLevelItem._supportSubLevel))) m.setSupportSubLevel(d.getSupportSubLevel());

			IndicatorReportLevelConfigs.add(m);
		}
		return IndicatorReportLevelConfigs;
	}
}
