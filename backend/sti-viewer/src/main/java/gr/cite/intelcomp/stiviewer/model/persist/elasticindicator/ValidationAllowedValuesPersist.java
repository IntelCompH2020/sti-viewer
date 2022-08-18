package gr.cite.intelcomp.stiviewer.model.persist.elasticindicator;

import com.fasterxml.jackson.annotation.JsonTypeName;
import gr.cite.intelcomp.stiviewer.common.enums.IndicatorPointValidationType;

import java.time.Instant;
import java.util.List;

@JsonTypeName("allowed_values")
public class ValidationAllowedValuesPersist extends BaseValidationPersist {

    private List<String> stringValues;
    private List<Integer> intValues;
    private List<Instant> dateValues;
    private List<Double> doubleValues;

    public ValidationAllowedValuesPersist(IndicatorPointValidationType type,
                                          List<String> stringValues,
                                          List<Integer> intValues,
                                          List<Instant> dateValues,
                                          List<Double> doubleValues) {
        super(type);
        this.stringValues = stringValues;
        this.intValues = intValues;
        this.dateValues = dateValues;
        this.doubleValues = doubleValues;
    }

    public List<String> getStringValues() {
        return stringValues;
    }

    public void setStringValues(List<String> stringValues) {
        this.stringValues = stringValues;
    }

    public List<Integer> getIntValues() {
        return intValues;
    }

    public void setIntValues(List<Integer> intValues) {
        this.intValues = intValues;
    }

    public List<Instant> getDateValues() {
        return dateValues;
    }

    public void setDateValues(List<Instant> dateValues) {
        this.dateValues = dateValues;
    }

    public List<Double> getDoubleValues() {
        return doubleValues;
    }

    public void setDoubleValues(List<Double> doubleValues) {
        this.doubleValues = doubleValues;
    }
}
