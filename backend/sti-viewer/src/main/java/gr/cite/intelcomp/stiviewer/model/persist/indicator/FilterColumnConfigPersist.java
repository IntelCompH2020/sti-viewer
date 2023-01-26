package gr.cite.intelcomp.stiviewer.model.persist.indicator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class FilterColumnConfigPersist {
    @NotNull(message = "{validation.empty}")
    @NotEmpty(message = "{validation.empty}")
    @Size(max = 200, message = "{validation.largerthanmax}")
    private String code;
    private String dependsOnCode;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDependsOnCode() {
        return dependsOnCode;
    }

    public void setDependsOnCode(String dependsOnCode) {
        this.dependsOnCode = dependsOnCode;
    }
}
