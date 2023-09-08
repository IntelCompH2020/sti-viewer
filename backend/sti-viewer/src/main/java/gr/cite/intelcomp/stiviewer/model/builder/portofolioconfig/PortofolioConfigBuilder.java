package gr.cite.intelcomp.stiviewer.model.builder.portofolioconfig;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupEntity;
import gr.cite.intelcomp.stiviewer.common.types.portofolioconfig.PortofolioConfigEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.IndicatorGroup;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.builder.IndicatorGroupBuilder;
import gr.cite.intelcomp.stiviewer.model.portofolioconfig.PortofolioColumnConfig;
import gr.cite.intelcomp.stiviewer.model.portofolioconfig.PortofolioConfig;
import gr.cite.intelcomp.stiviewer.service.indicatorgroup.IndicatorGroupService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PortofolioConfigBuilder extends BaseBuilder<PortofolioConfig, PortofolioConfigEntity> {

    private final BuilderFactory builderFactory;
    private final IndicatorGroupService indicatorGroupService;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    public PortofolioConfigBuilder(BuilderFactory builderFactory,
                                   ConventionService conventionService,
                                   IndicatorGroupService indicatorGroupService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(PortofolioConfigBuilder.class)));
        this.builderFactory = builderFactory;
        this.indicatorGroupService = indicatorGroupService;
    }

    public PortofolioConfigBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<PortofolioConfig> build(FieldSet fields, List<PortofolioConfigEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || data == null || fields.isEmpty())
            return new ArrayList<>();

        List<PortofolioConfig> dataTreeConfigs = new ArrayList<>(100);

        FieldSet columnFields = fields.extractPrefixed(this.asPrefix(PortofolioConfig._columns));

        FieldSet indicatorGroupFields = fields.extractPrefixed(this.asPrefix(PortofolioConfig._indicatorGroup));
        Map<String, IndicatorGroup> indicatorGroupItemsMap = this.collectIndicatorGroups(indicatorGroupFields, data.stream().map(PortofolioConfigEntity::getIndicatorGroupCode).distinct().collect(Collectors.toList()));

        for (PortofolioConfigEntity d : data) {
            PortofolioConfig m = new PortofolioConfig();

            if (fields.hasField(this.asIndexer(PortofolioConfig._code)))
                m.setCode(d.getCode());
            if (fields.hasField(this.asIndexer(PortofolioConfig._name)))
                m.setName(d.getName());
            if (fields.hasField(this.asIndexer(PortofolioColumnConfig._defaultDashboards)))
                m.setDefaultDashboards(d.getDefaultDashboards());
            if (!columnFields.isEmpty() && d.getColumns() != null)
                m.setColumns(this.builderFactory.builder(PortofolioColumnConfigBuilder.class).authorize(this.authorize).build(columnFields, d.getColumns()));
            if (!indicatorGroupFields.isEmpty() && indicatorGroupItemsMap != null && indicatorGroupItemsMap.containsKey(d.getIndicatorGroupCode()))
                m.setIndicatorGroup(indicatorGroupItemsMap.get(d.getIndicatorGroupCode()));

            dataTreeConfigs.add(m);
        }
        return dataTreeConfigs;
    }

    private Map<String, IndicatorGroup> collectIndicatorGroups(FieldSet fields, List<String> codes) throws MyApplicationException {
        if (fields.isEmpty() || codes.isEmpty()) return null;
        this.logger.debug("checking related - {}", IndicatorGroup.class.getSimpleName());

        Map<String, IndicatorGroup> itemMap;
        if (!fields.hasOtherField(this.asIndexer(IndicatorGroup._code))) {
            itemMap = this.asEmpty(
                    codes,
                    x -> {
                        IndicatorGroup item = new IndicatorGroup();
                        item.setCode(x);
                        return item;
                    },
                    IndicatorGroup::getCode);
        } else {
            FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(IndicatorGroup._id);
            List<IndicatorGroupEntity> items = indicatorGroupService.getIndicatorGroups().stream().filter(x -> codes.contains(x.getCode())).collect(Collectors.toList());
            itemMap = this.builderFactory.builder(IndicatorGroupBuilder.class).authorize(this.authorize).asForeignKey(items, clone, IndicatorGroup::getCode);
        }
        if (!fields.hasField(Indicator._id)) {
            itemMap = itemMap.values().stream().filter(Objects::nonNull).peek(x -> x.setId(null)).collect(Collectors.toMap(x -> x.getId().toString(), Function.identity()));
        }

        return itemMap;
    }
}
