package gr.cite.intelcomp.stiviewer.model.builder.elasticreport;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.data.IndicatorEntity;
import gr.cite.intelcomp.stiviewer.data.TenantEntityManager;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.FieldEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.IndicatorPointEntity;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpoint.IndicatorPointQuery;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter.IndicatorPointIntegerFilter;
import gr.cite.intelcomp.stiviewer.elastic.query.indicatorpointfilter.IndicatorPointKeywordFilter;
import gr.cite.intelcomp.stiviewer.model.Indicator;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.elasticreport.AggregateResponseGroupModel;
import gr.cite.intelcomp.stiviewer.model.elasticreport.IndicatorPointReportLookup;
import gr.cite.intelcomp.stiviewer.query.IndicatorQuery;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorConfigItem;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorConfigService;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.query.Aggregation.AggregateResponseGroup;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import javax.management.InvalidApplicationException;
import java.util.*;
import java.util.stream.Stream;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AggregateResponseGroupModelBuilder extends BaseBuilder<AggregateResponseGroupModel, AggregateResponseGroup> {

	private final TenantEntityManager entityManager;
	private final QueryFactory queryFactory;
	private final MessageSource messageSource;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);
	private final IndicatorConfigService indicatorConfigService;

	private Map<String, Map<String, String>> groupItemLabels;

	public AggregateResponseGroupModelBuilder(
			ConventionService conventionService,
			QueryFactory queryFactory,
			MessageSource messageSource,
			TenantEntityManager entityManager,
			IndicatorConfigService indicatorConfigService
	) {
		super(conventionService, new LoggerService(LoggerFactory.getLogger(AggregateResponseGroupModelBuilder.class)));
		this.queryFactory = queryFactory;
		this.entityManager = entityManager;
		this.messageSource = messageSource;
		this.indicatorConfigService = indicatorConfigService;
	}

	public AggregateResponseGroupModelBuilder authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	public AggregateResponseGroupModelBuilder groupItemLabels(Map<String, Map<String, String>>  groupItemLabels) {
		this.groupItemLabels = groupItemLabels;
		return this;
	}
	@Override
	public List<AggregateResponseGroupModel> build(FieldSet fields, List<AggregateResponseGroup> data) throws MyApplicationException {
		this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
		this.logger.trace(new DataLogEntry("requested fields", fields));

		List<AggregateResponseGroupModel> models = new ArrayList<>();

		for (AggregateResponseGroup d : data) {
			AggregateResponseGroupModel m = new AggregateResponseGroupModel();
			m.setItems(d.getItems());
			if (d.getItems() != null && this.groupItemLabels != null){
				Map<String, String> labels = new HashMap<>();
				for (String key: d.getItems().keySet()) {
					Map<String, String> mapping = this.groupItemLabels.get(key);
					if (mapping != null){
						String displayValue = mapping.get(d.getItems().get(key));
						if (!this.conventionService.isNullOrEmpty(displayValue)) labels.put(key, displayValue);
					}
				}
				if (!labels.isEmpty()) m.setItemLabels(labels);
			}
			models.add(m);
		}
		return models;
	}
}
