package gr.cite.intelcomp.stiviewer.service.dataset;

import gr.cite.intelcomp.stiviewer.model.Dataset;
import gr.cite.intelcomp.stiviewer.model.persist.DatasetPersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.util.UUID;

public interface DatasetService {
	Dataset persist(DatasetPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;

	void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException;
}
