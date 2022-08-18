package gr.cite.intelcomp.stiviewer.model.builder.elasticreport;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.elasticreport.AggregateResponseItemModel;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.elastic.query.Aggregation.AggregateResponseItem;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AggregateResponseItemModelBuilder extends BaseBuilder<AggregateResponseItemModel, AggregateResponseItem> {

	private final BuilderFactory builderFactory;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public AggregateResponseItemModelBuilder(
			ConventionService conventionService,
			BuilderFactory builderFactory
	) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(AggregateResponseItemModelBuilder.class)));
		this.builderFactory = builderFactory;
	}

	public AggregateResponseItemModelBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<AggregateResponseItemModel> build(FieldSet fields, List<AggregateResponseItem> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));

		List<AggregateResponseItemModel> models = new ArrayList<>();

		for (AggregateResponseItem d : data) {
			AggregateResponseItemModel m = new AggregateResponseItemModel();
			m.setGroup(this.builderFactory.builder(AggregateResponseGroupModelBuilder.class).authorize(this.authorize).build(null, d.getGroup()));
			m.setValues(this.builderFactory.builder(AggregateResponseValueModelBuilder.class).authorize(this.authorize).build(null, d.getValues()));
			models.add(m);
		}
		return models;
	}
}
