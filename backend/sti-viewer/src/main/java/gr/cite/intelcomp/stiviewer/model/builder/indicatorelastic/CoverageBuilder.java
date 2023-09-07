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
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CoverageBuilder extends BaseBuilder<Coverage, CoverageEntity> {

    @Autowired
    public CoverageBuilder(ConventionService conventionService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(CoverageBuilder.class)));
    }

    public CoverageBuilder authorize(EnumSet<AuthorizationFlags> values) {
        return this;
    }

    @Override
    public List<Coverage> build(FieldSet fields, List<CoverageEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || fields.isEmpty())
            return new ArrayList<>();

        List<Coverage> coverages = new ArrayList<>(100);

        if (data == null)
            return coverages;
        for (CoverageEntity d : data) {
            Coverage m = new Coverage();
            if (fields.hasField(this.asIndexer(Coverage._label)))
                m.setLabel(d.getLabel());
            if (fields.hasField(this.asIndexer(Coverage._max)))
                m.setMax(d.getMax());
            if (fields.hasField(this.asIndexer(Coverage._min)))
                m.setMin(d.getMin());
            coverages.add(m);
        }

        return coverages;
    }
}
