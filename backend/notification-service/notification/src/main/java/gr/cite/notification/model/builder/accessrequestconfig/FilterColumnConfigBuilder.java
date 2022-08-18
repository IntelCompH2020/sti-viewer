package gr.cite.notification.model.builder.accessrequestconfig;

import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.convention.ConventionService;
import gr.cite.notification.model.accessrequestconfig.FilterColumnConfig;
import gr.cite.notification.model.builder.BaseBuilder;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
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
public class FilterColumnConfigBuilder extends BaseBuilder<FilterColumnConfig, gr.cite.notification.common.types.accessrequestconfig.FilterColumnConfig> {

    private final QueryFactory queryFactory;
    private final BuilderFactory builderFactory;
    private final ConventionService conventionService;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    public FilterColumnConfigBuilder(ConventionService conventionService,
                                     QueryFactory queryFactory,
                                     BuilderFactory builderFactory, ConventionService conventionService1) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(FilterColumnConfigBuilder.class)));
        this.queryFactory = queryFactory;
        this.builderFactory = builderFactory;
        this.conventionService = conventionService1;
    }
    public FilterColumnConfigBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<FilterColumnConfig> build(FieldSet fields, List<gr.cite.notification.common.types.accessrequestconfig.FilterColumnConfig> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();
        List<FilterColumnConfig> models = new ArrayList<>();
        for (gr.cite.notification.common.types.accessrequestconfig.FilterColumnConfig d : data) {
            FilterColumnConfig m = new FilterColumnConfig();
            if (fields.hasField(this.asIndexer(FilterColumnConfig._code)) && !conventionService.isNullOrEmpty(d.getCode())) m.setCode(d.getCode());
            models.add(m);
        }
        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
        return models;
    }

}
