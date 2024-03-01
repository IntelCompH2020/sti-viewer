package gr.cite.intelcomp.stiviewer.model.builder.indicatorelastic;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.IndicatorMetadataEntity;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.elasticindicator.Metadata;
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
public class MetadataBuilder extends BaseBuilder<Metadata, IndicatorMetadataEntity> {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(MetadataBuilder.class));

	private final BuilderFactory builderFactory;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	@Autowired
	public MetadataBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
		super(conventionService, logger);
		this.builderFactory = builderFactory;
	}

	public MetadataBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<Metadata> build(FieldSet fields, List<IndicatorMetadataEntity> datas) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(datas).map(e -> e.size()).orElse(0), Optional.ofNullable(fields).map(e -> e.getFields()).map(e -> e.size()).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || fields.isEmpty()) return new ArrayList<>();

		FieldSet altDescriptionsFields = fields.extractPrefixed(this.asPrefix(Metadata._altDescriptions));
		FieldSet coverageFields = fields.extractPrefixed(this.asPrefix(Metadata._coverage));
		FieldSet altLabelsFields = fields.extractPrefixed(this.asPrefix(Metadata._altLabels));
		FieldSet semanticLabelsFields = fields.extractPrefixed(this.asPrefix(Metadata._semanticLabels));

		List<Metadata> metadata = new LinkedList<>();
		for (IndicatorMetadataEntity d : datas) {
			Metadata m = new Metadata();
			if (fields.hasField(this.asIndexer(Metadata._code))) m.setCode(d.getCode());
			if (fields.hasField(this.asIndexer(Metadata._date))) m.setDate(d.getDate());
			if (fields.hasField(this.asIndexer(Metadata._description))) m.setDescription(d.getDescription());
			if (fields.hasField(this.asIndexer(Metadata._label))) m.setLabel(d.getLabel());
			if (fields.hasField(this.asIndexer(Metadata._url))) m.setUrl(d.getUrl());
			if (!altDescriptionsFields.isEmpty() && d.getAltDescriptions() != null) m.setAltDescriptions(this.builderFactory.builder(AltTextBuilder.class).authorize(this.authorize).build(altDescriptionsFields, d.getAltDescriptions()));
			if (!coverageFields.isEmpty() && d.getCoverage() != null) m.setCoverage(this.builderFactory.builder(CoverageBuilder.class).authorize(this.authorize).build(coverageFields, d.getCoverage()));
			if (!altLabelsFields.isEmpty() && d.getAltLabels() != null) m.setAltLabels(this.builderFactory.builder(AltTextBuilder.class).authorize(this.authorize).build(altLabelsFields, d.getAltLabels()));
			if (!semanticLabelsFields.isEmpty() && d.getSemanticLabels() != null) m.setSemanticLabels(this.builderFactory.builder(SemanticsLabelBuilder.class).authorize(this.authorize).build(semanticLabelsFields, d.getSemanticLabels()));

			metadata.add(m);
		}

		return metadata;
	}
}
