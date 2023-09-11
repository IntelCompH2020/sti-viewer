package gr.cite.intelcomp.stiviewer.model.builder;

import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.DetailItemEntity;
import gr.cite.intelcomp.stiviewer.model.DetailItem;
import gr.cite.intelcomp.stiviewer.model.MasterItem;
import gr.cite.intelcomp.stiviewer.query.MasterItemQuery;
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
public class DetailItemBuilder extends BaseBuilder<DetailItem, DetailItemEntity> {

    private final BuilderFactory builderFactory;
    private final QueryFactory queryFactory;

    @Autowired
    public DetailItemBuilder(
            ConventionService conventionService,
            BuilderFactory builderFactory,
            QueryFactory queryFactory
    ) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DetailItemBuilder.class)));
        this.builderFactory = builderFactory;
        this.queryFactory = queryFactory;
    }

    @Override
    public List<DetailItem> build(FieldSet fields, List<DetailItemEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || fields.isEmpty())
            return new ArrayList<>();

        FieldSet masterItemFields = fields.extractPrefixed(this.asPrefix(DetailItem._master));
        Map<UUID, MasterItem> masterItemMap = this.collectMasterItems(masterItemFields, data);

        List<DetailItem> models = new ArrayList<>(100);

        if (data == null)
            return models;
        for (DetailItemEntity d : data) {
            DetailItem m = new DetailItem();
            if (fields.hasField(this.asIndexer(DetailItem._id)))
                m.setId(d.getId());
            if (fields.hasField(this.asIndexer(DetailItem._name)))
                m.setName(d.getName());
            if (fields.hasField(this.asIndexer(DetailItem._hash)))
                m.setHash(this.hashValue(d.getUpdatedAt()));
            if (fields.hasField(this.asIndexer(DetailItem._createdAt)))
                m.setCreatedAt(d.getCreatedAt());
            if (fields.hasField(this.asIndexer(DetailItem._updatedAt)))
                m.setUpdatedAt(d.getUpdatedAt());
            if (fields.hasField(this.asIndexer(DetailItem._isActive)))
                m.setIsActive(d.getIsActive());
            if (!masterItemFields.isEmpty() && masterItemMap != null && masterItemMap.containsKey(d.getMasterId()))
                m.setMaster(masterItemMap.get(d.getMasterId()));
            models.add(m);
        }
        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
        return models;
    }

    private Map<UUID, MasterItem> collectMasterItems(FieldSet fields, List<DetailItemEntity> datas) throws MyApplicationException {
        if (fields.isEmpty() || datas.isEmpty())
            return null;
        this.logger.debug("checking related - {}", MasterItem.class.getSimpleName());

        Map<UUID, MasterItem> itemMap;
        if (!fields.hasOtherField(this.asIndexer(MasterItem._id))) {
            itemMap = this.asEmpty(
                    datas.stream().map(DetailItemEntity::getMasterId).distinct().collect(Collectors.toList()),
                    x -> {
                        MasterItem item = new MasterItem();
                        item.setId(x);
                        return item;
                    },
                    MasterItem::getId);
        } else {
            FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(MasterItem._id);
            MasterItemQuery q = this.queryFactory.query(MasterItemQuery.class).ids(datas.stream().map(DetailItemEntity::getMasterId).distinct().collect(Collectors.toList()));
            itemMap = this.builderFactory.builder(MasterItemBuilder.class).asForeignKey(q, clone, MasterItem::getId);
        }
        if (!fields.hasField(MasterItem._id)) {
            itemMap.forEach((id, user) -> {
                if (user != null)
                    user.setId(null);
            });
        }

        return itemMap;
    }
}
