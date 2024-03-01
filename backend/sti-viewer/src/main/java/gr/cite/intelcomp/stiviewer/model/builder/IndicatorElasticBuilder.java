package gr.cite.intelcomp.stiviewer.model.builder;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.IndicatorElasticEntity;
import gr.cite.intelcomp.stiviewer.model.IndicatorElastic;
import gr.cite.intelcomp.stiviewer.model.builder.indicatorelastic.MetadataBuilder;
import gr.cite.intelcomp.stiviewer.model.builder.indicatorelastic.SchemaBuilder;
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
public class IndicatorElasticBuilder extends BaseBuilder<IndicatorElastic, IndicatorElasticEntity> {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(IndicatorElasticBuilder.class));
	private final BuilderFactory builderFactory;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	@Autowired
	public IndicatorElasticBuilder(ConventionService conventionService,
	                               BuilderFactory builderFactory) {
		super(conventionService, logger);
		this.builderFactory = builderFactory;
	}

	public IndicatorElasticBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<IndicatorElastic> build(FieldSet fields, List<IndicatorElasticEntity> datas) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(datas).map(e -> e.size()).orElse(0), Optional.ofNullable(fields).map(e -> e.getFields()).map(e -> e.size()).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || fields.isEmpty()) return new ArrayList<>();

		FieldSet metadataFields = fields.extractPrefixed(this.asPrefix(IndicatorElastic._metadata));
		FieldSet schemaFields = fields.extractPrefixed(this.asPrefix(IndicatorElastic._schema));

		List<IndicatorElastic> indicatorElastics = new LinkedList<>();
		for (IndicatorElasticEntity d : datas) {
			IndicatorElastic m = new IndicatorElastic();
			if (fields.hasField(this.asIndexer(IndicatorElastic._id))) m.setId(d.getId());
			if (!metadataFields.isEmpty() && d.getMetadata() != null) m.setMetadata(this.builderFactory.builder(MetadataBuilder.class).authorize(this.authorize).build(metadataFields, d.getMetadata()));
			if (!schemaFields.isEmpty() && d.getSchema() != null) m.setSchema(this.builderFactory.builder(SchemaBuilder.class).authorize(this.authorize).build(schemaFields, d.getSchema()));
			indicatorElastics.add(m);
		}
		return indicatorElastics;
	}
}
