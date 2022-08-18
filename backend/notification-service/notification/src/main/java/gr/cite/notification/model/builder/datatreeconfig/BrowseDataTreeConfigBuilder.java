package gr.cite.notification.model.builder.datatreeconfig;

import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.common.JsonHandlingService;
import gr.cite.notification.common.types.datatreeconfig.BrowseDataTreeConfig;
import gr.cite.notification.convention.ConventionService;
import gr.cite.notification.model.builder.BaseBuilder;
import gr.cite.notification.model.datatreeconfig.BrowseDataTreeConfigModel;
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
public class BrowseDataTreeConfigBuilder extends BaseBuilder<BrowseDataTreeConfigModel, BrowseDataTreeConfig> {

	private final QueryFactory queryFactory;
	private final BuilderFactory builderFactory;
	private final JsonHandlingService jsonHandlingService;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public BrowseDataTreeConfigBuilder(QueryFactory queryFactory,
	                                   BuilderFactory builderFactory,
	                                   JsonHandlingService jsonHandlingService,
	                                   ConventionService conventionService) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(BrowseDataTreeConfigBuilder.class)));
		this.queryFactory = queryFactory;
		this.builderFactory = builderFactory;
		this.jsonHandlingService = jsonHandlingService;
	}

	public BrowseDataTreeConfigBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<BrowseDataTreeConfigModel> build(FieldSet fields, List<BrowseDataTreeConfig> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();

		List<BrowseDataTreeConfigModel> browseDataTreeConfigModels = new ArrayList<>();

		FieldSet levelConfigsFields = fields.extractPrefixed(this.asPrefix(BrowseDataTreeConfigModel._levelConfigs));

		for (BrowseDataTreeConfig d : data) {
			BrowseDataTreeConfigModel m = new BrowseDataTreeConfigModel();

			if (fields.hasField(this.asIndexer(BrowseDataTreeConfigModel._id))) m.setId(d.getId());
			if (fields.hasField(this.asIndexer(BrowseDataTreeConfigModel._name))) m.setName(d.getName());
			if (!levelConfigsFields.isEmpty() && d.getLevelConfigs() != null) m.setLevelConfigs(this.builderFactory.builder(BrowseDataTreeLevelConfigBuilder.class).authorize(this.authorize).build(levelConfigsFields, d.getLevelConfigs()));

			browseDataTreeConfigModels.add(m);
		}
		return browseDataTreeConfigModels;
	}
}
