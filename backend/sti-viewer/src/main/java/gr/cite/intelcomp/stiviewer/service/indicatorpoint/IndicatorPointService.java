package gr.cite.intelcomp.stiviewer.service.indicatorpoint;

import gr.cite.intelcomp.stiviewer.model.elasticreport.AggregateResponseModel;
import gr.cite.intelcomp.stiviewer.model.elasticreport.IndicatorPointReportLookup;
import gr.cite.intelcomp.stiviewer.model.indicatorpoint.IndicatorPoint;
import gr.cite.intelcomp.stiviewer.model.persist.indicatorpoint.IndicatorPointPersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface IndicatorPointService {
	IndicatorPoint persist(UUID indicatorId, IndicatorPointPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;

	AggregateResponseModel report(UUID indicatorId, IndicatorPointReportLookup model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;

	byte[] export(UUID indicatorId, IndicatorPointReportLookup lookup) throws InvalidApplicationException, IOException;

	void persist(UUID indicatorId, List<IndicatorPointPersist> models) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;
}
