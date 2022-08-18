package gr.cite.intelcomp.stiviewer.model.builder.dataaccessrequest;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.types.dataaccessrequest.DataAccessRequestConfigEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequestConfig;
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
public class DataAccessRequestConfigBuilder extends BaseBuilder<DataAccessRequestConfig, DataAccessRequestConfigEntity> {

	private final QueryFactory queryFactory;
	private final BuilderFactory builderFactory;
	private final JsonHandlingService jsonHandlingService;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	@Autowired
	public DataAccessRequestConfigBuilder(
			ConventionService conventionService,
			QueryFactory queryFactory, BuilderFactory builderFactory, JsonHandlingService jsonHandlingService) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(DataAccessRequestConfigBuilder.class)));
		this.queryFactory = queryFactory;
		this.builderFactory = builderFactory;
		this.jsonHandlingService = jsonHandlingService;
	}

	public DataAccessRequestConfigBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequestConfig> build(FieldSet fields, List<DataAccessRequestConfigEntity> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();


		List<gr.cite.intelcomp.stiviewer.model.dataaccessrequest.DataAccessRequestConfig> models = new ArrayList<>();

		// TODO make it in bulk
		FieldSet indicatorFields = fields.extractPrefixed(this.asPrefix(DataAccessRequestConfig._indicators));
		FieldSet indicatorFieldGroups = fields.extractPrefixed(this.asPrefix(DataAccessRequestConfig._indicatorGroups));

		for (DataAccessRequestConfigEntity d : data) {
			DataAccessRequestConfig m = new DataAccessRequestConfig();
			if (!indicatorFields.isEmpty() && d.getIndicators() != null) m.setIndicators(this.builderFactory.builder(DataAccessRequestIndicatorConfigBuilder.class).authorize(this.authorize).build(indicatorFields, d.getIndicators()));
			if (!indicatorFieldGroups.isEmpty() && d.getIndicatorGroups() != null) m.setIndicatorGroups(this.builderFactory.builder(DataAccessRequestIndicatorGroupConfigBuilder.class).authorize(this.authorize).build(indicatorFieldGroups, d.getIndicatorGroups()));
			models.add(m);
		}

		this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
		return models;
	}

}
