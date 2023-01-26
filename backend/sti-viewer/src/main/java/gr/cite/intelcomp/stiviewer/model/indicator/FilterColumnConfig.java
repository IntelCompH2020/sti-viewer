package gr.cite.intelcomp.stiviewer.model.indicator;

import java.util.HashSet;

public class FilterColumnConfig {

    public static final String _code = "code";
    private String code;
    
    public static final String _dependsOnCode = "dependsOnCode";
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
