package gr.cite.intelcomp.stiviewer.elastic.converter;


import gr.cite.intelcomp.stiviewer.common.enums.IndicatorPointValidationType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;


@ReadingConverter
public class StringToIndicatorPointValidationTypeConverter implements Converter<String,IndicatorPointValidationType> {

    @Override
    public IndicatorPointValidationType convert(String source) {
        return IndicatorPointValidationType.fromString(source);
    }
}
