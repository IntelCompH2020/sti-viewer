package gr.cite.intelcomp.stiviewer.elastic.converter;

import gr.cite.intelcomp.stiviewer.common.enums.IndicatorFieldBaseType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StringToIndicatorFieldBaseTypeConverter implements Converter<String, IndicatorFieldBaseType> {
	@Override
	public IndicatorFieldBaseType convert(String source) {
		return IndicatorFieldBaseType.fromString(source);
	}
}
