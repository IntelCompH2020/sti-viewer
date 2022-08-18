package gr.cite.intelcomp.stiviewer.elastic.converter;

import gr.cite.intelcomp.stiviewer.common.enums.IndicatorFieldBaseType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class IndicatorFieldBaseTypeToStringConverter implements Converter<IndicatorFieldBaseType, String> {
	@Override
	public String convert(IndicatorFieldBaseType source) {
		return source.asString();
	}
}
