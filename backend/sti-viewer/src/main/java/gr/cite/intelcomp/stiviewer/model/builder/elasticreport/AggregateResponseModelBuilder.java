package gr.cite.intelcomp.stiviewer.model.builder.elasticreport;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.elasticreport.AggregateResponseModel;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.elastic.query.Aggregation.AggregateResponse;
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
public class AggregateResponseModelBuilder extends BaseBuilder<AggregateResponseModel, AggregateResponse> {

    private final BuilderFactory builderFactory;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    public AggregateResponseModelBuilder(
            ConventionService conventionService,
            BuilderFactory builderFactory
    ) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(AggregateResponseModelBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public AggregateResponseModelBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<AggregateResponseModel> build(FieldSet fields, List<AggregateResponse> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));

        List<AggregateResponseModel> models = new ArrayList<>(100);

        if (data == null)
            return models;
        for (AggregateResponse d : data) {
            AggregateResponseModel m = new AggregateResponseModel();
            m.setItems(this.builderFactory.builder(AggregateResponseItemModelBuilder.class).authorize(this.authorize).build(null, d.getItems()));
            m.setAfterKey(d.getAfterKey());
            m.setTotal(d.getTotal());
            models.add(m);
        }
        return models;
    }
}
