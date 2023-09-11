package gr.cite.intelcomp.stiviewer.model.builder.datatreeconfig;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.datatreeconfig.DataTreeLevelDashboardOverrideFieldRequirementEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.datatreeconfig.DataTreeLevelDashboardOverrideFieldRequirement;
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
public class DataTreeLevelDashboardOverrideFieldRequirementBuilder extends BaseBuilder<DataTreeLevelDashboardOverrideFieldRequirement, DataTreeLevelDashboardOverrideFieldRequirementEntity> {

    @Autowired
    public DataTreeLevelDashboardOverrideFieldRequirementBuilder(ConventionService conventionService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DataTreeLevelDashboardOverrideFieldRequirementBuilder.class)));
    }

    public DataTreeLevelDashboardOverrideFieldRequirementBuilder authorize(EnumSet<AuthorizationFlags> values) {
        return this;
    }

    @Override
    public List<DataTreeLevelDashboardOverrideFieldRequirement> build(FieldSet fields, List<DataTreeLevelDashboardOverrideFieldRequirementEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} BrowseDataFields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested BrowseDataFields", fields));
        if (fields == null || fields.isEmpty())
            return new ArrayList<>();

        List<DataTreeLevelDashboardOverrideFieldRequirement> models = new LinkedList<>();

        if (data == null)
            return models;
        for (DataTreeLevelDashboardOverrideFieldRequirementEntity d : data) {
            DataTreeLevelDashboardOverrideFieldRequirement m = new DataTreeLevelDashboardOverrideFieldRequirement();
            if (fields.hasField(this.asIndexer(DataTreeLevelDashboardOverrideFieldRequirement._field)))
                m.setField(d.getField());
            if (fields.hasField(this.asIndexer(DataTreeLevelDashboardOverrideFieldRequirement._value)))
                m.setValue(d.getValue());
            models.add(m);
        }

        return models;
    }
}
