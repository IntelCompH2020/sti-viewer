package gr.cite.intelcomp.stiviewer.service.indicatorelastic;

import gr.cite.intelcomp.stiviewer.model.IndicatorElastic;
import gr.cite.intelcomp.stiviewer.model.persist.IndicatorElasticPersist;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.io.IOException;
import java.util.UUID;

public interface ElasticIndicatorService {

	IndicatorElastic persist(IndicatorElasticPersist persist, FieldSet fieldSet) throws IOException, InvalidApplicationException;

	String calculateIndexName(String code, boolean ensureUnique) throws IOException;

	String ensureIndex(UUID indicatorId) throws IOException, InvalidApplicationException;

	String getIndexName(UUID indicatorId) throws InvalidApplicationException, IOException;

	void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException, IOException;
}
