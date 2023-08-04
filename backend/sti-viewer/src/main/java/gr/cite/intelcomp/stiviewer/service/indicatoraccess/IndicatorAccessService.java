package gr.cite.intelcomp.stiviewer.service.indicatoraccess;

import gr.cite.intelcomp.stiviewer.model.IndicatorAccess;
import gr.cite.intelcomp.stiviewer.model.persist.IndicatorAccessPersist;
import gr.cite.intelcomp.stiviewer.model.persist.UserAddAccessToIndicatorColumn;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.util.UUID;

public interface IndicatorAccessService {
	IndicatorAccess persist(IndicatorAccessPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;
	IndicatorAccess persist(UserAddAccessToIndicatorColumn model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;
	void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException;
}
