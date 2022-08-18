package gr.cite.notification.service.user.invitation;

import gr.cite.notification.model.UserInvitation;
import gr.cite.notification.model.persist.UserInvitationPersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;

public interface UserInvitationService {
	UserInvitation persist(UserInvitationPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;
}
