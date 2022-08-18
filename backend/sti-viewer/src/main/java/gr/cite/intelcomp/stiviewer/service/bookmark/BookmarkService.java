package gr.cite.intelcomp.stiviewer.service.bookmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import gr.cite.intelcomp.stiviewer.model.Bookmark;
import gr.cite.intelcomp.stiviewer.model.persist.GetBookmarkByHashParams;
import gr.cite.intelcomp.stiviewer.model.persist.MyBookmarkPersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.util.UUID;

public interface BookmarkService {
	Bookmark persistMine(MyBookmarkPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException, JsonProcessingException;

	Bookmark getBookmarkByHashMine(GetBookmarkByHashParams model, FieldSet fields) throws JsonProcessingException, InvalidApplicationException, MyForbiddenException;

	void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException;
}
