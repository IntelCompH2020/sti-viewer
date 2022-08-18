package gr.cite.intelcomp.stiviewer.model.builder.indicatorelastic;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.ValueRangeEntity;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.elasticindicator.ValueRange;
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
public class ValueRangeBuilder extends BaseBuilder<ValueRange, ValueRangeEntity> {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(ValueRangeBuilder.class));

	private final BuilderFactory builderFactory;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	@Autowired
	public ValueRangeBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
		super(conventionService, logger);
		this.builderFactory = builderFactory;
	}

	public ValueRangeBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<ValueRange> build(FieldSet fields, List<ValueRangeEntity> datas) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(datas).map(e -> e.size()).orElse(0), Optional.ofNullable(fields).map(e -> e.getFields()).map(e -> e.size()).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || fields.isEmpty()) return new ArrayList<>();
		FieldSet valuesFields = fields.extractPrefixed(this.asPrefix(ValueRange._values));

		List<ValueRange> valueRanges = new LinkedList<>();
		for (ValueRangeEntity d : datas) {
			ValueRange m = new ValueRange();
			if (fields.hasField(this.asIndexer(ValueRange._min))) m.setMin(d.getMin());
			if (fields.hasField(this.asIndexer(ValueRange._max))) m.setMax(d.getMax());
			if (!valuesFields.isEmpty() && d.getValues() != null) m.setValues(this.builderFactory.builder(ValueRangeValueBuilder.class).authorize(this.authorize).build(valuesFields, d.getValues()));
			valueRanges.add(m);
		}

		return valueRanges;
	}
}
