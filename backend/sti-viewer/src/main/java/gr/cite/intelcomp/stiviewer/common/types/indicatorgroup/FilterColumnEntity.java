package gr.cite.intelcomp.stiviewer.common.types.indicatorgroup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashSet;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterColumnEntity {
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
