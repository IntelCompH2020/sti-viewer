package gr.cite.intelcomp.stiviewer.service.indicatorpoint;

import gr.cite.intelcomp.stiviewer.model.elasticreport.AggregateResponseModel;
import gr.cite.intelcomp.stiviewer.model.elasticreport.IndicatorPointReportLookup;
import gr.cite.intelcomp.stiviewer.model.shared.PublicIndicatorPointReportLookup;
import gr.cite.intelcomp.stiviewer.model.indicatorpoint.IndicatorPoint;
import gr.cite.intelcomp.stiviewer.model.persist.indicatorpoint.IndicatorPointPersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import javax.naming.OperationNotSupportedException;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

public interface IndicatorPointService {
	IndicatorPoint persist(UUID indicatorId, IndicatorPointPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException, IOException;

	AggregateResponseModel report(UUID indicatorId, IndicatorPointReportLookup model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;
	AggregateResponseModel reportPublic(@NotNull UUID indicatorId, @NotNull PublicIndicatorPointReportLookup lookup, FieldSet fields) throws InvalidApplicationException, NoSuchAlgorithmException, OperationNotSupportedException;
	byte[] exportXlsx(UUID indicatorId, IndicatorPointReportLookup lookup) throws InvalidApplicationException, IOException;
	byte[] exportJson(UUID indicatorId, IndicatorPointReportLookup lookup) throws InvalidApplicationException, UnsupportedEncodingException;
	void deleteBatch(UUID indicatorId, UUID batchId) throws InvalidApplicationException, IOException;
	void persist(UUID indicatorId, List<IndicatorPointPersist> models) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException, IOException;

	String getGlobalSearchConfig(String key) throws InvalidApplicationException;
}
