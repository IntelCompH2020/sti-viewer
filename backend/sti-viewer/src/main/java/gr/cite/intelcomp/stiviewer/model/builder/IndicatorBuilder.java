package gr.cite.intelcomp.stiviewer.model.builder;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.types.indicator.IndicatorConfigEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.IndicatorEntity;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.IndicatorAccess;
import gr.cite.intelcomp.stiviewer.model.builder.indicator.IndicatorConfigBuilder;
import gr.cite.intelcomp.stiviewer.query.IndicatorAccessQuery;
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
public class IndicatorBuilder extends BaseBuilder<Indicator, IndicatorEntity> {

    private final QueryFactory queryFactory;
    private final BuilderFactory builderFactory;
    private final JsonHandlingService jsonHandlingService;
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public IndicatorBuilder(
            ConventionService conventionService,
            QueryFactory queryFactory, BuilderFactory builderFactory, JsonHandlingService jsonHandlingService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(IndicatorBuilder.class)));
        this.queryFactory = queryFactory;
        this.builderFactory = builderFactory;
        this.jsonHandlingService = jsonHandlingService;
    }

    public IndicatorBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<Indicator> build(FieldSet fields, List<IndicatorEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();

        List<Indicator> models = new ArrayList<>(100);

        FieldSet indicatorAccessesFields = fields.extractPrefixed(this.asPrefix(Indicator._indicatorAccesses));
        Map<UUID, List<IndicatorAccess>> indicatorAccessesMap = this.collectIndicatorAccesses(indicatorAccessesFields, data);
        // TODO make it in bulk
        FieldSet accessRequestConfigFields = fields.extractPrefixed(this.asPrefix(Indicator._config));

        for (IndicatorEntity d : data) {
            Indicator m = new Indicator();
            if (fields.hasField(this.asIndexer(Indicator._id)))
                m.setId(d.getId());
            if (fields.hasField(this.asIndexer(Indicator._code)))
                m.setCode(d.getCode());
            if (fields.hasField(this.asIndexer(Indicator._name)))
                m.setName(d.getName());
            if (fields.hasField(this.asIndexer(Indicator._description)))
                m.setDescription(d.getDescription());
            if (!accessRequestConfigFields.isEmpty() && d.getConfig() != null) {
                IndicatorConfigEntity accessRequestConfigEntity = this.jsonHandlingService.fromJsonSafe(IndicatorConfigEntity.class, d.getConfig());
                if (accessRequestConfigEntity != null)
                    m.setConfig(this.builderFactory.builder(IndicatorConfigBuilder.class).authorize(this.authorize).build(accessRequestConfigFields, accessRequestConfigEntity));
            }
            if (fields.hasField(this.asIndexer(Indicator._createdAt)))
                m.setCreatedAt(d.getCreatedAt());
            if (fields.hasField(this.asIndexer(Indicator._updatedAt)))
                m.setUpdatedAt(d.getUpdatedAt());
            if (fields.hasField(this.asIndexer(Indicator._isActive)))
                m.setIsActive(d.getIsActive());
            if (fields.hasField(this.asIndexer(Indicator._hash)))
                m.setHash(this.hashValue(d.getUpdatedAt()));
            if (!indicatorAccessesFields.isEmpty() && indicatorAccessesMap != null && indicatorAccessesMap.containsKey(d.getId()))
                m.setIndicatorAccesses(indicatorAccessesMap.get(d.getId()));
            models.add(m);
        }
        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
        return models;
    }

    private Map<UUID, List<IndicatorAccess>> collectIndicatorAccesses(FieldSet fields, List<IndicatorEntity> data) throws MyApplicationException {
        if (fields.isEmpty() || data.isEmpty()) return null;
        this.logger.debug("checking related - {}", IndicatorAccess.class.getSimpleName());

        Map<UUID, List<IndicatorAccess>> itemMap;
        FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(this.asIndexer(IndicatorAccess._indicator, Indicator._id));
        IndicatorAccessQuery query = this.queryFactory.query(IndicatorAccessQuery.class).authorize(this.authorize).indicatorIds(data.stream().map(IndicatorEntity::getId).distinct().collect(Collectors.toList()));
        itemMap = this.builderFactory.builder(IndicatorAccessBuilder.class).authorize(this.authorize).authorize(this.authorize).asMasterKey(query, clone, x -> x.getIndicator().getId());

        if (!fields.hasField(this.asIndexer(IndicatorAccess._indicator, Indicator._id))) {
            itemMap.forEach((id, indicators) -> {
                indicators.forEach(indicatorAccess -> {
                    if (indicatorAccess != null && indicatorAccess.getIndicator() != null)
                        indicatorAccess.getIndicator().setId(null);
                });
            });
        }
        return itemMap;
    }

}
