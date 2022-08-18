package gr.cite.intelcomp.stiviewer.service.user.contactinfo;

import gr.cite.intelcomp.stiviewer.model.UserContactInfo;
import gr.cite.intelcomp.stiviewer.model.persist.UserContactInfoPersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;

public interface UserContactInfoService {
	UserContactInfo persist(UserContactInfoPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;

	void deleteAndSave(UserContactInfoPersist.ID id) throws MyForbiddenException, InvalidApplicationException;
}
