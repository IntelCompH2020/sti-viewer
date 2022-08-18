package gr.cite.notification.service.notification;

import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.model.Notification;
import gr.cite.notification.model.SendNotificationResult;
import gr.cite.notification.model.persist.NotificationPersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.util.UUID;

public interface NotificationService{

    Notification persist(NotificationPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;
    void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException;
    SendNotificationResult doNotify(NotificationEntity notification);
}
