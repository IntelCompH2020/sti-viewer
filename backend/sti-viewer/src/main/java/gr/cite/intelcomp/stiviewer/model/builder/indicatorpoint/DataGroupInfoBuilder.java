package gr.cite.intelcomp.stiviewer.model.builder.indicatorpoint;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.DataGroupInfoEntity;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.indicatorpoint.DataGroupInfo;
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
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataGroupInfoBuilder extends BaseBuilder<DataGroupInfo, DataGroupInfoEntity> {

    private final BuilderFactory builderFactory;
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public DataGroupInfoBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DataGroupInfoBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public DataGroupInfoBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<DataGroupInfo> build(FieldSet fields, List<DataGroupInfoEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || fields.isEmpty())
            return new ArrayList<>();

        FieldSet columnsFields = fields.extractPrefixed(this.asPrefix(DataGroupInfo._columns));

        List<DataGroupInfo> dataGroupInfo = new ArrayList<>(100);

        if (data == null)
            return dataGroupInfo;
        for (DataGroupInfoEntity d : data) {
            DataGroupInfo m = new DataGroupInfo();
            if (!columnsFields.isEmpty() && d.getColumns() != null)
                m.setColumns(this.builderFactory.builder(DataGroupInfoColumnBuilder.class).authorize(this.authorize).build(columnsFields, d.getColumns()));

            dataGroupInfo.add(m);
        }

        return dataGroupInfo;
    }
}
