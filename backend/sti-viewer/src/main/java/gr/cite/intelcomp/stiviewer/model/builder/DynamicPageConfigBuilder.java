package gr.cite.intelcomp.stiviewer.model.builder;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.dynamicpageconfig.DynamicPageConfigEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.DynamicPageConfig;
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
public class DynamicPageConfigBuilder extends BaseBuilder<DynamicPageConfig, DynamicPageConfigEntity> {

    public DynamicPageConfigBuilder(ConventionService conventionService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DynamicPageConfigBuilder.class)));
    }

    public DynamicPageConfigBuilder authorize(EnumSet<AuthorizationFlags> values) {
        return this;
    }

    @Override
    public List<DynamicPageConfig> build(FieldSet fields, List<DynamicPageConfigEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();

        List<DynamicPageConfig> models = new ArrayList<>(100);

        for (DynamicPageConfigEntity d : data) {
            DynamicPageConfig m = new DynamicPageConfig();
            if (fields.hasField(this.asIndexer(DynamicPageConfig._allowedRoles)))
                m.setAllowedRoles(d.getAllowedRoles());
            if (fields.hasField(this.asIndexer(DynamicPageConfig._externalUrl)))
                m.setExternalUrl(d.getExternalUrl());
            if (fields.hasField(this.asIndexer(DynamicPageConfig._matIcon)))
                m.setMatIcon(d.getMatIcon());
            models.add(m);
        }

        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
        return models;
    }
}
