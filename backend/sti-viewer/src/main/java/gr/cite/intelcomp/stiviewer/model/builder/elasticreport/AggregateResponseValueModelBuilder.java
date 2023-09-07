package gr.cite.intelcomp.stiviewer.model.builder.elasticreport;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.elasticreport.AggregateResponseValueModel;
import gr.cite.tools.elastic.query.Aggregation.AggregateResponseValue;
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
public class AggregateResponseValueModelBuilder extends BaseBuilder<AggregateResponseValueModel, AggregateResponseValue> {

    public AggregateResponseValueModelBuilder(
            ConventionService conventionService
    ) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(AggregateResponseValueModelBuilder.class)));
    }

    public AggregateResponseValueModelBuilder authorize(EnumSet<AuthorizationFlags> values) {
        return this;
    }

    @Override
    public List<AggregateResponseValueModel> build(FieldSet fields, List<AggregateResponseValue> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));

        List<AggregateResponseValueModel> models = new ArrayList<>(100);

        if (data == null)
            return models;
        for (AggregateResponseValue d : data) {
            AggregateResponseValueModel m = new AggregateResponseValueModel();
            m.setValue(d.getValue());
            m.setField(d.getField());
            m.setAggregateType(d.getAggregateType());
            models.add(m);
        }
        return models;
    }
}
