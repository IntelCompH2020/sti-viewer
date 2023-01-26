package gr.cite.intelcomp.stiviewer.service.dataaccessrequest;

import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupAccessConfigViewEntity;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupEntity;
import gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequest;
import gr.cite.intelcomp.stiviewer.model.persist.dataaccessrequest.DataAccessRequestPersist;
import gr.cite.intelcomp.stiviewer.model.persist.dataaccessrequest.DataAccessRequestStatusPersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;

public interface DataAccessRequestService {
	DataAccessRequest persist(DataAccessRequestPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;

	DataAccessRequest persist(DataAccessRequestStatusPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;
	IndicatorGroupAccessConfigViewEntity getIndicatorGroupAccessConfigViewEntity(String code);
}
