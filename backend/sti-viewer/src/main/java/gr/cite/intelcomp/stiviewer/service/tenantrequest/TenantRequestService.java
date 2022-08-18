package gr.cite.intelcomp.stiviewer.service.tenantrequest;

import gr.cite.intelcomp.stiviewer.model.TenantRequest;
import gr.cite.intelcomp.stiviewer.model.persist.TenantRequestPersist;
import gr.cite.intelcomp.stiviewer.model.persist.TenantRequestStatusPersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;

public interface TenantRequestService {
	TenantRequest persist(TenantRequestPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;

	TenantRequest persist(TenantRequestStatusPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;
	//void deleteAndSave(UUID id) throws MyForbiddenException;
}
