package gr.cite.intelcomp.stiviewer.service.notification;

import gr.cite.intelcomp.stiviewer.model.notification.Notification;
import gr.cite.intelcomp.stiviewer.model.persist.notification.NotificationPersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;

public interface NotificationService{

    Notification persist(NotificationPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;

}
