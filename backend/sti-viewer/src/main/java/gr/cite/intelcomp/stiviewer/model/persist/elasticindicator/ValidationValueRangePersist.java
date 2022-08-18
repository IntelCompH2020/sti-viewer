package gr.cite.intelcomp.stiviewer.model.persist.elasticindicator;

import com.fasterxml.jackson.annotation.JsonTypeName;
import gr.cite.intelcomp.stiviewer.common.enums.IndicatorPointValidationType;

import java.time.Instant;

@JsonTypeName("value_range")
public class ValidationValueRangePersist extends BaseValidationPersist {

  private int minInt;
  private int maxInt;
  private Instant minDate;
  private Instant maxDate;
  private Double minDouble;
  private Double maxDouble;

    public ValidationValueRangePersist(IndicatorPointValidationType type,
                                       int minInt,
                                       int maxInt,
                                       Instant minDate,
                                       Instant maxDate,
                                       Double minDouble,
                                       Double maxDouble) {
        super(type);
        this.minInt = minInt;
        this.maxInt = maxInt;
        this.minDate = minDate;
        this.maxDate = maxDate;
        this.minDouble = minDouble;
        this.maxDouble = maxDouble;
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

}
