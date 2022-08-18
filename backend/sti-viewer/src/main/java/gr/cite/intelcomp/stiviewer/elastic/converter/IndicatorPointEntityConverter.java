package gr.cite.intelcomp.stiviewer.elastic.converter;

import gr.cite.intelcomp.stiviewer.elastic.data.indicator.FieldEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicatorpoint.IndicatorPointEntity;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorConfigItem;
import gr.cite.intelcomp.stiviewer.service.indicator.IndicatorConfigService;
import gr.cite.tools.exception.MyApplicationException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.LinkedHashMap;
import java.util.Map;

@WritingConverter
public class IndicatorPointEntityConverter implements Converter<IndicatorPointEntity, Map<String, Object>> {
	private final ApplicationContext applicationContext;

	public IndicatorPointEntityConverter(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public Map<String, Object> convert(IndicatorPointEntity source) {
		Map<String, Object> target = new LinkedHashMap<>();
		if (source.getId() != null) target.put(IndicatorPointEntity.Fields.id, source.getId());
		if (source.getBatchId() != null) target.put(IndicatorPointEntity.Fields.batchId, source.getBatchId());
		if (source.getBatchTimestamp() != null) target.put(IndicatorPointEntity.Fields.batchTimestamp, source.getBatchTimestamp());
		if (source.getTimestamp() != null) target.put(IndicatorPointEntity.Fields.timestamp, source.getTimestamp());
		if (source.getGroupHash() != null) target.put(IndicatorPointEntity.Fields.groupHash, source.getGroupHash());
		if (source.getGroupInfo() != null) target.put(IndicatorPointEntity.Fields.groupInfo, source.getGroupInfo());
		IndicatorConfigService indicatorConfigService = this.applicationContext.getBean(IndicatorConfigService.class);
		IndicatorConfigItem item = indicatorConfigService.getConfig(source.getIndicatorId());
		if (item != null && source.getProperties() != null) {
			for (Map.Entry<String, Object> prop : source.getProperties().entrySet()) {
				FieldEntity fieldEntity = item.getExtraProps().getOrDefault(prop.getKey(), null);

				if (prop.getKey() != null && fieldEntity != null) {
					switch (fieldEntity.getBaseType()) {
						case String:
						case Keyword:
						case Integer:
						case IntegerArray:
						case DoubleArray:
						case KeywordArray:
						case Double:
						case Date:
						case IntegerMap:
						case DoubleMap: {
							target.put(indicatorConfigService.ensurePropertyName(prop.getKey()), prop.getValue());
							break;
						}
						default:
							throw new MyApplicationException("invalid type " + fieldEntity.getBaseType());
					}
				}
			}
		}

		return target;
	}
}
