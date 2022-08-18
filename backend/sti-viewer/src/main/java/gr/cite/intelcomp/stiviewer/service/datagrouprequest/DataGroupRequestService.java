package gr.cite.intelcomp.stiviewer.service.datagrouprequest;

import gr.cite.intelcomp.stiviewer.data.DataGroupRequestEntity;
import gr.cite.intelcomp.stiviewer.model.datagrouprequest.DataGroupRequest;
import gr.cite.intelcomp.stiviewer.model.persist.datagrouprequest.DataGroupRequestNamePersist;
import gr.cite.intelcomp.stiviewer.model.persist.datagrouprequest.DataGroupRequestPersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public interface DataGroupRequestService {
	DataGroupRequest persist(DataGroupRequestPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException, NoSuchAlgorithmException;

	DataGroupRequest persist(DataGroupRequestNamePersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException, NoSuchAlgorithmException;
	
	void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException;

	boolean buildGroup(DataGroupRequestEntity request) throws InvalidApplicationException;
}
