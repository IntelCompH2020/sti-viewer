package gr.cite.intelcomp.stiviewer.model.builder.indicatorgroup;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupAccessColumnConfigViewEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.indicatorgroup.IndicatorGroupAccessColumnConfigView;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
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
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IndicatorGroupAccessColumnConfigViewBuilder extends BaseBuilder<IndicatorGroupAccessColumnConfigView, IndicatorGroupAccessColumnConfigViewEntity> {

	private final QueryFactory queryFactory;
	private final BuilderFactory builderFactory;
	private final JsonHandlingService jsonHandlingService;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	@Autowired
	public IndicatorGroupAccessColumnConfigViewBuilder(
			ConventionService conventionService,
			QueryFactory queryFactory, BuilderFactory builderFactory, JsonHandlingService jsonHandlingService) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(IndicatorGroupAccessColumnConfigViewBuilder.class)));
		this.queryFactory = queryFactory;
		this.builderFactory = builderFactory;
		this.jsonHandlingService = jsonHandlingService;
	}

	public IndicatorGroupAccessColumnConfigViewBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<IndicatorGroupAccessColumnConfigView> build(FieldSet fields, List<IndicatorGroupAccessColumnConfigViewEntity> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();

		FieldSet itemsFields = fields.extractPrefixed(this.asPrefix(IndicatorGroupAccessColumnConfigView._items));

		List<IndicatorGroupAccessColumnConfigView> models = new ArrayList<>();
		for (IndicatorGroupAccessColumnConfigViewEntity d : data) {
			IndicatorGroupAccessColumnConfigView m = new IndicatorGroupAccessColumnConfigView();
			if (fields.hasField(this.asIndexer(IndicatorGroupAccessColumnConfigView._code))) m.setCode(d.getCode());
			if (!itemsFields.isEmpty() && d.getItems() != null) m.setItems(this.builderFactory.builder(IndicatorGroupAccessColumnConfigItemViewBuilder.class).authorize(this.authorize).build(itemsFields, d.getItems()));
			models.add(m);
		}

		this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
		return models;
	}

}
