package gr.cite.notification.service.tenant;

import gr.cite.notification.model.Tenant;
import gr.cite.notification.model.persist.TenantPersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.util.UUID;

public interface TenantService {
	Tenant persist(TenantPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;
	void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException;

}
