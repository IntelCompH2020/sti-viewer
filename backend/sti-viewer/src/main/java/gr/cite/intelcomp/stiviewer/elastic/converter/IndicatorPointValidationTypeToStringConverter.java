package gr.cite.intelcomp.stiviewer.elastic.converter;


import gr.cite.intelcomp.stiviewer.common.enums.IndicatorPointValidationType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class IndicatorPointValidationTypeToStringConverter implements Converter<IndicatorPointValidationType,String> {

    @Override
    public String convert(IndicatorPointValidationType source) {
        return source.getMappedName();
    }
}
