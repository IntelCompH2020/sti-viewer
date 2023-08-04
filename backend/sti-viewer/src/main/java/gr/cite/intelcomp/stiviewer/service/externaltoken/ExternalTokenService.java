package gr.cite.intelcomp.stiviewer.service.externaltoken;

import com.fasterxml.jackson.core.JsonProcessingException;
import gr.cite.intelcomp.stiviewer.common.types.externaltoken.DefinitionEntity;
import gr.cite.intelcomp.stiviewer.data.ExternalTokenEntity;
import gr.cite.intelcomp.stiviewer.model.ExternalToken;
import gr.cite.intelcomp.stiviewer.model.ExternalTokenCreateResponse;
import gr.cite.intelcomp.stiviewer.model.persist.externaltoken.ExternalTokenChangePersist;
import gr.cite.intelcomp.stiviewer.model.persist.externaltoken.ExternalTokenExpirationPersist;
import gr.cite.intelcomp.stiviewer.model.persist.externaltoken.IndicatorPointReportExternalTokenPersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import javax.naming.OperationNotSupportedException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public interface ExternalTokenService {
	ExternalToken persist(ExternalTokenExpirationPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException, OperationNotSupportedException;
	String persist(ExternalTokenChangePersist model) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException, OperationNotSupportedException, NoSuchAlgorithmException;
	ExternalTokenCreateResponse persist(IndicatorPointReportExternalTokenPersist model) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException, OperationNotSupportedException, JsonProcessingException, NoSuchAlgorithmException;
	void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException, OperationNotSupportedException;
    DefinitionEntity getValidDefintionForce(String token) throws NoSuchAlgorithmException, OperationNotSupportedException;
	ExternalTokenEntity getValidForce(String token) throws NoSuchAlgorithmException, OperationNotSupportedException;
}
