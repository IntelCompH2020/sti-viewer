package gr.cite.intelcomp.stiviewer.model.builder.datagrouprequest;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.datagrouprequest.DataGroupColumnEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.datagrouprequest.DataGroupColumn;
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
public class DataGroupColumnBuilder extends BaseBuilder<DataGroupColumn, DataGroupColumnEntity> {

    @Autowired
    public DataGroupColumnBuilder(
            ConventionService conventionService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DataGroupColumnBuilder.class)));
    }

    public DataGroupColumnBuilder authorize(EnumSet<AuthorizationFlags> values) {
        return this;
    }

    @Override
    public List<DataGroupColumn> build(FieldSet fields, List<DataGroupColumnEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || data == null || fields.isEmpty())
            return new ArrayList<>();

        List<DataGroupColumn> models = new ArrayList<>(100);

        for (DataGroupColumnEntity d : data) {
            DataGroupColumn m = new DataGroupColumn();
            if (fields.hasField(this.asIndexer(DataGroupColumn._fieldCode)))
                m.setFieldCode(d.getFieldCode());
            if (fields.hasField(this.asIndexer(DataGroupColumn._values)))
                m.setValues(d.getValues());
            models.add(m);
        }
        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));

        return models;
    }

}
