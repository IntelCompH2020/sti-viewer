package gr.cite.intelcomp.stiviewer.model.builder.dataaccessrequest;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.dataaccessrequest.DataAccessRequestIndicatorGroupConfigEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.builder.IndicatorGroupBuilder;
import gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequestIndicatorGroupConfig;
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
public class DataAccessRequestIndicatorGroupConfigBuilder extends BaseBuilder<gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequestIndicatorGroupConfig, DataAccessRequestIndicatorGroupConfigEntity> {

    private final BuilderFactory builderFactory;
    private final IndicatorGroupService indicatorGroupService;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);


    @Autowired
    public DataAccessRequestIndicatorGroupConfigBuilder(
            ConventionService conventionService,
            BuilderFactory builderFactory,
            IndicatorGroupService indicatorGroupService
    ) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DataAccessRequestIndicatorGroupConfigBuilder.class)));
        this.builderFactory = builderFactory;
        this.indicatorGroupService = indicatorGroupService;
    }

    public DataAccessRequestIndicatorGroupConfigBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequestIndicatorGroupConfig> build(FieldSet fields, List<DataAccessRequestIndicatorGroupConfigEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || data == null || fields.isEmpty())
            return new ArrayList<>();


        List<gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequestIndicatorGroupConfig> models = new ArrayList<>(100);

        FieldSet indicatorGroupFields = fields.extractPrefixed(this.asPrefix(DataAccessRequestIndicatorGroupConfig._indicatorGroup));
        List<IndicatorGroupEntity> indicatorGroupEntities = !indicatorGroupFields.isEmpty() ? this.indicatorGroupService.getIndicatorGroups() : null;
        Map<UUID, IndicatorGroupEntity> indicatorGroupEntitiesMap = indicatorGroupEntities != null ? indicatorGroupEntities.stream().collect(Collectors.toMap(IndicatorGroupEntity::getId, item -> item)) : null;

        FieldSet filterColumnsFields = fields.extractPrefixed(this.asPrefix(DataAccessRequestIndicatorGroupConfig._filterColumns));
        for (DataAccessRequestIndicatorGroupConfigEntity d : data) {
            gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequestIndicatorGroupConfig m = new DataAccessRequestIndicatorGroupConfig();
            if (!indicatorGroupFields.isEmpty() && indicatorGroupEntitiesMap != null && indicatorGroupEntitiesMap.containsKey(d.getGroupId()))
                m.setIndicatorGroup(this.builderFactory.builder(IndicatorGroupBuilder.class).authorize(this.authorize).build(indicatorGroupFields, indicatorGroupEntitiesMap.get(d.getGroupId())));
            if (!filterColumnsFields.isEmpty() && d.getFilterColumns() != null && !d.getFilterColumns().isEmpty())
                m.setFilterColumns(this.builderFactory.builder(DataAccessFilterColumnConfigBuilder.class).authorize(this.authorize).build(filterColumnsFields, d.getFilterColumns()));
            models.add(m);
        }

        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
        return models;
    }
}
