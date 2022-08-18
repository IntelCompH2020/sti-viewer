package gr.cite.notification.model.builder.datatreeconfig;

import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.common.JsonHandlingService;
import gr.cite.notification.common.types.datatreeconfig.BrowseDataTreeLevelConfig;
import gr.cite.notification.convention.ConventionService;
import gr.cite.notification.model.builder.BaseBuilder;
import gr.cite.notification.model.datatreeconfig.BrowseDataTreeLevelConfigModel;
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
public class BrowseDataTreeLevelConfigBuilder extends BaseBuilder<BrowseDataTreeLevelConfigModel, BrowseDataTreeLevelConfig> {

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
	public List<BrowseDataTreeLevelConfigModel> build(FieldSet fields, List<BrowseDataTreeLevelConfig> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();

		List<BrowseDataTreeLevelConfigModel> IndicatorReportLevelConfigs = new ArrayList<>();

		FieldSet filedFields = fields.extractPrefixed(this.asPrefix(BrowseDataTreeLevelConfigModel._field));

		for (BrowseDataTreeLevelConfig d : data) {
			BrowseDataTreeLevelConfigModel m = new BrowseDataTreeLevelConfigModel();

			if (fields.hasField(this.asIndexer(BrowseDataTreeLevelConfigModel._order))) m.setOrder(d.getOrder());
			if (fields.hasField(this.asIndexer(BrowseDataTreeLevelConfigModel._supportSubLevel))) m.setSupportSubLevel(d.getSupportSubLevel());
			if (fields.hasField(this.asIndexer(BrowseDataTreeLevelConfigModel._supportedDashboards))) m.setSupportedDashboards(d.getSupportedDashboards());
			if (!filedFields.isEmpty()) m.setField(this.builderFactory.builder(BrowseDataFieldBuilder.class).authorize(this.authorize).build(filedFields, d.getField()));

			IndicatorReportLevelConfigs.add(m);
		}
		return IndicatorReportLevelConfigs;
	}
}
