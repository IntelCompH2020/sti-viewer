package gr.cite.intelcomp.stiviewer.model.builder.datatreeconfig;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.types.datatreeconfig.DataTreeConfigEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.datatreeconfig.DataTreeConfig;
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
public class BrowseDataTreeConfigBuilder extends BaseBuilder<DataTreeConfig, DataTreeConfigEntity> {

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
	public List<DataTreeConfig> build(FieldSet fields, List<DataTreeConfigEntity> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();

		List<DataTreeConfig> dataTreeConfigs = new ArrayList<>();

		FieldSet levelConfigsFields = fields.extractPrefixed(this.asPrefix(DataTreeConfig._levelConfigs));

		for (DataTreeConfigEntity d : data) {
			DataTreeConfig m = new DataTreeConfig();

			if (fields.hasField(this.asIndexer(DataTreeConfig._id))) m.setId(d.getId());
			if (fields.hasField(this.asIndexer(DataTreeConfig._name))) m.setName(d.getName());
			if (!levelConfigsFields.isEmpty() && d.getLevelConfigs() != null) m.setLevelConfigs(this.builderFactory.builder(BrowseDataTreeLevelConfigBuilder.class).authorize(this.authorize).build(levelConfigsFields, d.getLevelConfigs()));

			dataTreeConfigs.add(m);
		}
		return dataTreeConfigs;
	}
}
