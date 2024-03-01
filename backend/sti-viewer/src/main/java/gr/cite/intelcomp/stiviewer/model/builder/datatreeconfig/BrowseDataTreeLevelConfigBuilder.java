package gr.cite.intelcomp.stiviewer.model.builder.datatreeconfig;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.types.datatreeconfig.DataTreeLevelConfigEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.datatreeconfig.DataTreeLevelConfig;
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
public class BrowseDataTreeLevelConfigBuilder extends BaseBuilder<DataTreeLevelConfig, DataTreeLevelConfigEntity> {

	private final QueryFactory queryFactory;
	private final BuilderFactory builderFactory;
	private final JsonHandlingService jsonHandlingService;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public BrowseDataTreeLevelConfigBuilder(QueryFactory queryFactory,
	                                        BuilderFactory builderFactory,
	                                        JsonHandlingService jsonHandlingService,
	                                        ConventionService conventionService) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(BrowseDataTreeLevelConfigBuilder.class)));
		this.queryFactory = queryFactory;
		this.builderFactory = builderFactory;
		this.jsonHandlingService = jsonHandlingService;
	}

	public BrowseDataTreeLevelConfigBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<DataTreeLevelConfig> build(FieldSet fields, List<DataTreeLevelConfigEntity> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();

		List<DataTreeLevelConfig> IndicatorReportLevelConfigs = new ArrayList<>();

		FieldSet filedFields = fields.extractPrefixed(this.asPrefix(DataTreeLevelConfig._field));
		FieldSet dashboardOverrideFields = fields.extractPrefixed(this.asPrefix(DataTreeLevelConfig._dashboardOverrides));

		for (DataTreeLevelConfigEntity d : data) {
			DataTreeLevelConfig m = new DataTreeLevelConfig();

			if (fields.hasField(this.asIndexer(DataTreeLevelConfig._order))) m.setOrder(d.getOrder());
			if (fields.hasField(this.asIndexer(DataTreeLevelConfig._supportSubLevel))) m.setSupportSubLevel(d.getSupportSubLevel());
			if (fields.hasField(this.asIndexer(DataTreeLevelConfig._defaultDashboards))) m.setDefaultDashboards(d.getDefaultDashboards());
			if (!filedFields.isEmpty()) m.setField(this.builderFactory.builder(BrowseDataFieldBuilder.class).authorize(this.authorize).build(filedFields, d.getField()));
			if (!dashboardOverrideFields.isEmpty() && d.getDashboardOverrides() != null) m.setDashboardOverrides(this.builderFactory.builder(DataTreeLevelDashboardOverrideBuilder.class).authorize(this.authorize).build(dashboardOverrideFields, d.getDashboardOverrides()));

			IndicatorReportLevelConfigs.add(m);
		}
		return IndicatorReportLevelConfigs;
	}
}
