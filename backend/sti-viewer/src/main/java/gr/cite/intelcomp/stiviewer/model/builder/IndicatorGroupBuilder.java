package gr.cite.intelcomp.stiviewer.model.builder;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.IndicatorGroup;
import gr.cite.intelcomp.stiviewer.model.builder.indicatorgroup.FilterColumnBuilder;
import gr.cite.intelcomp.stiviewer.query.IndicatorQuery;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.BaseFieldSet;
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
public class IndicatorGroupBuilder extends BaseBuilder<IndicatorGroup, IndicatorGroupEntity> {

    private final QueryFactory queryFactory;
    private final BuilderFactory builderFactory;
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public IndicatorGroupBuilder(
            ConventionService conventionService,
            QueryFactory queryFactory, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(IndicatorGroupBuilder.class)));
        this.queryFactory = queryFactory;
        this.builderFactory = builderFactory;
    }

    public IndicatorGroupBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<IndicatorGroup> build(FieldSet fields, List<IndicatorGroupEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || data == null || fields.isEmpty())
            return new ArrayList<>();

        FieldSet indicatorFields = fields.extractPrefixed(this.asPrefix(IndicatorGroup._indicators));
        Map<UUID, Indicator> indicatorItemsMap = this.collectIndicators(indicatorFields, data.stream().map(IndicatorGroupEntity::getIndicatorIds).flatMap(List::stream).collect(Collectors.toList()));

        FieldSet filterColumnsFields = fields.extractPrefixed(this.asPrefix(IndicatorGroup._filterColumns));

        List<IndicatorGroup> models = new ArrayList<>(100);

        for (IndicatorGroupEntity d : data) {
            IndicatorGroup m = new IndicatorGroup();
            if (fields.hasField(this.asIndexer(IndicatorGroup._id)))
                m.setId(d.getId());
            if (fields.hasField(this.asIndexer(IndicatorGroup._code)))
                m.setCode(d.getCode());
            if (fields.hasField(this.asIndexer(IndicatorGroup._name)))
                m.setName(d.getName());
            if (!filterColumnsFields.isEmpty() && d.getFilterColumns() != null)
                m.setFilterColumns(this.builderFactory.builder(FilterColumnBuilder.class).authorize(this.authorize).build(filterColumnsFields, d.getFilterColumns()));
            if (!indicatorFields.isEmpty() && indicatorItemsMap != null) {
                if (d.getIndicatorIds() != null && !d.getIndicatorIds().isEmpty()) {
                    List<Indicator> indicators = new ArrayList<>();
                    for (UUID indicatorId : d.getIndicatorIds()) {
                        if (indicatorItemsMap.containsKey(indicatorId))
                            indicators.add(indicatorItemsMap.get(indicatorId));
                    }
                    m.setIndicators(indicators);
                }
            }
            models.add(m);
        }

        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
        return models;
    }

    private Map<UUID, Indicator> collectIndicators(FieldSet fields, List<UUID> ids) throws MyApplicationException {
        if (fields.isEmpty() || ids.isEmpty())
            return null;
        this.logger.debug("checking related - {}", Indicator.class.getSimpleName());

        Map<UUID, Indicator> itemMap;
        if (!fields.hasOtherField(this.asIndexer(Indicator._id))) {
            itemMap = this.asEmpty(
                    ids,
                    x -> {
                        Indicator item = new Indicator();
                        item.setId(x);
                        return item;
                    },
                    Indicator::getId);
        } else {
            FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(Indicator._id);
            IndicatorQuery q = this.queryFactory.query(IndicatorQuery.class).authorize(this.authorize).ids(ids);
            itemMap = this.builderFactory.builder(IndicatorBuilder.class).authorize(this.authorize).asForeignKey(q, clone, Indicator::getId);
        }
        if (!fields.hasField(Indicator._id)) {
            itemMap.forEach((id, indicator) -> {
                if (indicator != null)
                    indicator.setId(null);
            });
        }

        return itemMap;
    }

}
