package gr.cite.notification.service.user.contactinfo;

import gr.cite.notification.model.UserContactInfo;
import gr.cite.notification.model.persist.UserContactInfoPersist;
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
