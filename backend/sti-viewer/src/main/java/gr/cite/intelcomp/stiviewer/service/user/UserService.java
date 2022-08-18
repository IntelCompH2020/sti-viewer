package gr.cite.intelcomp.stiviewer.service.user;

import gr.cite.intelcomp.stiviewer.model.User;
import gr.cite.intelcomp.stiviewer.model.persist.UserPersist;
import gr.cite.intelcomp.stiviewer.model.persist.UserTouchedIntegrationEventPersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.util.UUID;

public interface UserService {
	User persist(UserPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;

	User persist(UserTouchedIntegrationEventPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;

	void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException;
}
