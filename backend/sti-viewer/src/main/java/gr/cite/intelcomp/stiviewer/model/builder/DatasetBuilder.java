package gr.cite.intelcomp.stiviewer.model.builder;

import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.DatasetEntity;
import gr.cite.intelcomp.stiviewer.model.Dataset;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DatasetBuilder extends BaseBuilder<Dataset, DatasetEntity> {

	@Autowired
	public DatasetBuilder(
			ConventionService conventionService
	) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(DatasetBuilder.class)));
	}

	@Override
	public List<Dataset> build(FieldSet fields, List<DatasetEntity> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || fields.isEmpty()) return new ArrayList<>();

		List<Dataset> models = new ArrayList<>();

		if (data == null)
			return models;
		for (DatasetEntity d : data) {
			Dataset m = new Dataset();
			if (fields.hasField(this.asIndexer(Dataset._id)))
				m.setId(d.getId());
			if (fields.hasField(this.asIndexer(Dataset._hash)))
				m.setHash(this.hashValue(d.getUpdatedAt()));
			if (fields.hasField(this.asIndexer(Dataset._title)))
				m.setTitle(d.getTitle());
			if (fields.hasField(this.asIndexer(Dataset._isActive)))
				m.setIsActive(d.getIsActive());
			if (fields.hasField(this.asIndexer(Dataset._notes)))
				m.setNotes(d.getNotes());
			if (fields.hasField(this.asIndexer(Dataset._createdAt)))
				m.setCreatedAt(d.getCreatedAt());
			if (fields.hasField(this.asIndexer(Dataset._updatedAt)))
				m.setUpdatedAt(d.getUpdatedAt());
			models.add(m);
		}
		this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
		return models;
	}
}
