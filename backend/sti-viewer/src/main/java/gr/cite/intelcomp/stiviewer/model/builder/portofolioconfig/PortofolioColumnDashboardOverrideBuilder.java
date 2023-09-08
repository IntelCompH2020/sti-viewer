package gr.cite.intelcomp.stiviewer.model.builder.portofolioconfig;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.portofolioconfig.PortofolioColumnDashboardOverrideEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.portofolioconfig.PortofolioColumnDashboardOverride;
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
public class PortofolioColumnDashboardOverrideBuilder extends BaseBuilder<PortofolioColumnDashboardOverride, PortofolioColumnDashboardOverrideEntity> {

    private final BuilderFactory builderFactory;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public PortofolioColumnDashboardOverrideBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(PortofolioColumnDashboardOverrideBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public PortofolioColumnDashboardOverrideBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<PortofolioColumnDashboardOverride> build(FieldSet fields, List<PortofolioColumnDashboardOverrideEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} BrowseDataFields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested BrowseDataFields", fields));
        if (fields == null || fields.isEmpty())
            return new ArrayList<>();

        FieldSet requirementsFields = fields.extractPrefixed(this.asPrefix(PortofolioColumnDashboardOverride._requirements));

        List<PortofolioColumnDashboardOverride> models = new ArrayList<>(100);

        if (data == null)
            return models;
        for (PortofolioColumnDashboardOverrideEntity d : data) {
            PortofolioColumnDashboardOverride m = new PortofolioColumnDashboardOverride();
            if (fields.hasField(this.asIndexer(PortofolioColumnDashboardOverride._supportedDashboards)))
                m.setSupportedDashboards(d.getSupportedDashboards());
            if (!requirementsFields.isEmpty() && d.getRequirements() != null)
                m.setRequirements(this.builderFactory.builder(PortofolioColumnDashboardOverrideFieldRequirementBuilder.class).authorize(this.authorize).build(requirementsFields, d.getRequirements()));
            models.add(m);
        }

        return models;
    }
}
