package gr.cite.intelcomp.stiviewer.model.builder.portofolioconfig;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.portofolioconfig.DataFieldEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.portofolioconfig.DataField;
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
public class DataFieldBuilder extends BaseBuilder<DataField, DataFieldEntity> {

    @Autowired
    public DataFieldBuilder(ConventionService conventionService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DataFieldBuilder.class)));
    }

    public DataFieldBuilder authorize(EnumSet<AuthorizationFlags> values) {
        return this;
    }

    @Override
    public List<DataField> build(FieldSet fields, List<DataFieldEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} BrowseDataFields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested BrowseDataFields", fields));
        if (fields == null || fields.isEmpty())
            return new ArrayList<>();

        List<DataField> models = new ArrayList<>(100);

        if (data == null)
            return models;
        for (DataFieldEntity d : data) {
            DataField m = new DataField();
            if (fields.hasField(this.asIndexer(DataField._code)))
                m.setCode(d.getCode());
            if (fields.hasField(this.asIndexer(DataField._name)))
                m.setName(d.getName());
            models.add(m);
        }

        return models;
    }
}
