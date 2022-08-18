package gr.cite.intelcomp.stiviewer.model.builder.indicatorelastic;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.CoverageEntity;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.elasticindicator.Coverage;
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
public class CoverageBuilder extends BaseBuilder<Coverage, CoverageEntity> {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(CoverageBuilder.class));

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	@Autowired
	public CoverageBuilder(ConventionService conventionService) {
		super(conventionService, logger);
	}

	public CoverageBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<Coverage> build(FieldSet fields, List<CoverageEntity> datas) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(datas).map(e -> e.size()).orElse(0), Optional.ofNullable(fields).map(e -> e.getFields()).map(e -> e.size()).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || fields.isEmpty()) return new ArrayList<>();

		List<Coverage> coverages = new LinkedList<>();
		for (CoverageEntity d : datas) {
			Coverage m = new Coverage();
			if (fields.hasField(this.asIndexer(Coverage._label))) m.setLabel(d.getLabel());
			if (fields.hasField(this.asIndexer(Coverage._max))) m.setMax(d.getMax());
			if (fields.hasField(this.asIndexer(Coverage._min))) m.setMin(d.getMin());
			coverages.add(m);
		}

		return coverages;
	}
}
