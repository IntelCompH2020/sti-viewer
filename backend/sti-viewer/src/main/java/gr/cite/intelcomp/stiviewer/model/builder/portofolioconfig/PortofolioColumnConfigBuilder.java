package gr.cite.intelcomp.stiviewer.model.builder.portofolioconfig;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.types.portofolioconfig.PortofolioColumnConfigEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.portofolioconfig.PortofolioColumnConfig;
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
public class PortofolioColumnConfigBuilder extends BaseBuilder<PortofolioColumnConfig, PortofolioColumnConfigEntity> {

	private final QueryFactory queryFactory;
	private final BuilderFactory builderFactory;
	private final JsonHandlingService jsonHandlingService;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public PortofolioColumnConfigBuilder(QueryFactory queryFactory,
	                                     BuilderFactory builderFactory,
	                                     JsonHandlingService jsonHandlingService,
	                                     ConventionService conventionService) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(PortofolioColumnConfigBuilder.class)));
		this.queryFactory = queryFactory;
		this.builderFactory = builderFactory;
		this.jsonHandlingService = jsonHandlingService;
	}

	public PortofolioColumnConfigBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<PortofolioColumnConfig> build(FieldSet fields, List<PortofolioColumnConfigEntity> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();

		List<PortofolioColumnConfig> IndicatorReportLevelConfigs = new ArrayList<>();

		FieldSet filedFields = fields.extractPrefixed(this.asPrefix(PortofolioColumnConfig._field));
		FieldSet dashboardOverrideFields = fields.extractPrefixed(this.asPrefix(PortofolioColumnConfig._dashboardOverrides));

		for (PortofolioColumnConfigEntity d : data) {
			PortofolioColumnConfig m = new PortofolioColumnConfig();

			if (fields.hasField(this.asIndexer(PortofolioColumnConfig._defaultDashboards))) m.setDefaultDashboards(d.getDefaultDashboards());
			if (fields.hasField(this.asIndexer(PortofolioColumnConfig._order))) m.setOrder(d.getOrder());
			if (fields.hasField(this.asIndexer(PortofolioColumnConfig._major))) m.setMajor(d.getMajor());
			if (!filedFields.isEmpty()) m.setField(this.builderFactory.builder(DataFieldBuilder.class).authorize(this.authorize).build(filedFields, d.getField()));
			if (!dashboardOverrideFields.isEmpty() && d.getDashboardOverrides() != null) m.setDashboardOverrides(this.builderFactory.builder(PortofolioColumnDashboardOverrideBuilder.class).authorize(this.authorize).build(dashboardOverrideFields, d.getDashboardOverrides()));

			IndicatorReportLevelConfigs.add(m);
		}
		return IndicatorReportLevelConfigs;
	}
}
