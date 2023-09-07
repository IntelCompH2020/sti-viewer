package gr.cite.intelcomp.stiviewer.model.builder;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.types.dynamicpageconfig.DynamicPageConfigEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.DynamicPageEntity;
import gr.cite.intelcomp.stiviewer.model.DynamicPage;
import gr.cite.intelcomp.stiviewer.model.DynamicPageContent;
import gr.cite.intelcomp.stiviewer.model.User;
import gr.cite.intelcomp.stiviewer.query.DynamicPageContentQuery;
import gr.cite.intelcomp.stiviewer.query.UserQuery;
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
public class DynamicPageBuilder extends BaseBuilder<DynamicPage, DynamicPageEntity> {

    private final QueryFactory queryFactory;
    private final BuilderFactory builderFactory;
    private final JsonHandlingService jsonHandlingService;
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public DynamicPageBuilder(
            ConventionService conventionService,
            QueryFactory queryFactory, BuilderFactory builderFactory, JsonHandlingService jsonHandlingService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DynamicPageBuilder.class)));
        this.queryFactory = queryFactory;
        this.builderFactory = builderFactory;
        this.jsonHandlingService = jsonHandlingService;
    }

    public DynamicPageBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<DynamicPage> build(FieldSet fields, List<DynamicPageEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || data == null || fields.isEmpty())
            return new ArrayList<>();

        List<DynamicPage> models = new ArrayList<>(100);

        FieldSet creatorFields = fields.extractPrefixed(this.asPrefix(DynamicPage._creator));
        Map<UUID, User> creatorItemsMap = this.collectCreators(creatorFields, data);

        FieldSet dynamicPageContentsFields = fields.extractPrefixed(this.asPrefix(DynamicPage._pageContents));
        Map<UUID, List<DynamicPageContent>> dynamicPageContentsMap = this.collectPageContents(dynamicPageContentsFields, data);
        // TODO make it in bulk
        FieldSet dynamicPageConfigFields = fields.extractPrefixed(this.asPrefix(DynamicPage._config));

        for (DynamicPageEntity d : data) {
            DynamicPage m = new DynamicPage();
            if (fields.hasField(this.asIndexer(DynamicPage._id)))
                m.setId(d.getId());
            if (fields.hasField(this.asIndexer(DynamicPage._visibility)))
                m.setVisibility(d.getVisibility());
            if (fields.hasField(this.asIndexer(DynamicPage._order)))
                m.setOrder(d.getOrder());
            if (fields.hasField(this.asIndexer(DynamicPage._type)))
                m.setType(d.getType());
            if (fields.hasField(this.asIndexer(DynamicPage._defaultLanguage)))
                m.setDefaultLanguage(d.getDefaultLanguage());
            if (!dynamicPageConfigFields.isEmpty() && d.getConfig() != null) {
                DynamicPageConfigEntity dynamicPageConfigEntity = this.jsonHandlingService.fromJsonSafe(DynamicPageConfigEntity.class, d.getConfig());
                if (dynamicPageConfigEntity != null)
                    m.setConfig(this.builderFactory.builder(DynamicPageConfigBuilder.class).authorize(this.authorize).build(dynamicPageConfigFields, dynamicPageConfigEntity));
            }
            if (fields.hasField(this.asIndexer(DynamicPage._createdAt)))
                m.setCreatedAt(d.getCreatedAt());
            if (fields.hasField(this.asIndexer(DynamicPage._updatedAt)))
                m.setUpdatedAt(d.getUpdatedAt());
            if (fields.hasField(this.asIndexer(DynamicPage._isActive)))
                m.setIsActive(d.getIsActive());
            if (fields.hasField(this.asIndexer(DynamicPage._hash)))
                m.setHash(this.hashValue(d.getUpdatedAt()));
            if (!creatorFields.isEmpty() && creatorItemsMap != null && d.getCreatorId() != null && creatorItemsMap.containsKey(d.getCreatorId()))
                m.setCreator(creatorItemsMap.get(d.getCreatorId()));
            if (!dynamicPageContentsFields.isEmpty() && dynamicPageContentsMap != null && dynamicPageContentsMap.containsKey(d.getId()))
                m.setPageContents(dynamicPageContentsMap.get(d.getId()));
            models.add(m);
        }
        this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
        return models;
    }


    private Map<UUID, User> collectCreators(FieldSet fields, List<DynamicPageEntity> data) throws MyApplicationException {
        if (fields.isEmpty() || data.isEmpty()) return null;
        this.logger.debug("checking related - {}", User.class.getSimpleName());

        Map<UUID, User> itemMap;
        if (!fields.hasOtherField(this.asIndexer(User._id))) {
            itemMap = this.asEmpty(
                    data.stream().map(DynamicPageEntity::getCreatorId).filter(Objects::nonNull).distinct().collect(Collectors.toList()),
                    x -> {
                        User item = new User();
                        item.setId(x);
                        return item;
                    },
                    User::getId);
        } else {
            FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(User._id);
            UserQuery q = this.queryFactory.query(UserQuery.class).authorize(this.authorize).ids(data.stream().map(DynamicPageEntity::getCreatorId).filter(Objects::nonNull).distinct().collect(Collectors.toList()));
            itemMap = this.builderFactory.builder(UserBuilder.class).authorize(this.authorize).asForeignKey(q, clone, User::getId);
        }
        if (!fields.hasField(User._id)) {
            itemMap.forEach((id, user) -> {
                if (user != null)
                    user.setId(null);
            });
        }

        return itemMap;
    }

    private Map<UUID, List<DynamicPageContent>> collectPageContents(FieldSet fields, List<DynamicPageEntity> data) throws MyApplicationException {
        if (fields.isEmpty() || data.isEmpty())
            return null;
        this.logger.debug("checking related - {}", DynamicPageContent.class.getSimpleName());

        Map<UUID, List<DynamicPageContent>> itemMap;
        FieldSet clone = new BaseFieldSet(fields.getFields()).ensure(this.asIndexer(DynamicPageContent._page, DynamicPage._id));
        DynamicPageContentQuery query = this.queryFactory.query(DynamicPageContentQuery.class).authorize(this.authorize).pageIds(data.stream().map(DynamicPageEntity::getId).distinct().collect(Collectors.toList()));
        itemMap = this.builderFactory.builder(DynamicPageContentBuilder.class).authorize(this.authorize).authorize(this.authorize).asMasterKey(query, clone, x -> x.getPage().getId());

        if (!fields.hasField(this.asIndexer(DynamicPageContent._page, DynamicPage._id))) {
            itemMap.forEach((id, items) -> {
                items.forEach(item -> {
                    if (item != null && item.getPage() != null)
                        item.getPage().setId(null);
                });
            });
        }
        return itemMap;
    }

}
