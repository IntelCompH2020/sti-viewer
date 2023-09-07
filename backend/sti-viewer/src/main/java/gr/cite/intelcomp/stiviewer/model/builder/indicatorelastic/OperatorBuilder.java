package gr.cite.intelcomp.stiviewer.model.builder.indicatorelastic;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.OperationEntity;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.elasticindicator.Operator;
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
public class OperatorBuilder extends BaseBuilder<Operator, OperationEntity> {

    @Autowired
    public OperatorBuilder(ConventionService conventionService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(OperatorBuilder.class)));
    }

    public OperatorBuilder authorize(EnumSet<AuthorizationFlags> values) {
        return this;
    }

    @Override
    public List<Operator> build(FieldSet fields, List<OperationEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || fields.isEmpty()) return new ArrayList<>();

        List<Operator> operators = new LinkedList<>();

        if (data == null)
            return operators;
        for (OperationEntity d : data) {
            Operator m = new Operator();
            if (fields.hasField(this.asIndexer(Operator._op)))
                m.setOp(d.getOp());

            operators.add(m);
        }

        return operators;
    }
}
