package gr.cite.intelcomp.stiviewer.service.masteritem;

import gr.cite.intelcomp.stiviewer.model.MasterItem;
import gr.cite.intelcomp.stiviewer.model.persist.MasterItemPersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.util.UUID;

public interface MasterItemService {
	MasterItem persist(MasterItemPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;

	void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException;
}
