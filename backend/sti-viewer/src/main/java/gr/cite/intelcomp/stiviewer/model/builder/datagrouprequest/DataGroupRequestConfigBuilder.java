package gr.cite.intelcomp.stiviewer.model.builder.datagrouprequest;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.datagrouprequest.DataGroupRequestConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.builder.IndicatorGroupBuilder;
import gr.cite.intelcomp.stiviewer.model.datagrouprequest.DataGroupRequestConfig;
import gr.cite.intelcomp.stiviewer.service.indicatorgroup.IndicatorGroupService;
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
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataGroupRequestConfigBuilder extends BaseBuilder<DataGroupRequestConfig, DataGroupRequestConfigEntity> {

    private final BuilderFactory builderFactory;
    private final IndicatorGroupService indicatorGroupService;
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public DataGroupRequestConfigBuilder(
            ConventionService conventionService,
            BuilderFactory builderFactory,
            IndicatorGroupService indicatorGroupService
    ) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DataGroupRequestConfigBuilder.class)));
        this.builderFactory = builderFactory;
        this.indicatorGroupService = indicatorGroupService;
    }

    public DataGroupRequestConfigBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<DataGroupRequestConfig> build(FieldSet fields, List<DataGroupRequestConfigEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || data == null || fields.isEmpty())
            return new ArrayList<>();


        List<DataGroupRequestConfig> models = new ArrayList<>(100);

        FieldSet groupColumnFields = fields.extractPrefixed(this.asPrefix(DataGroupRequestConfig._groupColumns));

        FieldSet indicatorGroupFields = fields.extractPrefixed(this.asPrefix(DataGroupRequestConfig._indicatorGroup));
        List<IndicatorGroupEntity> indicatorGroupEntities = !indicatorGroupFields.isEmpty() ? this.indicatorGroupService.getIndicatorGroups() : null;
        Map<UUID, IndicatorGroupEntity> indicatorGroupEntitiesMap = indicatorGroupEntities != null ? indicatorGroupEntities.stream().collect(Collectors.toMap(x -> x.getId(), item -> item)) : null;

        for (DataGroupRequestConfigEntity d : data) {
            DataGroupRequestConfig m = new DataGroupRequestConfig();
            if (!groupColumnFields.isEmpty() && d.getGroupColumns() != null)
                m.setGroupColumns(this.builderFactory.builder(DataGroupColumnBuilder.class).authorize(this.authorize).build(groupColumnFields, d.getGroupColumns()));
            if (!indicatorGroupFields.isEmpty() && indicatorGroupEntitiesMap != null && indicatorGroupEntitiesMap.containsKey(d.getIndicatorGroupId()))
                m.setIndicatorGroup(this.builderFactory.builder(IndicatorGroupBuilder.class).authorize(this.authorize).build(indicatorGroupFields, indicatorGroupEntitiesMap.get(d.getIndicatorGroupId())));
            models.add(m);
        }

        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
        return models;
    }

}
