package gr.cite.intelcomp.stiviewer.model.builder.elasticreport;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.elasticreport.AggregateResponseGroupModel;
import gr.cite.tools.elastic.query.Aggregation.AggregateResponseGroup;
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
public class AggregateResponseGroupModelBuilder extends BaseBuilder<AggregateResponseGroupModel, AggregateResponseGroup> {

    public AggregateResponseGroupModelBuilder(
            ConventionService conventionService
    ) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(AggregateResponseGroupModelBuilder.class)));
    }

    public AggregateResponseGroupModelBuilder authorize(EnumSet<AuthorizationFlags> values) {
        return this;
    }

    @Override
    public List<AggregateResponseGroupModel> build(FieldSet fields, List<AggregateResponseGroup> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));

        List<AggregateResponseGroupModel> models = new ArrayList<>(100);

        if (data == null)
            return models;
        for (AggregateResponseGroup d : data) {
            AggregateResponseGroupModel m = new AggregateResponseGroupModel();
            m.setItems(d.getItems());
            models.add(m);
        }
        return models;
    }
}
