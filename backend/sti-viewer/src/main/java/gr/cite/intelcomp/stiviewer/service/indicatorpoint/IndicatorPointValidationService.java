package gr.cite.intelcomp.stiviewer.service.indicatorpoint;

import gr.cite.intelcomp.stiviewer.common.enums.IndicatorFieldBaseType;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.FieldEntity;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.ValidationEntity;

import java.util.List;
import java.util.Map;

public interface IndicatorPointValidationService {
    void validateModel();
    void  validateField(List<ValidationEntity> validations, Map.Entry<String, Object> field, IndicatorFieldBaseType baseType);
    void checkIfMissingRequiredFields(Map<String, Object> modelProps, Map<String, FieldEntity> configProps);
}
