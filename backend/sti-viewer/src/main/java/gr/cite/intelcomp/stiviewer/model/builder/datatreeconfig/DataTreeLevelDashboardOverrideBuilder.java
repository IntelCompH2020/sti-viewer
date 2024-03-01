package gr.cite.intelcomp.stiviewer.model.builder.datatreeconfig;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.datatreeconfig.DataTreeLevelDashboardOverrideEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.datatreeconfig.DataTreeLevelDashboardOverride;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
//Like in C# make it transient
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataTreeLevelDashboardOverrideBuilder extends BaseBuilder<DataTreeLevelDashboardOverride, DataTreeLevelDashboardOverrideEntity> {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(DataTreeLevelDashboardOverrideBuilder.class));

	private final BuilderFactory builderFactory;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	@Autowired
	public DataTreeLevelDashboardOverrideBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
		super(conventionService, logger);
		this.builderFactory = builderFactory;
	}

	public DataTreeLevelDashboardOverrideBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<DataTreeLevelDashboardOverride> build(FieldSet fields, List<DataTreeLevelDashboardOverrideEntity> datas) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} BrowseDataFields", Optional.ofNullable(datas).map(e -> e.size()).orElse(0), Optional.ofNullable(fields).map(e -> e.getFields()).map(e -> e.size()).orElse(0));
		this.logger.trace(new DataLogEntry("requested BrowseDataFields", fields));
		if (fields == null || fields.isEmpty()) return new ArrayList<>();

		FieldSet requirementsFields = fields.extractPrefixed(this.asPrefix(DataTreeLevelDashboardOverride._requirements));

		List<DataTreeLevelDashboardOverride> models = new LinkedList<>();
		for (DataTreeLevelDashboardOverrideEntity d : datas) {
			DataTreeLevelDashboardOverride m = new DataTreeLevelDashboardOverride();
			if (fields.hasField(this.asIndexer(DataTreeLevelDashboardOverride._supportedDashboards))) m.setSupportedDashboards(d.getSupportedDashboards());
			if (fields.hasField(this.asIndexer(DataTreeLevelDashboardOverride._supportSubLevel))) m.setSupportSubLevel(d.getSupportSubLevel());
			if (!requirementsFields.isEmpty() && d.getRequirements() != null) m.setRequirements(this.builderFactory.builder(DataTreeLevelDashboardOverrideFieldRequirementBuilder.class).authorize(this.authorize).build(requirementsFields, d.getRequirements()));
			models.add(m);
		}

		return models;
	}
}
