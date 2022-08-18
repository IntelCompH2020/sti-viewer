package gr.cite.intelcomp.stiviewer.model.builder.indicatorelastic;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.SemanticLabelEntity;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.elasticindicator.SemanticsLabel;
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
public class SemanticsLabelBuilder extends BaseBuilder<SemanticsLabel, SemanticLabelEntity> {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(SemanticsLabelBuilder.class));

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	@Autowired
	public SemanticsLabelBuilder(ConventionService conventionService) {
		super(conventionService, logger);
	}

	public SemanticsLabelBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<SemanticsLabel> build(FieldSet fields, List<SemanticLabelEntity> datas) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(datas).map(e -> e.size()).orElse(0), Optional.ofNullable(fields).map(e -> e.getFields()).map(e -> e.size()).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || fields.isEmpty()) return new ArrayList<>();

		List<SemanticsLabel> semanticsLabels = new LinkedList<>();
		for (SemanticLabelEntity d : datas) {
			SemanticsLabel m = new SemanticsLabel();
			if (fields.hasField(this.asIndexer(SemanticsLabel._label))) m.setLabel(d.getLabel());
			semanticsLabels.add(m);
		}

		return semanticsLabels;
	}
}
