package gr.cite.intelcomp.stiviewer.model.builder.indicatorelastic;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.IndicatorSchemaEntity;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.elasticindicator.Schema;
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
public class SchemaBuilder extends BaseBuilder<Schema, IndicatorSchemaEntity> {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(SchemaBuilder.class));

	private final BuilderFactory builderFactory;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	@Autowired
	public SchemaBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
		super(conventionService, logger);

		this.builderFactory = builderFactory;

	}

	public SchemaBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<Schema> build(FieldSet fields, List<IndicatorSchemaEntity> datas) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(datas).map(e -> e.size()).orElse(0), Optional.ofNullable(fields).map(e -> e.getFields()).map(e -> e.size()).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || fields.isEmpty()) return new ArrayList<>();

		FieldSet fieldsFields = fields.extractPrefixed(this.asPrefix(Schema._fields));

		List<Schema> schemas = new LinkedList<>();
		for (IndicatorSchemaEntity d : datas) {
			Schema m = new Schema();
			if (fields.hasField(this.asIndexer(Schema._id))) m.setId(d.getId());
			if (!fieldsFields.isEmpty() && d.getFields() != null) m.setFields(this.builderFactory.builder(FieldBuilder.class).authorize(this.authorize).build(fieldsFields, d.getFields()));

			schemas.add(m);
		}

		return schemas;
	}
}
