package gr.cite.intelcomp.stiviewer.model.builder;

import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.MasterItemEntity;
import gr.cite.intelcomp.stiviewer.model.DetailItem;
import gr.cite.intelcomp.stiviewer.model.MasterItem;
import gr.cite.intelcomp.stiviewer.query.DetailItemQuery;
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
public class MasterItemBuilder extends BaseBuilder<MasterItem, MasterItemEntity> {

	private final BuilderFactory builderFactory;
	private final QueryFactory queryFactory;

	@Autowired
	public MasterItemBuilder(
			ConventionService conventionService,
			BuilderFactory builderFactory,
			QueryFactory queryFactory
	) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(DatasetBuilder.class)));
		this.builderFactory = builderFactory;
		this.queryFactory = queryFactory;
	}

	@Override
	public List<MasterItem> build(FieldSet fields, List<MasterItemEntity> datas) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(datas).map(e -> e.size()).orElse(0), Optional.ofNullable(fields).map(e -> e.getFields()).map(e -> e.size()).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || fields.isEmpty()) return new ArrayList<>();

		FieldSet detailItemFields = fields.extractPrefixed(this.asPrefix(MasterItem._details));
		Map<UUID, List<DetailItem>> detailItemsMap = this.collectDetailItems(detailItemFields, datas);

		List<MasterItem> models = new ArrayList<>();

		for (MasterItemEntity d : datas) {
			MasterItem m = new MasterItem();
			if (fields.hasField(this.asIndexer(MasterItem._id))) m.setId(d.getId());
			if (fields.hasField(this.asIndexer(MasterItem._hash))) m.setHash(this.hashValue(d.getUpdatedAt()));
			if (fields.hasField(this.asIndexer(MasterItem._name))) m.setName(d.getName());
			if (fields.hasField(this.asIndexer(MasterItem._createdAt))) m.setCreatedAt(d.getCreatedAt());
			if (fields.hasField(this.asIndexer(MasterItem._updatedAt))) m.setUpdatedAt(d.getUpdatedAt());
			if (fields.hasField(this.asIndexer(MasterItem._isActive))) m.setIsActive(d.getIsActive());
			if (!detailItemFields.isEmpty() && detailItemsMap.containsKey(d.getId())) m.setDetails(detailItemsMap.get(d.getId()));
			models.add(m);
		}
		this.logger.debug("build {} items", Optional.ofNullable(models).map(e -> e.size()).orElse(0));
		return models;
	}

	private Map<UUID, List<DetailItem>> collectDetailItems(FieldSet fields, List<MasterItemEntity> datas) throws MyApplicationException {
		if (fields.isEmpty() || datas.isEmpty()) return null;
		this.logger.debug("checking related - {}", DetailItem.class.getSimpleName());

		Map<UUID, List<DetailItem>> itemMap = null;
		FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(this.asIndexer(DetailItem._master, MasterItem._id));
		DetailItemQuery query = this.queryFactory.query(DetailItemQuery.class).masterItemIds(datas.stream().map(x -> x.getId()).distinct().collect(Collectors.toList()));
		itemMap = this.builderFactory.builder(DetailItemBuilder.class).asMasterKey(query, clone, x -> x.getMaster().getId());

		if (!fields.hasField(this.asIndexer(DetailItem._master, MasterItem._id))) {
			itemMap.values().stream().flatMap(List::stream).filter(x -> x != null && x.getMaster() != null).map(x -> {
				x.getMaster().setId(null);
				return x;
			}).collect(Collectors.toList());
		}
		return itemMap;
	}
}
