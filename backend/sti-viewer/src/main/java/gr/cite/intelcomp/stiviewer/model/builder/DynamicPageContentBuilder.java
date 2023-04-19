package gr.cite.intelcomp.stiviewer.model.builder;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.DynamicPageContentEntity;
import gr.cite.intelcomp.stiviewer.model.DynamicPage;
import gr.cite.intelcomp.stiviewer.model.DynamicPageContent;
import gr.cite.intelcomp.stiviewer.query.DynamicPageQuery;
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
public class DynamicPageContentBuilder extends BaseBuilder<DynamicPageContent, DynamicPageContentEntity> {

	private final QueryFactory queryFactory;
	private final BuilderFactory builderFactory;
	private final JsonHandlingService jsonHandlingService;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	@Autowired
	public DynamicPageContentBuilder(
			ConventionService conventionService,
			QueryFactory queryFactory, BuilderFactory builderFactory, JsonHandlingService jsonHandlingService) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(DynamicPageContentBuilder.class)));
		this.queryFactory = queryFactory;
		this.builderFactory = builderFactory;
		this.jsonHandlingService = jsonHandlingService;
	}

	public DynamicPageContentBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<DynamicPageContent> build(FieldSet fields, List<DynamicPageContentEntity> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();

		FieldSet dynamicPageFields = fields.extractPrefixed(this.asPrefix(DynamicPageContent._page));
		Map<UUID, DynamicPage> dynamicPageItemsMap = this.collectPages(dynamicPageFields, data);

		List<DynamicPageContent> models = new ArrayList<>();

		for (DynamicPageContentEntity d : data) {
			DynamicPageContent m = new DynamicPageContent();
			if (fields.hasField(this.asIndexer(DynamicPageContent._id))) m.setId(d.getId());
			if (fields.hasField(this.asIndexer(DynamicPageContent._isActive))) m.setIsActive(d.getIsActive());
			if (fields.hasField(this.asIndexer(DynamicPageContent._language))) m.setLanguage(d.getLanguage());
			if (fields.hasField(this.asIndexer(DynamicPageContent._title))) m.setTitle(d.getTitle());
			if (fields.hasField(this.asIndexer(DynamicPageContent._content))) m.setContent(d.getContent());
			if (fields.hasField(this.asIndexer(DynamicPageContent._createdAt))) m.setCreatedAt(d.getCreatedAt());
			if (fields.hasField(this.asIndexer(DynamicPageContent._updatedAt))) m.setUpdatedAt(d.getUpdatedAt());
			if (fields.hasField(this.asIndexer(DynamicPageContent._hash))) m.setHash(this.hashValue(d.getUpdatedAt()));
			if (!dynamicPageFields.isEmpty() && dynamicPageItemsMap != null && dynamicPageItemsMap.containsKey(d.getPageId())) m.setPage(dynamicPageItemsMap.get(d.getPageId()));
			models.add(m);
		}
		this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
		return models;
	}

	private Map<UUID, DynamicPage> collectPages(FieldSet fields, List<DynamicPageContentEntity> data) throws MyApplicationException {
		if (fields.isEmpty() || data.isEmpty()) return null;
		this.logger.debug("checking related - {}", DynamicPage.class.getSimpleName());

		Map<UUID, DynamicPage> itemMap;
		if (!fields.hasOtherField(this.asIndexer(DynamicPage._id))) {
			itemMap = this.asEmpty(
					data.stream().map(x -> x.getPageId()).distinct().collect(Collectors.toList()),
					x -> {
						DynamicPage item = new DynamicPage();
						item.setId(x);
						return item;
					},
					DynamicPage::getId);
		} else {
			FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(DynamicPage._id);
			DynamicPageQuery q = this.queryFactory.query(DynamicPageQuery.class).authorize(this.authorize).ids(data.stream().map(x -> x.getPageId()).distinct().collect(Collectors.toList()));
			itemMap = this.builderFactory.builder(DynamicPageBuilder.class).authorize(this.authorize).asForeignKey(q, clone, DynamicPage::getId);
		}
		if (!fields.hasField(DynamicPage._id)) {
			itemMap.values().stream().filter(Objects::nonNull).peek(x -> x.setId(null)).collect(Collectors.toList());
		}

		return itemMap;
	}

}
