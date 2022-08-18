package gr.cite.user.service.user.contactinfo;

import gr.cite.user.data.UserContactInfoCompositeKey;
import gr.cite.user.model.UserContactInfo;
import gr.cite.user.model.persist.UserContactInfoPersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.util.List;

public interface UserContactInfoService {
	UserContactInfo persist(UserContactInfoPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;

	void batchPersist(List<UserContactInfoPersist> model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;

	void deleteAndSave(List<UserContactInfoPersist> model) throws MyForbiddenException, InvalidApplicationException;

}
