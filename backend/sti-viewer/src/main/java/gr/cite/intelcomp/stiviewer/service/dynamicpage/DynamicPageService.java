package gr.cite.intelcomp.stiviewer.service.dynamicpage;

import gr.cite.intelcomp.stiviewer.model.DynamicPage;
import gr.cite.intelcomp.stiviewer.model.DynamicPageContentData;
import gr.cite.intelcomp.stiviewer.model.PageContentRequest;
import gr.cite.intelcomp.stiviewer.model.DynamicPageMenuItem;
import gr.cite.intelcomp.stiviewer.model.persist.PagePersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.util.List;
import java.util.UUID;

public interface DynamicPageService {
	DynamicPage persist(PagePersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;
	List<DynamicPageMenuItem> getAllowedPageMenuItems(String language);
	DynamicPageContentData getPageContent(PageContentRequest language);
	void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException;
}
