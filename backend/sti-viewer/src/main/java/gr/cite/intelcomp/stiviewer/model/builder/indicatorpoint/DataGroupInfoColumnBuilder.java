package gr.cite.intelcomp.stiviewer.model.builder.indicatorpoint;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.DataGroupInfoColumnEntity;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.indicatorpoint.DataGroupInfoColumn;
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
public class DataGroupInfoColumnBuilder extends BaseBuilder<DataGroupInfoColumn, DataGroupInfoColumnEntity> {

    @Autowired
    public DataGroupInfoColumnBuilder(ConventionService conventionService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DataGroupInfoColumnBuilder.class)));
    }

    public DataGroupInfoColumnBuilder authorize(EnumSet<AuthorizationFlags> values) {
        return this;
    }

    @Override
    public List<DataGroupInfoColumn> build(FieldSet fields, List<DataGroupInfoColumnEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || fields.isEmpty())
            return new ArrayList<>();

        List<DataGroupInfoColumn> dataGroupInfoColumns = new ArrayList<>(100);

        if (data == null)
            return dataGroupInfoColumns;
        for (DataGroupInfoColumnEntity d : data) {
            DataGroupInfoColumn m = new DataGroupInfoColumn();
            if (fields.hasField(this.asIndexer(DataGroupInfoColumn._fieldCode)))
                m.setFieldCode(d.getFieldCode());
            if (fields.hasField(this.asIndexer(DataGroupInfoColumn._values)))
                m.setValues(d.getValues());
            dataGroupInfoColumns.add(m);
        }

        return dataGroupInfoColumns;
    }
}
