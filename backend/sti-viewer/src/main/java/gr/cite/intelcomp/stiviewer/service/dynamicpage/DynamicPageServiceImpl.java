package gr.cite.intelcomp.stiviewer.service.dynamicpage;

import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractor;
import gr.cite.intelcomp.stiviewer.authorization.Permission;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.DynamicPageType;
import gr.cite.intelcomp.stiviewer.common.enums.IsActive;
import gr.cite.intelcomp.stiviewer.common.enums.DynamicPageVisibility;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.common.types.dynamicpageconfig.DynamicPageConfigEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.DynamicPageContentEntity;
import gr.cite.intelcomp.stiviewer.data.DynamicPageEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.errorcode.ErrorThesaurusProperties;
import gr.cite.intelcomp.stiviewer.model.*;
import gr.cite.intelcomp.stiviewer.model.builder.DynamicPageBuilder;
import gr.cite.intelcomp.stiviewer.model.deleter.DynamicPageContentDeleter;
import gr.cite.intelcomp.stiviewer.model.deleter.DynamicPageDeleter;
import gr.cite.intelcomp.stiviewer.model.persist.*;
import gr.cite.intelcomp.stiviewer.query.DynamicPageContentQuery;
import gr.cite.intelcomp.stiviewer.query.DynamicPageQuery;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.deleter.DeleterFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequestScope
public class DynamicPageServiceImpl implements DynamicPageService {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(DynamicPageServiceImpl.class));

	private final TenantEntityManager entityManager;
	private final AuthorizationService authorizationService;
	private final DeleterFactory deleterFactory;
	private final BuilderFactory builderFactory;
	private final ConventionService conventionService;
	private final ErrorThesaurusProperties errors;
	private final MessageSource messageSource;
	private final UserScope userScope;
	private final QueryFactory queryFactory;
	private final JsonHandlingService jsonHandlingService;
	private final ClaimExtractor claimExtractor;
	private final CurrentPrincipalResolver currentPrincipalResolver;
	
	@Autowired
	public DynamicPageServiceImpl(
			TenantEntityManager entityManager,
			AuthorizationService authorizationService,
			DeleterFactory deleterFactory,
			BuilderFactory builderFactory,
			ConventionService conventionService,
			ErrorThesaurusProperties errors,
			MessageSource messageSource,
			UserScope userScope,
			QueryFactory queryFactory,
			JsonHandlingService jsonHandlingService,
			ClaimExtractor claimExtractor, 
			CurrentPrincipalResolver currentPrincipalResolver) {
		this.entityManager = entityManager;
		this.authorizationService = authorizationService;
		this.deleterFactory = deleterFactory;
		this.builderFactory = builderFactory;
		this.conventionService = conventionService;
		this.errors = errors;
		this.messageSource = messageSource;
		this.userScope = userScope;
		this.queryFactory = queryFactory;
		this.jsonHandlingService = jsonHandlingService;
		this.claimExtractor = claimExtractor;
		this.currentPrincipalResolver = currentPrincipalResolver;
	}

	public DynamicPage persist(PagePersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
		logger.debug(new MapLogEntry("persisting page").And("model", model).And("fields", fields));

		this.authorizationService.authorizeForce(Permission.EditDynamicPage);
		
		DynamicPageEntity data = this.patchAndSave(model);
		
		PagePageContentsPatch detailsPatch = new PagePageContentsPatch();
		detailsPatch.setId(data.getId());
		detailsPatch.setHash(this.conventionService.hashValue(data.getUpdatedAt()));
		detailsPatch.setPageContents(model.getPageContents().stream().map(x -> {
					x.setPageId(data.getId());
					return x;
				}
		).collect(Collectors.toList()));

		this.patchAndSave(Arrays.asList(new PagePageContentsPatch[]{detailsPatch}));
		
		this.entityManager.flush();

		DynamicPage persisted = this.builderFactory.builder(DynamicPageBuilder.class).build(BaseFieldSet.build(fields, DynamicPage._id, DynamicPage._hash), data);
		return persisted;
	}

	@Override
	public List<DynamicPageMenuItem> getAllowedPageMenuItems(String language) {
		List<DynamicPageEntity> datas = this.queryFactory.query(DynamicPageQuery.class).isActive(IsActive.ACTIVE).collect();
		List<DynamicPageEntity> allowedPages = new ArrayList<>();

		for (DynamicPageEntity data : datas) {
			if (this.canViewPage(data)) allowedPages.add(data);
		}
		List<DynamicPageContentEntity> pageContents = this.queryFactory.query(DynamicPageContentQuery.class).isActive(IsActive.ACTIVE).pageIds(allowedPages.stream().map(x -> x.getId()).distinct().collect(Collectors.toList())).
				languages(language).collectAs(new BaseFieldSet().ensure(DynamicPageContent._title).ensure(this.conventionService.asIndexer(DynamicPageContent._page, DynamicPage._id)));

		List<DynamicPageMenuItem> dynamicPageMenuItems = new ArrayList<>();
		for (DynamicPageEntity data : allowedPages.stream().sorted(Comparator.comparingInt(DynamicPageEntity::getOrder)).collect(Collectors.toList())) {
			DynamicPageContentEntity dynamicPageContentEntity = pageContents.stream().filter(x -> x.getPageId().equals(data.getId())).findFirst().orElse(null);
			if (dynamicPageContentEntity == null) dynamicPageContentEntity = this.queryFactory.query(DynamicPageContentQuery.class).isActive(IsActive.ACTIVE).pageIds(data.getId()).languages(data.getDefaultLanguage()).first();
			if (dynamicPageContentEntity == null) continue;
			DynamicPageMenuItem dynamicPageMenuItem = new DynamicPageMenuItem();
			dynamicPageMenuItem.setId(data.getId());
			dynamicPageMenuItem.setOrder(data.getOrder());
			dynamicPageMenuItem.setType(data.getType());
			dynamicPageMenuItem.setTitle(dynamicPageContentEntity.getTitle());
			DynamicPageConfigEntity dynamicPageConfigEntity = jsonHandlingService.fromJsonSafe(DynamicPageConfigEntity.class, data.getConfig());
			if (dynamicPageConfigEntity != null) {
				dynamicPageMenuItem.setExternalUrl(dynamicPageConfigEntity.getExternalUrl());
				dynamicPageMenuItem.setMatIcon(dynamicPageConfigEntity.getMatIcon());
			}
			dynamicPageMenuItems.add(dynamicPageMenuItem);
		}
		return dynamicPageMenuItems;
	}

	@Override
	public DynamicPageContentData getPageContent(PageContentRequest model) {
		logger.debug(new MapLogEntry("get page contents").And("model", model));
		DynamicPageEntity data = this.queryFactory.query(DynamicPageQuery.class).isActive(IsActive.ACTIVE).ids(model.getId()).first();
		if (data == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), DynamicPage.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		if (!this.canViewPage(data)) throw new MyForbiddenException("Access is denied");

		DynamicPageContentEntity dynamicPageContentEntity = this.queryFactory.query(DynamicPageContentQuery.class).isActive(IsActive.ACTIVE).pageIds(model.getId()).languages(model.getLanguage()).first();
		if (dynamicPageContentEntity == null) dynamicPageContentEntity = this.queryFactory.query(DynamicPageContentQuery.class).isActive(IsActive.ACTIVE).pageIds(model.getId()).languages(data.getDefaultLanguage()).first();
		if (dynamicPageContentEntity == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), DynamicPageContent.class.getSimpleName()}, LocaleContextHolder.getLocale()));

		DynamicPageContentData contentData = new DynamicPageContentData();
		contentData.setType(data.getType());
		contentData.setId(data.getId());
		contentData.setTitle(dynamicPageContentEntity.getTitle());
		contentData.setContent(dynamicPageContentEntity.getContent());
		
		return contentData;
	}
	
	private boolean canViewPage(DynamicPageEntity data){
		if (data.getVisibility().equals(DynamicPageVisibility.Authenticated)) return true;
		else if (data.getVisibility().equals(DynamicPageVisibility.Owner) && data.getCreatorId() != null && data.getCreatorId().equals(this.userScope.getUserIdSafe())) return true;
		else if (data.getVisibility().equals(DynamicPageVisibility.HasRole)) {
			DynamicPageConfigEntity dynamicPageConfigEntity = jsonHandlingService.fromJsonSafe(DynamicPageConfigEntity.class, data.getConfig());
			if (dynamicPageConfigEntity != null && dynamicPageConfigEntity.getAllowedRoles() != null && !dynamicPageConfigEntity.getAllowedRoles().isEmpty()) {
				List<String> roles = this.claimExtractor.roles(this.currentPrincipalResolver.currentPrincipal());
				if (roles != null && !roles.isEmpty()){
					for (String role : dynamicPageConfigEntity.getAllowedRoles()) {
						if (roles.contains(role)) return true;
					}
				}
			}
		}
		return false;
	}

	private DynamicPageEntity patchAndSave(PagePersist model) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {

		Boolean isUpdate = this.conventionService.isValidGuid(model.getId());

		DynamicPageEntity data = null;
		if (isUpdate) {
			data = this.entityManager.find(DynamicPageEntity.class, model.getId());
			if (data == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), DynamicPage.class.getSimpleName()}, LocaleContextHolder.getLocale()));
			if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(model.getHash())) throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
		} else {
			data = new DynamicPageEntity();
			data.setId(UUID.randomUUID());
			data.setCreatorId(this.userScope.getUserIdSafe());
			data.setIsActive(IsActive.ACTIVE);
			data.setCreatedAt(Instant.now());
		}

		data.setDefaultLanguage(model.getDefaultLanguage());
		data.setOrder(model.getOrder());
		data.setType(model.getType());
		data.setVisibility(model.getVisibility());
		data.setUpdatedAt(Instant.now());
		data.setConfig(jsonHandlingService.toJsonSafe(this.applyConfig(model)));

		if (isUpdate) this.entityManager.merge(data);
		else this.entityManager.persist(data);

		this.entityManager.flush();

		return data;
	}
	private void patchAndSave(List<PagePageContentsPatch> models) throws MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException {
		if (models == null || models.isEmpty()) return;
		List<UUID> pageIds = models.stream().filter(x -> this.conventionService.isValidGuid(x.getId())).map(x -> x.getId()).distinct().collect(Collectors.toList());

		List<DynamicPageEntity> pages = this.queryFactory.query(DynamicPageQuery.class).ids(pageIds)
				.collectAs(new BaseFieldSet().ensure(DynamicPage._id).ensure(DynamicPage._hash));
		Map<UUID, DynamicPageEntity> pagesLookup = pages.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));

		List<DynamicPageContentEntity> pageContents = this.queryFactory.query(DynamicPageContentQuery.class)
				.pageIds(pageIds).collect();
		Map<UUID, List<DynamicPageContentEntity>> pageContentsLookup = this.conventionService.toDictionaryOfList(pageContents, x -> x.getPageId());

		for (PagePageContentsPatch model : models) {
			if (!pagesLookup.containsKey(model.getId())) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), DynamicPage.class.getSimpleName()}, LocaleContextHolder.getLocale()));
			DynamicPageEntity page = pagesLookup.get(model.getId());
			if (page == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getId(), DynamicPage.class.getSimpleName()}, LocaleContextHolder.getLocale()));
			if (!this.conventionService.hashValue(page.getUpdatedAt()).equals(model.getHash())) throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());

			List<DynamicPageContentEntity> existingPageContents = null;
			if (pageContentsLookup.containsKey(model.getId())) existingPageContents = pageContentsLookup.get(model.getId());
			else existingPageContents = new ArrayList<>();

			List<UUID> updatedPageContentIds = model.getPageContents().stream().filter(x -> this.conventionService.isValidGuid(x.getId())).map(x -> x.getId()).distinct().collect(Collectors.toList());
			List<DynamicPageContentEntity> toDelete = existingPageContents.stream().filter(x -> !updatedPageContentIds.contains(x.getId())).collect(Collectors.toList());
			this.deleterFactory.deleter(DynamicPageContentDeleter.class).delete(toDelete);

			Map<UUID, DynamicPageContentEntity> existingPageContentsLookup = existingPageContents.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));

			for (PageContentPersist pageContent : model.getPageContents()) {
				Boolean isUpdate = this.conventionService.isValidGuid(pageContent.getId());

				DynamicPageContentEntity data = null;
				if (isUpdate) {
					if (!existingPageContentsLookup.containsKey(pageContent.getId())) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{pageContent.getId(), DynamicPageContent.class.getSimpleName()}, LocaleContextHolder.getLocale()));
					data = existingPageContentsLookup.get(pageContent.getId());
					if (data == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{pageContent.getId(), DynamicPageContent.class.getSimpleName()}, LocaleContextHolder.getLocale()));
					if (!this.conventionService.hashValue(data.getUpdatedAt()).equals(pageContent.getHash())) throw new MyValidationException(this.errors.getHashConflict().getCode(), this.errors.getHashConflict().getMessage());
				} else {
					data = new DynamicPageContentEntity();
					data.setId(UUID.randomUUID());
					data.setIsActive(IsActive.ACTIVE);
					data.setCreatedAt(Instant.now());
					data.setPageId(page.getId());
				}

				data.setLanguage(pageContent.getLanguage());
				data.setTitle(pageContent.getTitle());
				data.setContent(pageContent.getContent());

				data.setUpdatedAt(Instant.now());

				if (isUpdate) this.entityManager.merge(data);
				else this.entityManager.persist(data);
			}
		}
		this.entityManager.flush();
	}
	private DynamicPageConfigEntity applyConfig(PagePersist model){
		if (model.getConfig() == null) return null;
		DynamicPageConfigEntity entity = new DynamicPageConfigEntity();
		entity.setAllowedRoles(model.getConfig().getAllowedRoles());
		entity.setExternalUrl(model.getConfig().getExternalUrl());
		entity.setMatIcon(model.getConfig().getMatIcon());
		if (model.getType() == DynamicPageType.External && this.conventionService.isNullOrEmpty(entity.getExternalUrl())){
			throw new MyValidationException(this.errors.getDynamicPageUrlRequired().getCode(), this.errors.getDynamicPageUrlRequired().getMessage());
		}
		return entity;
	}

	public void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException {
		logger.debug("deleting page: {}", id);

		this.authorizationService.authorizeForce(Permission.DeleteDynamicPage);

		this.deleterFactory.deleter(DynamicPageDeleter.class).deleteAndSaveByIds(Arrays.asList(new UUID[]{id}));
	}
}
