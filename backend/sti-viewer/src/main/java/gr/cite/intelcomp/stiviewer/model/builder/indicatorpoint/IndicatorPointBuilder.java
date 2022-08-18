package gr.cite.intelcomp.stiviewer.model.builder.indicatorpoint;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.IndicatorPointEntity;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.builder.DetailItemBuilder;
import gr.cite.intelcomp.stiviewer.model.indicatorpoint.IndicatorPoint;
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
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IndicatorPointBuilder extends BaseBuilder<IndicatorPoint, IndicatorPointEntity> {

	private final BuilderFactory builderFactory;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	@Autowired
	public IndicatorPointBuilder(
			ConventionService conventionService,
			BuilderFactory builderFactory
	) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(DetailItemBuilder.class)));
		this.builderFactory = builderFactory;
	}

	public IndicatorPointBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<IndicatorPoint> build(FieldSet fields, List<IndicatorPointEntity> datas) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(datas).map(e -> e.size()).orElse(0), Optional.ofNullable(fields).map(e -> e.getFields()).map(e -> e.size()).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || fields.isEmpty()) return new ArrayList<>();

		List<IndicatorPoint> models = new ArrayList<>();
		FieldSet groupInfoFields = fields.extractPrefixed(this.asPrefix(IndicatorPoint._groupInfo));

		for (IndicatorPointEntity d : datas) {
			IndicatorPoint m = new IndicatorPoint();
			if (fields.hasField(this.asIndexer(IndicatorPoint._id))) m.setId(d.getId());
			if (fields.hasField(this.asIndexer(IndicatorPoint._batchId))) m.setBatchId(d.getBatchId());
			if (fields.hasField(this.asIndexer(IndicatorPoint._batchTimestamp))) m.setBatchTimestamp(d.getBatchTimestamp());
			if (fields.hasField(this.asIndexer(IndicatorPoint._timestamp))) m.setTimestamp(d.getTimestamp());
			if (fields.hasField(this.asIndexer(IndicatorPoint._groupHash))) m.setGroupHash(d.getGroupHash());
			if (!groupInfoFields.isEmpty() && d.getGroupInfo() != null) m.setGroupInfo(this.builderFactory.builder(DataGroupInfoBuilder.class).authorize(this.authorize).build(groupInfoFields, d.getGroupInfo()));
			if (d.getProperties() != null) {
				Map<String, Object> properties = new HashMap<>();
				for (Map.Entry<String, Object> prop : d.getProperties().entrySet()) {
					if (fields.hasField(this.asIndexer(prop.getKey()))) properties.put(prop.getKey(), prop.getValue());
				}
				if (properties.size() > 0) m.setProperties(properties);
			}
			models.add(m);
		}
		this.logger.debug("build {} items", Optional.ofNullable(models).map(e -> e.size()).orElse(0));
		return models;
	}
}
