package gr.cite.intelcomp.stiviewer.model.builder.portofolioconfig;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.portofolioconfig.PortofolioColumnDashboardOverrideFieldRequirementEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.portofolioconfig.PortofolioColumnDashboardOverrideFieldRequirement;
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
public class PortofolioColumnDashboardOverrideFieldRequirementBuilder extends BaseBuilder<PortofolioColumnDashboardOverrideFieldRequirement, PortofolioColumnDashboardOverrideFieldRequirementEntity> {

    @Autowired
    public PortofolioColumnDashboardOverrideFieldRequirementBuilder(ConventionService conventionService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(PortofolioColumnDashboardOverrideFieldRequirementBuilder.class)));
    }

    public PortofolioColumnDashboardOverrideFieldRequirementBuilder authorize(EnumSet<AuthorizationFlags> values) {
        return this;
    }

    @Override
    public List<PortofolioColumnDashboardOverrideFieldRequirement> build(FieldSet fields, List<PortofolioColumnDashboardOverrideFieldRequirementEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} BrowseDataFields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested BrowseDataFields", fields));
        if (fields == null || fields.isEmpty())
            return new ArrayList<>();

        List<PortofolioColumnDashboardOverrideFieldRequirement> models = new ArrayList<>(100);

        if (data == null)
            return models;
        for (PortofolioColumnDashboardOverrideFieldRequirementEntity d : data) {
            PortofolioColumnDashboardOverrideFieldRequirement m = new PortofolioColumnDashboardOverrideFieldRequirement();
            if (fields.hasField(this.asIndexer(PortofolioColumnDashboardOverrideFieldRequirement._value)))
                m.setValue(d.getValue());
            models.add(m);
        }

        return models;
    }
}
