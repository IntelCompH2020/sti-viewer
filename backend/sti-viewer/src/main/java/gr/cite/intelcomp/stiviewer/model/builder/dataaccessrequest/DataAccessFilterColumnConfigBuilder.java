package gr.cite.intelcomp.stiviewer.model.builder.dataaccessrequest;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.dataaccessrequest.DataAccessRequestFilterColumnEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequestFilterColumnConfig;
import gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequestIndicatorConfig;
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
public class DataAccessFilterColumnConfigBuilder extends BaseBuilder<DataAccessRequestFilterColumnConfig, DataAccessRequestFilterColumnEntity> {

    @Autowired
    public DataAccessFilterColumnConfigBuilder(
            ConventionService conventionService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DataAccessFilterColumnConfigBuilder.class)));
    }

    public DataAccessFilterColumnConfigBuilder authorize(EnumSet<AuthorizationFlags> values) {
        return this;
    }

    @Override
    public List<DataAccessRequestFilterColumnConfig> build(FieldSet fields, List<DataAccessRequestFilterColumnEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || data == null || fields.isEmpty())
            return new ArrayList<>();


        List<DataAccessRequestFilterColumnConfig> models = new ArrayList<>(100);

        fields.extractPrefixed(this.asPrefix(DataAccessRequestIndicatorConfig._filterColumns));

        for (DataAccessRequestFilterColumnEntity d : data) {
            DataAccessRequestFilterColumnConfig m = new DataAccessRequestFilterColumnConfig();
            if (fields.hasField(this.asIndexer(DataAccessRequestFilterColumnConfig._column)))
                m.setColumn(d.getColumn());
            if (fields.hasField(this.asIndexer(DataAccessRequestFilterColumnConfig._values)))
                m.setValues(d.getValues());
            models.add(m);
        }
        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));

        return models;
    }

}
