package gr.cite.intelcomp.stiviewer.service.user.invitation;

import gr.cite.intelcomp.stiviewer.model.UserInvitation;
import gr.cite.intelcomp.stiviewer.model.persist.UserInvitationPersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;

public interface UserInvitationService {
	UserInvitation persist(UserInvitationPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;
}
