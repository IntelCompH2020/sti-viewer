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
	public List<DetailItem> build(FieldSet fields, List<DetailItemEntity> datas) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(datas).map(e -> e.size()).orElse(0), Optional.ofNullable(fields).map(e -> e.getFields()).map(e -> e.size()).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || fields.isEmpty()) return new ArrayList<>();

		FieldSet masterItemFields = fields.extractPrefixed(this.asPrefix(DetailItem._master));
		Map<UUID, MasterItem> masterItemMap = this.collectMasterItems(masterItemFields, datas);

		List<DetailItem> models = new ArrayList<>();

		for (DetailItemEntity d : datas) {
			DetailItem m = new DetailItem();
			if (fields.hasField(this.asIndexer(DetailItem._id))) m.setId(d.getId());
			if (fields.hasField(this.asIndexer(DetailItem._name))) m.setName(d.getName());
			if (fields.hasField(this.asIndexer(DetailItem._hash))) m.setHash(this.hashValue(d.getUpdatedAt()));
			if (fields.hasField(this.asIndexer(DetailItem._createdAt))) m.setCreatedAt(d.getCreatedAt());
			if (fields.hasField(this.asIndexer(DetailItem._updatedAt))) m.setUpdatedAt(d.getUpdatedAt());
			if (fields.hasField(this.asIndexer(DetailItem._isActive))) m.setIsActive(d.getIsActive());
			if (!masterItemFields.isEmpty() && masterItemMap != null && masterItemMap.containsKey(d.getMasterId())) m.setMaster(masterItemMap.get(d.getMasterId()));
			models.add(m);
		}
		this.logger.debug("build {} items", Optional.ofNullable(models).map(e -> e.size()).orElse(0));
		return models;
	}

	private Map<UUID, MasterItem> collectMasterItems(FieldSet fields, List<DetailItemEntity> datas) throws MyApplicationException {
		if (fields.isEmpty() || datas.isEmpty()) return null;
		this.logger.debug("checking related - {}", MasterItem.class.getSimpleName());

		Map<UUID, MasterItem> itemMap = null;
		if (!fields.hasOtherField(this.asIndexer(MasterItem._id))) {
			itemMap = this.asEmpty(
					datas.stream().map(x -> x.getMasterId()).distinct().collect(Collectors.toList()),
					x -> {
						MasterItem item = new MasterItem();
						item.setId(x);
						return item;
					},
					x -> x.getId());
		} else {
			FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(MasterItem._id);
			MasterItemQuery q = this.queryFactory.query(MasterItemQuery.class).ids(datas.stream().map(x -> x.getMasterId()).distinct().collect(Collectors.toList()));
			itemMap = this.builderFactory.builder(MasterItemBuilder.class).asForeignKey(q, clone, x -> x.getId());
		}
		if (!fields.hasField(MasterItem._id)) {
			itemMap.values().stream().filter(x -> x != null).map(x -> {
				x.setId(null);
				return x;
			}).collect(Collectors.toList());
		}

		return itemMap;
	}
}
