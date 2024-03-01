package gr.cite.intelcomp.stiviewer.model.builder.portofolioconfig;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.portofolioconfig.DataFieldEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.portofolioconfig.DataField;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
//Like in C# make it transient
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataFieldBuilder extends BaseBuilder<DataField, DataFieldEntity> {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(DataFieldBuilder.class));

	private final BuilderFactory builderFactory;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	@Autowired
	public DataFieldBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
		super(conventionService, logger);
		this.builderFactory = builderFactory;
	}

	public DataFieldBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<DataField> build(FieldSet fields, List<DataFieldEntity> datas) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} BrowseDataFields", Optional.ofNullable(datas).map(e -> e.size()).orElse(0), Optional.ofNullable(fields).map(e -> e.getFields()).map(e -> e.size()).orElse(0));
		this.logger.trace(new DataLogEntry("requested BrowseDataFields", fields));
		if (fields == null || fields.isEmpty()) return new ArrayList<>();

		List<DataField> models = new LinkedList<>();
		for (DataFieldEntity d : datas) {
			DataField m = new DataField();
			if (fields.hasField(this.asIndexer(DataField._code))) m.setCode(d.getCode());
			if (fields.hasField(this.asIndexer(DataField._name))) m.setName(d.getName());
			models.add(m);
		}

		return models;
	}
}
