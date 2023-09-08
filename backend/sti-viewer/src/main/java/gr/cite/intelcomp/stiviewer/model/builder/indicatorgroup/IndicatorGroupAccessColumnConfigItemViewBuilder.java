package gr.cite.intelcomp.stiviewer.model.builder.indicatorgroup;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupAccessColumnConfigItemViewEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.indicatorgroup.IndicatorGroupAccessColumnConfigItemView;
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
public class IndicatorGroupAccessColumnConfigItemViewBuilder extends BaseBuilder<IndicatorGroupAccessColumnConfigItemView, IndicatorGroupAccessColumnConfigItemViewEntity> {

    public IndicatorGroupAccessColumnConfigItemViewBuilder(ConventionService conventionService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(IndicatorGroupAccessColumnConfigItemViewBuilder.class)));
    }

    public IndicatorGroupAccessColumnConfigItemViewBuilder authorize(EnumSet<AuthorizationFlags> values) {
        return this;
    }

    @Override
    public List<IndicatorGroupAccessColumnConfigItemView> build(FieldSet fields, List<IndicatorGroupAccessColumnConfigItemViewEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || data == null || fields.isEmpty())
            return new ArrayList<>();

        List<IndicatorGroupAccessColumnConfigItemView> models = new ArrayList<>(100);

        for (IndicatorGroupAccessColumnConfigItemViewEntity d : data) {
            IndicatorGroupAccessColumnConfigItemView m = new IndicatorGroupAccessColumnConfigItemView();
            if (fields.hasField(this.asIndexer(IndicatorGroupAccessColumnConfigItemView._value)))
                m.setValue(d.getValue());
            if (fields.hasField(this.asIndexer(IndicatorGroupAccessColumnConfigItemView._status)))
                m.setStatus(d.getStatus());
            models.add(m);
        }
        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
        return models;
    }

}
