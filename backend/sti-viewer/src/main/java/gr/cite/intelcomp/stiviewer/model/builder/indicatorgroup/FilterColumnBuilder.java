package gr.cite.intelcomp.stiviewer.model.builder.indicatorgroup;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.FilterColumnEntity;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.indicatorgroup.FilterColumn;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
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
public class FilterColumnBuilder extends BaseBuilder<FilterColumn, FilterColumnEntity> {

	private final QueryFactory queryFactory;
	private final BuilderFactory builderFactory;
	private final ConventionService conventionService;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public FilterColumnBuilder(ConventionService conventionService,
	                           QueryFactory queryFactory,
	                           BuilderFactory builderFactory, ConventionService conventionService1) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(FilterColumnBuilder.class)));
		this.queryFactory = queryFactory;
		this.builderFactory = builderFactory;
		this.conventionService = conventionService1;
	}

	public FilterColumnBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public List<FilterColumn> build(FieldSet fields, List<FilterColumnEntity> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));
		if (fields == null || data == null || fields.isEmpty()) return new ArrayList<>();
		List<FilterColumn> models = new ArrayList<>();
		for (FilterColumnEntity d : data) {
			FilterColumn m = new FilterColumn();
			if (fields.hasField(this.asIndexer(FilterColumn._code)) && !conventionService.isNullOrEmpty(d.getCode())) m.setCode(d.getCode());
			if (fields.hasField(this.asIndexer(FilterColumn._dependsOnCode)) && !conventionService.isNullOrEmpty(d.getDependsOnCode())) m.setDependsOnCode(d.getDependsOnCode());
			models.add(m);
		}
		this.logger.debug("build {} items", Optional.of(models).map(List::size).orElse(0));
		return models;
	}

}
