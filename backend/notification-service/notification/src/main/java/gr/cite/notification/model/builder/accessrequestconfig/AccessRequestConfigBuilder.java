package gr.cite.notification.model.builder.accessrequestconfig;

import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.convention.ConventionService;
import gr.cite.notification.model.accessrequestconfig.AccessRequestConfig;
import gr.cite.notification.model.builder.BaseBuilder;
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
public class AccessRequestConfigBuilder extends BaseBuilder<AccessRequestConfig, gr.cite.notification.common.types.accessrequestconfig.AccessRequestConfig> {

    private final BuilderFactory builderFactory;
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    public AccessRequestConfigBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(AccessRequestConfigBuilder.class)));
        this.builderFactory = builderFactory;
    }
    public AccessRequestConfigBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }
    @Override
    public List<AccessRequestConfig> build(FieldSet fields, List<gr.cite.notification.common.types.accessrequestconfig.AccessRequestConfig> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();


        List<AccessRequestConfig> models = new ArrayList<>();

        // TODO make it in bulk
        FieldSet filterColumnsFields = fields.extractPrefixed(this.asPrefix(AccessRequestConfig._filterColumns));

        for (gr.cite.notification.common.types.accessrequestconfig.AccessRequestConfig d : data) {
            AccessRequestConfig m = new AccessRequestConfig();
            if (!filterColumnsFields.isEmpty() && d.getFilterColumns() != null) m.setFilterColumns(this.builderFactory.builder(FilterColumnConfigBuilder.class).authorize(this.authorize).build(filterColumnsFields, d.getFilterColumns()));
            models.add(m);
        }

        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
        return models;
    }
}
