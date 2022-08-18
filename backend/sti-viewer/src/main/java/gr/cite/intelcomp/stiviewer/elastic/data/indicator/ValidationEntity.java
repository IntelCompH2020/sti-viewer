package gr.cite.intelcomp.stiviewer.elastic.data.indicator;

import gr.cite.intelcomp.stiviewer.common.enums.IndicatorPointValidationType;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.List;

public  class ValidationEntity {

    public static final class Fields {
        public static final String type = "type";
        public static final String stringValues = "stringValues";
        public static final String intValues = "intValues";
        public static final String dateValues = "dateValues";
        public static final String doubleValues = "doubleValues";
        public static final String minInt = "minInt";
        public static final String maxInt = "maxInt";
        public static final String minDate = "minDate";
        public static final String maxDate = "maxDate";
        public static final String minDouble = "minDouble";
        public static final String maxDouble = "maxDouble";
    }

    @Field(value = ValidationEntity.Fields.stringValues, type = FieldType.Keyword)
    private List<String> stringValues;
    @Field(value = ValidationEntity.Fields.intValues, type = FieldType.Integer)
    private List<Integer> intValues;
    @Field(value = ValidationEntity.Fields.dateValues, type = FieldType.Date)
    private List<Instant> dateValues;
    @Field(value = ValidationEntity.Fields.doubleValues, type = FieldType.Double)
    private List<Double> doubleValues;
    @Field(value = ValidationEntity.Fields.minInt, type = FieldType.Integer)
    private int minInt;
    @Field(value = ValidationEntity.Fields.maxInt, type = FieldType.Integer)
    private int maxInt;
    @Field(value = ValidationEntity.Fields.minDate, type = FieldType.Date)
    private Instant minDate;
    @Field(value = ValidationEntity.Fields.maxDate, type = FieldType.Date)
    private Instant maxDate;
    @Field(value = ValidationEntity.Fields.minDouble, type = FieldType.Double)
    private Double minDouble;
    @Field(value = ValidationEntity.Fields.maxDouble, type = FieldType.Double)
    private Double maxDouble;
    @Field(value = ValidationEntity.Fields.type, type = FieldType.Keyword)
    private IndicatorPointValidationType type;

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

    public int getMinInt() {
        return minInt;
    }

    public void setMinInt(int minInt) {
        this.minInt = minInt;
    }

    public int getMaxInt() {
        return maxInt;
    }

    public void setMaxInt(int maxInt) {
        this.maxInt = maxInt;
    }

    public Instant getMinDate() {
        return minDate;
    }

    public void setMinDate(Instant minDate) {
        this.minDate = minDate;
    }

    public Instant getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Instant maxDate) {
        this.maxDate = maxDate;
    }

    public Double getMinDouble() {
        return minDouble;
    }

    public void setMinDouble(Double minDouble) {
        this.minDouble = minDouble;
    }

    public Double getMaxDouble() {
        return maxDouble;
    }

    public void setMaxDouble(Double maxDouble) {
        this.maxDouble = maxDouble;
    }

    public IndicatorPointValidationType getType() {
        return type;
    }

    public void setType(IndicatorPointValidationType type) {
        this.type = type;
    }
}
