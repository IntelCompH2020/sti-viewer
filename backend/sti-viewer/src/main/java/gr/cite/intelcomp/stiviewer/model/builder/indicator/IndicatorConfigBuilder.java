package gr.cite.intelcomp.stiviewer.model.builder.indicator;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.indicator.IndicatorConfigEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.indicator.IndicatorConfig;
import gr.cite.tools.data.builder.BuilderFactory;
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
public class IndicatorConfigBuilder extends BaseBuilder<IndicatorConfig, IndicatorConfigEntity> {

    private final BuilderFactory builderFactory;
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    public IndicatorConfigBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(IndicatorConfigBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public IndicatorConfigBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<IndicatorConfig> build(FieldSet fields, List<IndicatorConfigEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || data == null || fields.isEmpty())
            return new ArrayList<>();


        List<IndicatorConfig> models = new ArrayList<>(100);

        // TODO make it in bulk
        FieldSet filterColumnsFields = fields.extractPrefixed(this.asPrefix(IndicatorConfig._accessRequestConfig));

        for (IndicatorConfigEntity d : data) {
            IndicatorConfig m = new IndicatorConfig();
            if (!filterColumnsFields.isEmpty() && d.getAccessRequestConfig() != null)
                m.setAccessRequestConfig(this.builderFactory.builder(AccessRequestConfigBuilder.class).authorize(this.authorize).build(filterColumnsFields, d.getAccessRequestConfig()));
            models.add(m);
        }

        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
        return models;
    }
}
