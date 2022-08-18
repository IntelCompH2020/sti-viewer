package gr.cite.notification.model.builder.datatreeconfig;

import gr.cite.notification.authorization.AuthorizationFlags;
import gr.cite.notification.common.types.datatreeconfig.BrowseDataField;
import gr.cite.notification.convention.ConventionService;
import gr.cite.notification.model.builder.BaseBuilder;
import gr.cite.notification.model.datatreeconfig.BrowseDataFieldModel;
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
public class BrowseDataFieldBuilder extends BaseBuilder<BrowseDataFieldModel, BrowseDataField> {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(BrowseDataFieldBuilder.class));

	private final BuilderFactory builderFactory;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	@Autowired
	public BrowseDataFieldBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
		super(conventionService, logger);
		this.builderFactory = builderFactory;
	}

	public BrowseDataFieldBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<BrowseDataFieldModel> build(FieldSet fields, List<BrowseDataField> datas) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} BrowseDataFields", Optional.ofNullable(datas).map(e -> e.size()).orElse(0), Optional.ofNullable(fields).map(e -> e.getFields()).map(e -> e.size()).orElse(0));
		this.logger.trace(new DataLogEntry("requested BrowseDataFields", fields));
		if (fields == null || fields.isEmpty()) return new ArrayList<>();

		List<BrowseDataFieldModel> models = new LinkedList<>();
		for (BrowseDataField d : datas) {
			BrowseDataFieldModel m = new BrowseDataFieldModel();
			if (fields.hasField(this.asIndexer(BrowseDataFieldModel._code))) m.setCode(d.getCode());
			if (fields.hasField(this.asIndexer(BrowseDataFieldModel._name))) m.setName(d.getName());
			models.add(m);
		}

		return models;
	}
}
